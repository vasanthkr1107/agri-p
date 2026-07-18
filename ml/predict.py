import json
from pathlib import Path
from typing import Dict, List, Tuple, Union

import numpy as np
import tensorflow as tf
from PIL import Image

# Pairs that are often confused on RGB leaf images (same crop, similar lesions).
# Raw keys must match folder names in PlantVillage / class_indices.json.
_AMBIGUOUS_PAIRS = frozenset(
    frozenset(pair)
    for pair in (
        ("Tomato___Bacterial_spot", "Tomato___Septoria_leaf_spot"),
        ("Tomato___Bacterial_spot", "Tomato___Target_Spot"),
    )
)


def load_class_mappings(class_indices_path: Union[str, Path]) -> Dict[int, str]:
    with open(class_indices_path, "r", encoding="utf-8") as f:
        payload = json.load(f)

    if "index_to_class_name" in payload:
        idx_to_name = {int(k): v for k, v in payload["index_to_class_name"].items()}
        return idx_to_name

    # Backward-compat: class_name_to_index only
    name_to_idx = payload.get("class_name_to_index", payload)
    return {int(v): k for k, v in name_to_idx.items()}


def preprocess_pil_image(img: Image.Image) -> np.ndarray:
    img = img.convert("RGB")
    img = img.resize((224, 224))
    arr = np.asarray(img, dtype=np.float32) / 255.0
    arr = np.expand_dims(arr, axis=0)  # (1,224,224,3)
    return arr


def predict_image(
    image: Union[str, Path, Image.Image],
    model_path: Union[str, Path] = "plant_disease_model.h5",
    class_indices_path: Union[str, Path] = "class_indices.json",
) -> Tuple[str, float]:
    model = tf.keras.models.load_model(model_path)
    idx_to_name = load_class_mappings(class_indices_path)

    if isinstance(image, (str, Path)):
        img = Image.open(image)
    else:
        img = image

    x = preprocess_pil_image(img)
    probs = model.predict(x, verbose=0)[0]
    idx = int(np.argmax(probs))
    label = idx_to_name.get(idx, str(idx))
    return label, float(probs[idx])


def format_label(label: str) -> dict:
    # PlantVillage labels are often like "Tomato___Late_blight"
    parts = label.split("___")
    if len(parts) == 2:
        plant_name = parts[0].replace("_", " ").strip()
        disease_name = parts[1].replace("_", " ").strip()
    else:
        plant_name = "Unknown"
        disease_name = label.replace("_", " ").strip()
    return {"plant_name": plant_name, "disease_name": disease_name}

def top_k_from_probs(
    probs: np.ndarray, idx_to_name: Dict[int, str], k: int = 5
) -> List[Dict[str, Union[str, float]]]:
    k = min(k, len(probs))
    top_idx = np.argsort(probs)[-k:][::-1]
    out: List[Dict[str, Union[str, float]]] = []
    for i in top_idx:
        raw = idx_to_name.get(int(i), str(int(i)))
        formatted = format_label(raw)
        out.append(
            {
                "plant_name": formatted["plant_name"],
                "disease_name": formatted["disease_name"],
                "raw_class": raw,
                "confidence": float(probs[i]),
            }
        )
    return out


def is_ambiguous_spot_pair(
    raw_top1: str, raw_top2: str, p1: float, p2: float, margin: float = 0.15
) -> bool:
    """True when the model is unsure or when top-2 are a known visually similar pair."""
    if p1 - p2 < margin:
        return True
    pair = frozenset((raw_top1, raw_top2))
    return pair in _AMBIGUOUS_PAIRS


def build_prediction_result(probs: np.ndarray, idx_to_name: Dict[int, str]) -> Dict[str, Union[str, float, bool, list]]:
    idx = int(np.argmax(probs))
    confidence = float(probs[idx])
    label = idx_to_name.get(idx, str(idx))
    formatted = format_label(label)
    top = top_k_from_probs(probs, idx_to_name, k=5)
    raw_top2 = top[1]["raw_class"] if len(top) > 1 else label
    p2 = float(top[1]["confidence"]) if len(top) > 1 else 0.0
    ambiguous = is_ambiguous_spot_pair(label, raw_top2, confidence, p2)
    note = (
        "Top classes are close; bacterial spot and leaf spot often look alike in photos. "
        "Consider lab testing or expert inspection if treatment depends on the exact disease."
        if ambiguous
        else None
    )
    return {
        "plant_name": formatted["plant_name"],
        "disease_name": formatted["disease_name"],
        "confidence": confidence,
        "top_predictions": [
            {
                "plant_name": t["plant_name"],
                "disease_name": t["disease_name"],
                "confidence": t["confidence"]
            } for t in top
        ],
        "ambiguous": ambiguous,
        "note": note,
    }


if __name__ == "__main__":
    import argparse
    import json as _json

    parser = argparse.ArgumentParser()
    parser.add_argument("--image", required=True)
    parser.add_argument("--model", default="plant_disease_model.h5")
    parser.add_argument("--classes", default="class_indices.json")
    args = parser.parse_args()

    model = tf.keras.models.load_model(args.model)
    idx_to_name = load_class_mappings(args.classes)
    img = Image.open(args.image)
    x = preprocess_pil_image(img)
    probs = model.predict(x, verbose=0)[0]
    out = build_prediction_result(probs, idx_to_name)
    out["confidence"] = round(float(out["confidence"]), 6)
    for row in out["top_predictions"]:
        row["confidence"] = round(float(row["confidence"]), 6)
    if out.get("note") is None:
        del out["note"]
    print(_json.dumps(out, ensure_ascii=False))


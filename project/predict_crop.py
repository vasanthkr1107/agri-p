"""
Predict crop from full agronomic inputs.
Accepts: nitrogen, phosphorus, potassium, temperature, humidity, rainfall, ph, soil_type, budget
Returns: predicted_crop, confidence, reason, estimated_cost, expected_profit
"""

from __future__ import annotations

import json
import re
from pathlib import Path
from typing import Any

import joblib
import numpy as np
import pandas as pd

MODEL_PATH = Path(__file__).resolve().parent / "crop_model.pkl"

# Feature order must match training
NUMERIC_FEATURES = ["N", "P", "K", "temperature", "humidity", "ph", "rainfall"]

# Cost estimates per crop per acre (INR) — used when DB enrichment unavailable
CROP_COST_MAP: dict[str, str] = {
    "rice": "₹18,500 per acre", "maize": "₹16,000 per acre",
    "chickpea": "₹12,500 per acre", "kidneybeans": "₹14,000 per acre",
    "pigeonpeas": "₹13,500 per acre", "mothbeans": "₹11,000 per acre",
    "mungbean": "₹12,000 per acre", "blackgram": "₹11,800 per acre",
    "lentil": "₹13,000 per acre", "pomegranate": "₹1,20,000 per acre",
    "banana": "₹95,000 per acre", "mango": "₹85,000 per acre",
    "grapes": "₹1,80,000 per acre", "watermelon": "₹78,000 per acre",
    "muskmelon": "₹72,000 per acre", "apple": "₹2,50,000 per acre",
    "orange": "₹1,40,000 per acre", "papaya": "₹95,000 per acre",
    "coconut": "₹45,000 per acre", "cotton": "₹55,000 per acre",
    "jute": "₹42,000 per acre", "coffee": "₹1,80,000 per acre",
    "wheat": "₹15,000 per acre", "sugarcane": "₹35,000 per acre",
}

# Profit label per crop
CROP_PROFIT_MAP: dict[str, str] = {
    "rice": "Medium", "maize": "Medium", "wheat": "Medium",
    "chickpea": "Medium", "lentil": "Medium", "blackgram": "Medium",
    "mungbean": "Medium", "mothbeans": "Low", "pigeonpeas": "Medium",
    "kidneybeans": "Medium", "jute": "Low", "cotton": "High",
    "sugarcane": "High", "pomegranate": "High", "banana": "High",
    "mango": "High", "grapes": "High", "apple": "High",
    "orange": "High", "papaya": "Medium", "coconut": "High",
    "coffee": "High", "watermelon": "Medium", "muskmelon": "Medium",
}

# Reason templates keyed by dominant feature
REASON_TEMPLATES = [
    # (feature_index, threshold, comparator, reason_snippet)
    (2, 120, ">", "high potassium levels boost root development"),
    (0, 80, ">", "nitrogen-rich soil promotes lush vegetative growth"),
    (1, 80, ">", "high phosphorus supports strong flowering and fruiting"),
    (4, 80, ">", "high humidity suits moisture-loving crops"),
    (4, 30, "<", "low humidity matches drought-tolerant crop requirements"),
    (5, 200, ">", "abundant rainfall provides natural irrigation"),
    (5, 50, "<", "low rainfall aligns with water-efficient crop needs"),
    (3, 30, ">", "warm temperature accelerates crop maturation"),
    (3, 15, "<", "cool temperatures favour this cold-season crop"),
    (6, 6.5, ">", "slightly alkaline pH suits this crop's nutrient uptake"),
    (6, 6.0, "<", "mildly acidic soil pH is optimal for this crop"),
]


_bundle = None


def load_bundle() -> dict:
    """Load the model bundle (model + encoders) from disk."""
    global _bundle
    if _bundle is None:
        if not MODEL_PATH.exists():
            raise FileNotFoundError(
                f"Model not found at {MODEL_PATH}. "
                "The application should auto-train on startup."
            )
        _bundle = joblib.load(MODEL_PATH)
        # Support old-format model (just the classifier object)
        if not isinstance(_bundle, dict):
            _bundle = {"model": _bundle, "soil_encoder": None, "budget_encoder": None}
    return _bundle


def _normalize(s: str) -> str:
    return re.sub(r"\s+", " ", s.strip().lower())


def _encode_soil(soil_type: str, soil_enc) -> int:
    """Encode soil_type; fallback to 1 (Loamy index) on unknown."""
    if soil_enc is None:
        return 1
    key = soil_type.strip().title()
    try:
        return int(soil_enc.transform([key])[0])
    except ValueError:
        # Try case-insensitive match
        for cls in soil_enc.classes_:
            if cls.lower() == key.lower():
                return int(soil_enc.transform([cls])[0])
        return 1  # default Loamy


# our budget calculator using "Random Forest"
def _encode_budget(budget: str, budget_enc) -> int:
    """Encode budget level; default Medium=1."""
    if budget_enc is None:
        return 1
    key = budget.strip().title()
    try:
        return int(budget_enc.transform([key])[0])
    except ValueError:
        return 1


def _build_reason(crop: str, features: list[float], soil_type: str, budget: str) -> str:
    """Generate a human-readable explanation based on dominant features."""
    reasons = []
    # N, P, K, temperature, humidity, ph, rainfall
    for feat_idx, threshold, comparator, snippet in REASON_TEMPLATES:
        val = features[feat_idx]
        if comparator == ">" and val > threshold:
            reasons.append(snippet)
        elif comparator == "<" and val < threshold:
            reasons.append(snippet)
        if len(reasons) >= 2:
            break

    crop_title = crop.strip().capitalize()
    soil_note = f"{soil_type} soil" if soil_type else "the given soil"
    reason_parts = ", ".join(reasons) if reasons else "the provided soil and climate conditions"
    return (
        f"{crop_title} is recommended because {reason_parts} "
        f"make {soil_note} ideal for its cultivation. "
        f"A {budget.lower() if budget else 'medium'} budget allocation "
        f"is appropriate for this crop."
    )


def predict_crop_from_dict(payload: dict[str, Any]) -> dict[str, Any]:
    """
    Accept full agronomic payload and return prediction with enrichment.

    Required keys: nitrogen, phosphorus, potassium, temperature, humidity, rainfall
    Optional keys: ph (default 6.5), soil_type (default 'Loamy'), budget (default 'Medium')
    """
    # Extract inputs
    nitrogen = float(payload.get("nitrogen", 0))
    phosphorus = float(payload.get("phosphorus", 0))
    potassium = float(payload.get("potassium", 0))
    temperature = float(payload.get("temperature", 25))
    humidity = float(payload.get("humidity", 60))
    rainfall = float(payload.get("rainfall", 100))
    ph = float(payload.get("ph", 6.5))
    soil_type = str(payload.get("soil_type", "Loamy"))
    budget = str(payload.get("budget", "Medium"))

    bundle = load_bundle()
    model = bundle["model"]
    soil_enc = bundle.get("soil_encoder")
    budget_enc = bundle.get("budget_encoder")

    soil_encoded = _encode_soil(soil_type, soil_enc)
    budget_encoded = _encode_budget(budget, budget_enc)

    features = [nitrogen, phosphorus, potassium, temperature, humidity, ph, rainfall]
    row = features + [soil_encoded, budget_encoded]

    feature_names = NUMERIC_FEATURES + ["soil_encoded", "budget_encoded"]
    X = pd.DataFrame([row], columns=feature_names)

    proba = model.predict_proba(X)[0]
    idx = int(np.argmax(proba))
    raw_label = str(model.classes_[idx])
    confidence = float(round(float(proba[idx]), 4))

    crop_key = raw_label.strip().lower()
    estimated_cost = CROP_COST_MAP.get(crop_key, "Contact local agri office for estimate")
    expected_profit = CROP_PROFIT_MAP.get(crop_key, "Medium")
    reason = _build_reason(raw_label, features, soil_type, budget)

    # Format crop name nicely
    predicted_crop = raw_label.strip()
    predicted_crop = predicted_crop[0].upper() + predicted_crop[1:].lower() if predicted_crop else predicted_crop

    return {
        "predicted_crop": predicted_crop,
        "confidence": confidence,
        "reason": reason,
        "estimated_cost": estimated_cost,
        "expected_profit": expected_profit,
    }


if __name__ == "__main__":
    import sys
    example = {
        "nitrogen": 90, "phosphorus": 42, "potassium": 43,
        "temperature": 25, "humidity": 80, "rainfall": 200,
        "ph": 6.5, "soil_type": "Clay", "budget": "Medium",
    }
    raw = json.loads(sys.argv[1]) if len(sys.argv) > 1 else example
    out = predict_crop_from_dict(raw)
    print(json.dumps(out, ensure_ascii=False, indent=2))

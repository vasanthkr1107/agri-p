from io import BytesIO
from pathlib import Path
from typing import Optional

import tensorflow as tf
from fastapi import FastAPI, File, UploadFile
from fastapi.middleware.cors import CORSMiddleware
from PIL import Image

from predict import build_prediction_result, load_class_mappings, preprocess_pil_image


class Predictor:
    def __init__(self, model_path: str, class_indices_path: str):
        self.model_path = model_path
        self.class_indices_path = class_indices_path
        self._model: Optional[tf.keras.Model] = None
        self._idx_to_name = None

    def load(self) -> None:
        if self._model is None:
            self._model = tf.keras.models.load_model(self.model_path)
        if self._idx_to_name is None:
            self._idx_to_name = load_class_mappings(self.class_indices_path)

    def predict(self, img: Image.Image):
        self.load()
        x = preprocess_pil_image(img)
        probs = self._model.predict(x, verbose=0)[0]
        return build_prediction_result(probs, self._idx_to_name)


MODEL_PATH = str(Path(__file__).with_name("plant_disease_model.h5"))
CLASSES_PATH = str(Path(__file__).with_name("class_indices.json"))

predictor = Predictor(MODEL_PATH, CLASSES_PATH)

app = FastAPI(title="PlantVillage Crop Disease Predictor", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/predict")
async def predict(image: UploadFile = File(...)):
    content = await image.read()
    img = Image.open(BytesIO(content))
    out = predictor.predict(img)
    out["confidence"] = round(out["confidence"], 6)
    for row in out["top_predictions"]:
        row["confidence"] = round(float(row["confidence"]), 6)
    if out.get("note") is None:
        del out["note"]
    return out


from recommend_pesticide import PesticideRequest, PesticideResponse, recommend

@app.post("/recommend-pesticide", response_model=PesticideResponse)
def recommend_pesticide(req: PesticideRequest):
    return recommend(req)



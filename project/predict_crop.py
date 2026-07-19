"""
Predict crop from full agronomic inputs.
Accepts: nitrogen, phosphorus, potassium, temperature, humidity, rainfall, ph, soil_type, budget
Returns: predicted_crop, confidence, reason, estimated_cost, expected_profit
"""

from __future__ import annotations

import json
import os
import re
from pathlib import Path
from typing import Any

import joblib
import numpy as np
import pandas as pd
from dotenv import load_dotenv
import google.generativeai as genai

# Load env from the ml directory where GEMINI_API_KEY is stored
env_path = Path(__file__).resolve().parent.parent / "ml" / ".env"
load_dotenv(dotenv_path=env_path)

GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")
if GEMINI_API_KEY:
    genai.configure(api_key=GEMINI_API_KEY)


MODEL_PATH = Path(__file__).resolve().parent / "crop_model.pkl"

# Feature order must match training
NUMERIC_FEATURES = ["N", "P", "K", "temperature", "humidity", "ph", "rainfall"]

# Cost and profit maps have been removed in favour of dynamic generation via Gemini AI.


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


def generate_ai_recommendation(crop: str, features: list[float], soil_type: str, budget: str) -> dict[str, str]:
    if not GEMINI_API_KEY:
        return {
            "reason": f"AI key missing. ML predicts {crop} is suitable for these conditions.",
            "estimated_cost": "Contact local agri office",
            "expected_profit": "Medium"
        }
    
    n, p, k, temp, hum, ph, rain = features
    prompt = f"""
The machine learning model has predicted '{crop}' as the best crop for the following conditions:
- Nitrogen: {n} kg/ha
- Phosphorus: {p} kg/ha
- Potassium: {k} kg/ha
- Temperature: {temp}°C
- Humidity: {hum}%
- Rainfall: {rain} mm
- pH: {ph}
- Soil Type: {soil_type}
- Budget Level: {budget}

Provide a short, clear reason (about 2-3 sentences) why this crop is highly suitable for these specific agronomic and soil conditions.
Also provide a realistic estimated cost of cultivation per acre in India (in INR, e.g. "₹25,000 per acre") and expected profit level ("Low", "Medium", or "High").

Return the response strictly as a JSON object with the following keys: "reason", "estimated_cost", "expected_profit". Do not include Markdown formatting like ```json.
"""
    try:
        model = genai.GenerativeModel("gemini-flash-latest")
        response = model.generate_content(prompt)
        text = response.text.strip()
        if text.startswith("```json"):
            text = text[7:]
        if text.endswith("```"):
            text = text[:-3]
        return json.loads(text.strip())
    except Exception as e:
        import logging
        logging.getLogger("crop_api").error(f"Gemini error: {e}")
        return {
            "reason": f"{crop.capitalize()} is recommended based on the provided soil and climate conditions.",
            "estimated_cost": "Estimate unavailable",
            "expected_profit": "Medium"
        }


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
    
    # Generate dynamic AI recommendation
    ai_response = generate_ai_recommendation(raw_label, features, soil_type, budget)
    estimated_cost = ai_response.get("estimated_cost", "Estimate unavailable")
    expected_profit = ai_response.get("expected_profit", "Medium")
    reason = ai_response.get("reason", "No reason provided")

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

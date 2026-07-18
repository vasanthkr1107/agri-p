"""
FastAPI ML service for Crop Recommendation.
Auto-trains the model on startup if crop_model.pkl is missing.
POST /recommend-crop — accepts full agronomic input, returns enriched prediction.
"""

from __future__ import annotations

import logging
from contextlib import asynccontextmanager
from pathlib import Path
from typing import Optional

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field, field_validator

MODEL_PATH = Path(__file__).resolve().parent / "crop_model.pkl"
logger = logging.getLogger("crop_api")


def _ensure_model() -> None:
    """Auto-train model if not present."""
    if MODEL_PATH.exists():
        logger.info("Model found at %s — skipping training.", MODEL_PATH)
        return
    logger.info("Model not found — auto-training now. This may take ~30 seconds …")
    try:
        from train_model import main as train_main
        train_main()
        logger.info("Auto-training complete.")
    except Exception as exc:
        logger.error("Auto-training failed: %s", exc)
        raise RuntimeError(f"Model training failed: {exc}") from exc


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Run model check/train before accepting requests."""
    _ensure_model()
    # Pre-load model into memory cache
    from predict_crop import load_bundle
    load_bundle()
    logger.info("Model loaded and ready.")
    yield


app = FastAPI(
    title="AI Crop Recommendation API",
    description=(
        "Random Forest ML service. Accepts N, P, K, temperature, humidity, "
        "rainfall, ph, soil_type, budget and returns predicted crop with "
        "confidence, reason, estimated cost, and expected profit."
    ),
    version="2.0.0",
    lifespan=lifespan,
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ── Request / Response Models ─────────────────────────────────────────────────

class RecommendRequest(BaseModel):
    nitrogen: float = Field(..., ge=0, le=140, description="Nitrogen content (kg/ha)")
    phosphorus: float = Field(..., ge=0, le=145, description="Phosphorus content (kg/ha)")
    potassium: float = Field(..., ge=0, le=205, description="Potassium content (kg/ha)")
    temperature: float = Field(..., ge=-10, le=60, description="Temperature (°C)")
    humidity: float = Field(..., ge=0, le=100, description="Relative humidity (%)")
    rainfall: float = Field(..., ge=0, description="Annual rainfall (mm)")
    ph: Optional[float] = Field(default=6.5, ge=4.0, le=9.0, description="Soil pH")
    soil_type: str = Field(default="Loamy", min_length=1, description="e.g. Clay, Loamy, Sandy, Black, Red, Sandy Loam")
    budget: str = Field(default="Medium", description="Low / Medium / High")
    location: Optional[str] = Field(default=None, description="Optional location/region")

    @field_validator("soil_type", "budget", mode="before")
    @classmethod
    def strip_strings(cls, v):
        return v.strip() if isinstance(v, str) else v


class RecommendResponse(BaseModel):
    predicted_crop: str
    confidence: float = Field(..., ge=0.0, le=1.0)
    reason: str
    estimated_cost: str
    expected_profit: str


# ── Endpoints ─────────────────────────────────────────────────────────────────

@app.post("/recommend-crop", response_model=RecommendResponse)
def recommend_crop(body: RecommendRequest) -> RecommendResponse:
    """
    Predict the best crop for the given agronomic conditions.
    Returns crop name, confidence score, reason, estimated cost, and profit level.
    """
    from predict_crop import predict_crop_from_dict
    try:
        result = predict_crop_from_dict({
            "nitrogen": body.nitrogen,
            "phosphorus": body.phosphorus,
            "potassium": body.potassium,
            "temperature": body.temperature,
            "humidity": body.humidity,
            "rainfall": body.rainfall,
            "ph": body.ph if body.ph is not None else 6.5,
            "soil_type": body.soil_type,
            "budget": body.budget,
        })
        return RecommendResponse(**result)
    except FileNotFoundError as exc:
        raise HTTPException(status_code=503, detail=str(exc)) from exc
    except ValueError as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc
    except Exception as exc:
        logger.exception("Prediction error")
        raise HTTPException(status_code=500, detail=f"Prediction failed: {exc}") from exc


@app.get("/health")
def health() -> dict:
    """Health check endpoint."""
    return {"status": "ok", "model": str(MODEL_PATH), "model_ready": MODEL_PATH.exists()}

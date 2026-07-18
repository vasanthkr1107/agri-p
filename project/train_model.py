"""
Train a RandomForestClassifier on the crop recommendation dataset.
Features: N, P, K, temperature, humidity, ph, rainfall, soil_type (encoded), budget (encoded)
Outputs:
  - crop_model.pkl         (model + encoders bundle)
  - confusion_matrix.png
  - feature_importance.png
"""

from __future__ import annotations

import urllib.request
from pathlib import Path

import joblib
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import seaborn as sns
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder

# ── Paths ────────────────────────────────────────────────────────────────────
ROOT = Path(__file__).resolve().parent
DATASET_DIR = ROOT / "dataset"
DATA_PATH = DATASET_DIR / "crop_recommendation.csv"
MODEL_PATH = ROOT / "crop_model.pkl"
CM_PATH = ROOT / "confusion_matrix.png"
FI_PATH = ROOT / "feature_importance.png"

DATASET_URL = (
    "https://raw.githubusercontent.com/Gladiator07/Harvestify/master/"
    "Data-processed/crop_recommendation.csv"
)

# Base numeric features from the dataset
NUMERIC_FEATURES = ["N", "P", "K", "temperature", "humidity", "ph", "rainfall"]

# Soil types aligned with frontend options (mapped to representative NPK tiers)
SOIL_TYPES = ["Clay", "Loamy", "Sandy", "Black", "Red", "Sandy Loam"]

# Budget levels
BUDGET_LEVELS = ["Low", "Medium", "High"]


def ensure_dataset() -> Path:
    """Download dataset if not present; return path to CSV."""
    DATASET_DIR.mkdir(parents=True, exist_ok=True)
    if DATA_PATH.exists():
        print(f"Dataset found at {DATA_PATH}")
        return DATA_PATH
    print(f"Downloading dataset to {DATA_PATH} ...")
    urllib.request.urlretrieve(DATASET_URL, DATA_PATH)
    return DATA_PATH


def load_and_prepare(csv_path: Path) -> tuple[pd.DataFrame, pd.Series, LabelEncoder, LabelEncoder]:
    """
    Load dataset, add synthetic soil_type and budget columns,
    encode categoricals, and return X, y, soil_encoder, budget_encoder.
    """
    df = pd.read_csv(csv_path)
    df.columns = [c.strip().lower() for c in df.columns]
    # Standardise column names: lowercase n,p,k → N,P,K
    df = df.rename(columns={"n": "N", "p": "P", "k": "K"})

    if "label" not in df.columns:
        raise ValueError(f"Expected 'label' column in CSV. Got: {list(df.columns)}")

    # ── Synthetic soil_type based on N/P/K ranges ────────────────────────────
    # This maps dataset rows to a realistic soil category so the model learns
    # the soil_type → crop relationship.
    def assign_soil(row: pd.Series) -> str:
        n, p, k = row["N"], row["P"], row["K"]
        if n > 80 and k > 40:
            return "Clay"
        if n > 60:
            return "Loamy"
        if p > 60:
            return "Black"
        if k > 60:
            return "Red"
        if n < 25 and p < 25:
            return "Sandy"
        return "Sandy Loam"

    df["soil_type"] = df.apply(assign_soil, axis=1)

    # ── Synthetic budget based on crop market value ───────────────────────────
    # High-value crops (fruits, cash crops) → High budget; cereals → Low/Medium
    HIGH_BUDGET_CROPS = {"pomegranate", "mango", "grapes", "apple", "coffee", "coconut", "orange"}
    LOW_BUDGET_CROPS = {"rice", "maize", "wheat", "mungbean", "blackgram", "lentil",
                        "mothbeans", "pigeonpeas", "chickpea"}

    def assign_budget(label: str) -> str:
        l = label.strip().lower()
        if l in HIGH_BUDGET_CROPS:
            return "High"
        if l in LOW_BUDGET_CROPS:
            return "Low"
        return "Medium"

    df["budget"] = df["label"].apply(assign_budget)

    # ── Encode categoricals ───────────────────────────────────────────────────
    soil_enc = LabelEncoder()
    soil_enc.fit(SOIL_TYPES)
    df["soil_encoded"] = soil_enc.transform(df["soil_type"])

    budget_enc = LabelEncoder()
    budget_enc.fit(BUDGET_LEVELS)
    df["budget_encoded"] = budget_enc.transform(df["budget"])

    all_features = NUMERIC_FEATURES + ["soil_encoded", "budget_encoded"]
    X = df[all_features].copy()
    y = df["label"].astype(str).str.strip()

    return X, y, soil_enc, budget_enc


def plot_confusion_matrix(y_test: pd.Series, y_pred: np.ndarray, classes: list[str]) -> None:
    """Save a styled confusion matrix PNG."""
    cm = confusion_matrix(y_test, y_pred, labels=classes)
    fig, ax = plt.subplots(figsize=(18, 14))
    sns.heatmap(
        cm,
        annot=True,
        fmt="d",
        cmap="YlOrRd",
        xticklabels=classes,
        yticklabels=classes,
        ax=ax,
        linewidths=0.5,
    )
    ax.set_title("Crop Recommendation — Confusion Matrix", fontsize=16, pad=16)
    ax.set_xlabel("Predicted Label", fontsize=12)
    ax.set_ylabel("True Label", fontsize=12)
    plt.xticks(rotation=45, ha="right", fontsize=9)
    plt.yticks(rotation=0, fontsize=9)
    plt.tight_layout()
    fig.savefig(CM_PATH, dpi=150)
    plt.close(fig)
    print(f"Confusion matrix saved -> {CM_PATH}")


def plot_feature_importance(clf: RandomForestClassifier, feature_names: list[str]) -> None:
    """Save a feature importance bar chart PNG."""
    importances = clf.feature_importances_
    indices = np.argsort(importances)[::-1]
    sorted_names = [feature_names[i] for i in indices]
    sorted_imp = importances[indices]

    fig, ax = plt.subplots(figsize=(10, 6))
    colors = plt.cm.RdYlGn(np.linspace(0.3, 0.9, len(sorted_names)))
    bars = ax.barh(sorted_names[::-1], sorted_imp[::-1], color=colors[::-1], edgecolor="white")
    ax.set_xlabel("Importance Score", fontsize=12)
    ax.set_title("Feature Importance — Random Forest", fontsize=14, pad=12)
    for bar, val in zip(bars, sorted_imp[::-1]):
        ax.text(bar.get_width() + 0.002, bar.get_y() + bar.get_height() / 2,
                f"{val:.3f}", va="center", fontsize=9)
    plt.tight_layout()
    fig.savefig(FI_PATH, dpi=150)
    plt.close(fig)
    print(f"Feature importance saved -> {FI_PATH}")


def main() -> None:
    csv_path = ensure_dataset()
    X, y, soil_enc, budget_enc = load_and_prepare(csv_path)

    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, random_state=42, stratify=y
    )

    print(f"Training on {len(X_train)} samples, testing on {len(X_test)} samples …")

    clf = RandomForestClassifier(
        n_estimators=300,
        max_depth=None,
        min_samples_split=2,
        random_state=42,
        n_jobs=-1,
    )
    clf.fit(X_train, y_train)

    y_pred = clf.predict(X_test)
    acc = accuracy_score(y_test, y_pred)
    print(f"\n[OK] Test accuracy: {acc:.4f} ({acc * 100:.2f}%)")
    print("\nClassification Report:")
    print(classification_report(y_test, y_pred))

    feature_names = NUMERIC_FEATURES + ["soil_type", "budget"]
    plot_confusion_matrix(y_test, y_pred, sorted(y.unique().tolist()))
    plot_feature_importance(clf, feature_names)

    # ── Bundle model + encoders together ─────────────────────────────────────
    bundle = {
        "model": clf,
        "soil_encoder": soil_enc,
        "budget_encoder": budget_enc,
        "feature_names": NUMERIC_FEATURES + ["soil_encoded", "budget_encoded"],
        "soil_types": SOIL_TYPES,
        "budget_levels": BUDGET_LEVELS,
    }
    joblib.dump(bundle, MODEL_PATH)
    print(f"\n[OK] Model bundle saved -> {MODEL_PATH}")


if __name__ == "__main__":
    main()

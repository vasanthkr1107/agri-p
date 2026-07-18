# ML (CNN) — PlantVillage Disease Prediction (TensorFlow + FastAPI)

This folder contains a complete **image-based crop disease prediction system** using the PlantVillage dataset.

## Project structure (this folder)

```
ml/
  dataset/
    PlantVillage/
      Apple___Scab/
      Tomato___Late_blight/
      Potato___Early_blight/
      ...
  train.py
  predict.py
  app.py
  plant_disease_model.h5
  class_indices.json
  requirements.txt
```

## Setup (Windows / PowerShell)

```bash
python -m venv .venv
.\.venv\Scripts\activate
pip install -r ml\requirements.txt
```

## Train model

Point `--dataset-dir` at your local PlantVillage folder.

```bash
python ml\train.py --dataset-dir "ml\dataset\PlantVillage" --epochs 10 --batch-size 32
```

Outputs:
- `plant_disease_model.h5` (best model)
- `class_indices.json` (label mapping)
- `training_accuracy.png`
- `confusion_matrix.npy`

## Predict (CLI)

```bash
python ml\predict.py --image "path\to\leaf.jpg"
```

Output:
```json
{"disease":"Tomato Late blight","confidence":0.95}
```

## Run API (FastAPI)

```bash
uvicorn ml.app:app --reload
```

Then open `http://127.0.0.1:8000/docs`.

## Integration note (Spring Boot)

The API returns only:
- `disease` (string)
- `confidence` (float)

Your Spring Boot backend can:
- fetch symptoms / pesticide / solution from MySQL using `disease`
- store predictions in MySQL


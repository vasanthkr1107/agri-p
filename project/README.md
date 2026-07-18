# Crop recommendation (ML + FastAPI)

Trains a `RandomForestClassifier` on the standard crop recommendation CSV, serves predictions over FastAPI, and pairs with the Spring Boot app in `../demo` for DB enrichment (cost, profit, pesticide).

## Setup

```powershell
cd k:\Downloads\demo\project
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
```

## Train model

Downloads `dataset/crop_recommendation.csv` on first run if missing, then writes `crop_model.pkl` and prints test accuracy.

```powershell
python train_model.py
```

## Run API

```powershell
uvicorn app:app --reload
```

- Interactive docs: [http://127.0.0.1:8000/docs](http://127.0.0.1:8000/docs)
- Health: `GET http://127.0.0.1:8000/health`
- Predict: `POST http://127.0.0.1:8000/recommend-crop` with body  
  `{"soilType": "Loamy", "season": "Kharif", "landArea": 2.5}`  
  Response: `{"crop": "..."}` only.

## Spring Boot

With this API running, start the Java app and call `POST http://localhost:8080/api/recommend-crop` with the same JSON (configure `crop.ml.base-url` in `application.properties` if the ML service is not on port 8000).

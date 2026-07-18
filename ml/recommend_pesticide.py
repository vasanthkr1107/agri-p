from pydantic import BaseModel
from typing import Optional, List

class PesticideRequest(BaseModel):
    crop_name: str
    disease_name: str
    severity: str  # Low, Medium, High

class PesticideResponse(BaseModel):
    recommended_pesticide: str
    chemical_type: str
    dosage: str
    spray_interval: str
    organic_alternative: str

def recommend(req: PesticideRequest) -> PesticideResponse:
    crop = req.crop_name.lower().strip()
    disease = req.disease_name.lower().strip()
    severity = req.severity.lower().strip()

    # Default fallback
    rec_pest = "Broad-spectrum Fungicide/Bactericide"
    chem_type = "General"
    dosage = "Refer to manufacturer label"
    spray_interval = "Every 10-14 days"
    organic = "Neem Oil Extract (Azadirachtin)"

    # Rule-based Engine
    if "blight" in disease:
        chem_type = "Fungicide"
        if severity == "high":
            rec_pest = "Mancozeb + Cymoxanil"
            dosage = "2.5g per liter of water"
            spray_interval = "Every 5-7 days"
            organic = "Copper Fungicide or Bacillus subtilis"
        elif severity == "medium":
            rec_pest = "Mancozeb or Chlorothalonil"
            dosage = "2g per liter of water"
            spray_interval = "Every 7-10 days"
            organic = "Copper soap (Octanoate)"
        else: # low
            rec_pest = "Chlorothalonil"
            dosage = "1.5g per liter of water"
            spray_interval = "Every 10-14 days"
            organic = "Neem oil / Compost Tea"

    elif "bacterial" in disease:
        chem_type = "Bactericide / Copper-based"
        rec_pest = "Copper Hydroxide or Copper Oxychloride"
        dosage = "2g per liter of water"
        spray_interval = "Every 7 days"
        organic = "Serenade (Bacillus subtilis)"
        if severity == "high":
            dosage = "3g per liter of water"
            spray_interval = "Every 5 days"

    elif "mold" in disease or "mildew" in disease:
        chem_type = "Fungicide"
        rec_pest = "Sulfur-based Fungicide or Chlorothalonil"
        dosage = "2g per liter of water"
        spray_interval = "Every 7 days"
        organic = "Potassium Bicarbonate or Neem Oil"
        if severity == "high":
            rec_pest = "Myclobutanil or Azoxystrobin"
            spray_interval = "Every 5-7 days"

    elif "spider" in disease or "mite" in disease:
        chem_type = "Acaricide / Miticide"
        if severity == "high":
            rec_pest = "Abamectin or Spiromesifen"
            dosage = "1ml per liter of water"
            spray_interval = "Every 5-7 days"
            organic = "Horticultural Oil / Insecticidal Soap"
        else:
            rec_pest = "Propargite or Sulfur"
            dosage = "2ml per liter of water"
            spray_interval = "Every 7-10 days"
            organic = "Neem Oil Extract"

    elif "virus" in disease or "mosaic" in disease or "curl" in disease:
        chem_type = "Insecticide (Vector Control)"
        rec_pest = "Imidacloprid or Thiamethoxam (for Whiteflies/Aphids)"
        dosage = "0.5ml per liter of water"
        spray_interval = "Every 10 days"
        organic = "Neem Oil / Pyrethrin"
        if severity == "high":
            spray_interval = "Every 7 days"

    elif "spot" in disease:
        chem_type = "Fungicide"
        rec_pest = "Mancozeb or Copper-based"
        dosage = "2g per liter of water"
        spray_interval = "Every 7-10 days"
        organic = "Bacillus subtilis"
        if severity == "high":
            dosage = "2.5g per liter of water"
            spray_interval = "Every 7 days"

    return PesticideResponse(
        recommended_pesticide=rec_pest,
        chemical_type=chem_type,
        dosage=dosage,
        spray_interval=spray_interval,
        organic_alternative=organic
    )

// src/i18n.js
// i18next configuration with English and Tamil translations
import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

const resources = {
  en: {
    translation: {
      // Page title
      pageTitle: 'AI Crop Recommendation',
      pageSubtitle: 'Enter your soil & climate data to get AI-powered crop predictions',

      // Section headings
      soilParams: 'Soil Parameters',
      climateParams: 'Climate Parameters',
      farmDetails: 'Farm Details',

      // Form labels
      nitrogen: 'Nitrogen (N)',
      phosphorus: 'Phosphorus (P)',
      potassium: 'Potassium (K)',
      ph: 'Soil pH',
      soilType: 'Soil Type',
      temperature: 'Temperature',
      humidity: 'Humidity',
      rainfall: 'Rainfall',
      landArea: 'Land Area',
      budget: 'Budget Level',
      location: 'Location / Region',
      locationPlaceholder: 'e.g. Tamil Nadu, Punjab (optional)',

      // Units
      kgHa: 'kg/ha',
      celsius: '°C',
      percent: '%',
      mmYear: 'mm/year',
      acres: 'acres',

      // Budget
      budgetLow: 'Low',
      budgetMedium: 'Medium',
      budgetHigh: 'High',

      // Soil types
      soilClay: 'Clay',
      soilLoamy: 'Loamy',
      soilSandy: 'Sandy',
      soilBlack: 'Black',
      soilRed: 'Red',
      soilSandyLoam: 'Sandy Loam',

      // Button
      getRecommendation: 'Get AI Recommendation',
      analyzing: 'Analyzing…',

      // Result card
      recommended: 'Recommended Crop',
      confidence: 'Model Confidence',
      whySuitable: 'Why This Crop?',
      estimatedCost: 'Estimated Cost',
      expectedProfit: 'Expected Profit',
      duration: 'Duration',
      days: 'days',
      waterIrrigation: 'Water & Irrigation',
      pesticides: 'Pesticides / IPM',
      soilSuitability: 'Soil Suitability',
      budgetNote: 'Budget Advice',
      profitHigh: 'High',
      profitMedium: 'Medium',
      profitLow: 'Low',

      // Errors
      errorTitle: 'Something went wrong',
      networkError: 'Network error — is the backend running on port 8080?',
      mlUnavailable: 'ML service unavailable — start FastAPI on port 8000.',

      // History page
      historyTitle: 'My Prediction History',
      historySubtitle: 'All your past crop recommendations',
      historyEmpty: 'No predictions yet. Make your first recommendation!',
      historyDate: 'Date',
      historyCrop: 'Crop',
      historyConf: 'Confidence',
      historyBudget: 'Budget',
      historySoil: 'Soil',

      // Disease Detection
      diseaseTitle: 'Disease Detection',
      diseaseSubtitle: 'Upload a plant leaf image to identify diseases using AI',
      uploadImage: 'Upload Leaf Image',
      predictButton: 'Analyze Leaf',
      predictionResult: 'Detection Result',
      plantName: 'Plant',
      diseaseName: 'Detected Disease',
      treatmentTitle: 'Recommended Treatment',
      pesticideTitle: 'Recommended Pesticide',
      preventionTitle: 'Prevention Methods',
      diseaseDescription: 'Description',
      selectImageErr: 'Please select a leaf image first',

      // Pesticide Recommendation
      pestTitle: 'Pesticide Recommendation',
      pestSubtitle: 'Get smart pesticide dosing and organic alternatives',
      severity: 'Disease Severity',
      low: 'Low',
      medium: 'Medium',
      high: 'High',
      getPest: 'Get Recommendation',
      estimatedCost: 'Estimated Cost',
      organicAlternative: 'Organic Alternative',
      safetyPrecautions: 'Safety Precautions',

      // Language toggle
      switchLanguage: 'தமிழ்',
    },
  },

  ta: {
    translation: {
      pageTitle: 'AI பயிர் பரிந்துரை',
      pageSubtitle: 'மண் மற்றும் காலநிலை தரவை உள்ளிட்டு AI பயிர் கணிப்பு பெறுங்கள்',

      soilParams: 'மண் அளவுருக்கள்',
      climateParams: 'காலநிலை அளவுருக்கள்',
      farmDetails: 'பண்ணை விவரங்கள்',

      nitrogen: 'நைட்ரஜன் (N)',
      phosphorus: 'பாஸ்பரஸ் (P)',
      potassium: 'பொட்டாசியம் (K)',
      ph: 'மண் pH',
      soilType: 'மண் வகை',
      temperature: 'வெப்பநிலை',
      humidity: 'ஈரப்பதம்',
      rainfall: 'மழைவீழ்ச்சி',
      landArea: 'நில பரப்பு',
      budget: 'பட்ஜெட் நிலை',
      location: 'இடம் / பிராந்தியம்',
      locationPlaceholder: 'எ.கா: தமிழ்நாடு, பஞ்சாப் (விருப்பத்தேர்வு)',

      kgHa: 'கிகி/ஹெக்டேர்',
      celsius: '°C',
      percent: '%',
      mmYear: 'மிமீ/ஆண்டு',
      acres: 'ஏக்கர்',

      budgetLow: 'குறைவு',
      budgetMedium: 'நடுத்தரம்',
      budgetHigh: 'அதிகம்',

      soilClay: 'களிமண்',
      soilLoamy: 'கலப்பு மண்',
      soilSandy: 'மணல் மண்',
      soilBlack: 'கருப்பு மண்',
      soilRed: 'சிவப்பு மண்',
      soilSandyLoam: 'மணல் கலப்பு மண்',

      getRecommendation: 'AI பரிந்துரை பெறு',
      analyzing: 'பகுப்பாய்வு செய்கிறது…',

      recommended: 'பரிந்துரைக்கப்பட்ட பயிர்',
      confidence: 'மாதிரி நம்பகத்தன்மை',
      whySuitable: 'ஏன் இந்த பயிர்?',
      estimatedCost: 'மதிப்பிடப்பட்ட செலவு',
      expectedProfit: 'எதிர்பார்க்கப்படும் லாபம்',
      duration: 'காலம்',
      days: 'நாட்கள்',
      waterIrrigation: 'நீர் & நீர்ப்பாசனம்',
      pesticides: 'பூச்சிக்கொல்லிகள் / IPM',
      soilSuitability: 'மண் தகுதி',
      budgetNote: 'பட்ஜெட் ஆலோசனை',
      profitHigh: 'அதிக லாபம்',
      profitMedium: 'நடுத்தர லாபம்',
      profitLow: 'குறைவான லாபம்',

      errorTitle: 'பிழை ஏற்பட்டது',
      networkError: 'நெட்வொர்க் பிழை — backend port 8080 இல் இயங்குகிறதா?',
      mlUnavailable: 'ML சேவை இல்லை — FastAPI port 8000 இல் தொடங்குங்கள்.',

      historyTitle: 'என் கணிப்பு வரலாறு',
      historySubtitle: 'உங்கள் அனைத்து பயிர் பரிந்துரைகளும்',
      historyEmpty: 'இன்னும் கணிப்புகள் இல்லை. முதல் பரிந்துரையை பெறுங்கள்!',
      historyDate: 'தேதி',
      historyCrop: 'பயிர்',
      historyConf: 'நம்பகத்தன்மை',
      historyBudget: 'பட்ஜெட்',
      historySoil: 'மண்',

      diseaseTitle: 'நோய் கண்டறிதல்',
      diseaseSubtitle: 'AI மூலம் நோய்களைக் கண்டறிய இலை படத்தை பதிவேற்றவும்',
      uploadImage: 'இலை படத்தை பதிவேற்றவும்',
      predictButton: 'இலையை பகுப்பாய்வு செய்',
      predictionResult: 'கண்டறிதல் முடிவு',
      plantName: 'தாவரம்',
      diseaseName: 'கண்டறியப்பட்ட நோய்',
      treatmentTitle: 'பரிந்துரைக்கப்பட்ட சிகிச்சை',
      pesticideTitle: 'பரிந்துரைக்கப்பட்ட பூச்சிக்கொல்லி',
      preventionTitle: 'தடுப்பு முறைகள்',
      diseaseDescription: 'விளக்கம்',
      selectImageErr: 'தயவுசெய்து முதலில் ஒரு இலை படத்தை தேர்ந்தெடுக்கவும்',

      pestTitle: 'பூச்சிக்கொல்லி பரிந்துரை',
      pestSubtitle: 'சரியான அளவுகள் மற்றும் இயற்கை மாற்றுகளைப் பெறுங்கள்',
      severity: 'நோயின் தீவிரம்',
      low: 'குறைந்த',
      medium: 'நடுத்தர',
      high: 'அதிக',
      getPest: 'பரிந்துரையைப் பெறுக',
      estimatedCost: 'மதிப்பிடப்பட்ட செலவு',
      organicAlternative: 'இயற்கை மாற்று',
      safetyPrecautions: 'பாதுகாப்பு முன்னெச்சரிக்கைகள்',

      switchLanguage: 'English',
    },
  },
};

i18n.use(initReactI18next).init({
  resources,
  lng: 'en',
  fallbackLng: 'en',
  interpolation: { escapeValue: false },
});

export default i18n;

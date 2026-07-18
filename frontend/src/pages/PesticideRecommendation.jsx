import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { recommendPesticideAi } from '../services/api';
import Loader from '../components/Loader';

const CROPS = ['Tomato', 'Potato', 'Pepper bell', 'Wheat', 'Rice'];
const DISEASES = [
  'Early blight', 'Late blight', 'Bacterial spot', 'Leaf Mold',
  'Septoria leaf spot', 'Spider mites Two spotted spider mite',
  'Target Spot', 'Tomato YellowLeaf Curl Virus', 'Tomato mosaic virus'
];

export default function PesticideRecommendation() {
  const { t, i18n } = useTranslation();
  const [form, setForm] = useState({
    crop_name: 'Tomato',
    disease_name: 'Early blight',
    severity: 'Medium'
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [result, setResult] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setResult(null);
    setLoading(true);

    try {
      const res = await recommendPesticideAi(form);
      setResult(res.data);
    } catch (err) {
      setError(err.response?.data?.message || err.response?.data?.detail || err.message || 'Recommendation failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 900, margin: '0 auto', fontFamily: "'Inter', sans-serif" }}>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 24 }}>
        <div>
          <h1 style={{ fontSize: 26, fontWeight: 800, margin: 0, background: 'linear-gradient(90deg, #38bdf8, #818cf8)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
            🧪 {t('pestTitle') || 'Pesticide Recommendation'}
          </h1>
          <p style={{ color: '#64748b', margin: '4px 0 0', fontSize: 14 }}>{t('pestSubtitle') || 'Get smart pesticide dosing and organic alternatives'}</p>
        </div>
        <button onClick={() => i18n.changeLanguage(i18n.language === 'en' ? 'ta' : 'en')}
          style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: 10, color: '#38bdf8', padding: '8px 14px', fontSize: 13, fontWeight: 700, cursor: 'pointer' }}>
          🌐 {t('switchLanguage')}
        </button>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 24 }}>
        {/* Form Section */}
        <div style={{ background: 'linear-gradient(135deg, #1e293b 0%, #0f172a 100%)', border: '1px solid #334155', borderRadius: 16, padding: '24px 28px' }}>
          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            <div>
              <label style={{ display: 'block', fontSize: 12, color: '#94a3b8', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.04em', marginBottom: 8 }}>
                {t('plantName')}
              </label>
              <select 
                value={form.crop_name} 
                onChange={e => setForm({...form, crop_name: e.target.value})}
                style={{ width: '100%', padding: '10px 14px', borderRadius: 10, border: '1px solid #334155', background: '#0f172a', color: '#f1f5f9', cursor: 'pointer', fontSize: 14 }}
              >
                {CROPS.map(c => <option key={c} value={c}>{c}</option>)}
              </select>
            </div>

            <div>
              <label style={{ display: 'block', fontSize: 12, color: '#94a3b8', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.04em', marginBottom: 8 }}>
                {t('diseaseName')}
              </label>
              <select 
                value={form.disease_name} 
                onChange={e => setForm({...form, disease_name: e.target.value})}
                style={{ width: '100%', padding: '10px 14px', borderRadius: 10, border: '1px solid #334155', background: '#0f172a', color: '#f1f5f9', cursor: 'pointer', fontSize: 14 }}
              >
                {DISEASES.map(d => <option key={d} value={d}>{d}</option>)}
              </select>
            </div>

            <div>
              <label style={{ display: 'block', fontSize: 12, color: '#94a3b8', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.04em', marginBottom: 8 }}>
                {t('severity') || 'Disease Severity'}
              </label>
              <div style={{ display: 'flex', gap: 8 }}>
                {['Low', 'Medium', 'High'].map(lvl => (
                  <button
                    key={lvl}
                    type="button"
                    onClick={() => setForm({...form, severity: lvl})}
                    style={{
                      flex: 1, padding: '10px 0', borderRadius: 10, fontSize: 13, fontWeight: 700, cursor: 'pointer', border: '1.5px solid',
                      borderColor: form.severity === lvl ? (lvl === 'High' ? '#f87171' : lvl === 'Medium' ? '#fcd34d' : '#6ee7b7') : '#334155',
                      background: form.severity === lvl ? (lvl === 'High' ? '#f8717122' : lvl === 'Medium' ? '#fcd34d22' : '#6ee7b722') : '#0f172a',
                      color: form.severity === lvl ? (lvl === 'High' ? '#f87171' : lvl === 'Medium' ? '#fcd34d' : '#6ee7b7') : '#64748b',
                      transition: 'all 0.2s'
                    }}
                  >
                    {t(lvl.toLowerCase()) || lvl}
                  </button>
                ))}
              </div>
            </div>

            {error && (
              <div style={{ padding: '10px 14px', borderRadius: 10, border: '1px solid #ef444466', background: '#ef444411', color: '#fca5a5', fontSize: 13 }}>
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              style={{
                width: '100%', padding: '14px', borderRadius: 12, fontSize: 15, fontWeight: 800, marginTop: 8,
                cursor: loading ? 'not-allowed' : 'pointer', border: 'none',
                background: loading ? '#1e293b' : 'linear-gradient(90deg, #38bdf8, #818cf8)',
                color: loading ? '#475569' : '#0f172a',
                display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 10,
                transition: 'all 0.3s'
              }}
            >
              {loading && <Loader size="sm" />}
              {loading ? t('analyzing') : `🔍 ${t('getPest') || 'Get Recommendation'}`}
            </button>
          </form>
        </div>

        {/* Result Section */}
        {result && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: 16, animation: 'fadeIn 0.4s ease' }}>
            <div style={{ background: 'linear-gradient(135deg, #1e293b 0%, #0f172a 100%)', border: '1px solid #38bdf844', borderRadius: 16, padding: '24px 28px' }}>
              <p style={{ fontSize: 11, color: '#64748b', textTransform: 'uppercase', letterSpacing: '0.08em', margin: '0 0 4px' }}>{t('pesticideTitle')}</p>
              <h2 style={{ fontSize: 24, fontWeight: 900, margin: '0 0 16px', color: '#f1f5f9' }}>{result.recommended_pesticide}</h2>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                <div style={{ background: '#0f172a', border: '1px solid #1e293b', borderRadius: 10, padding: '12px 14px' }}>
                  <p style={{ fontSize: 10, color: '#475569', textTransform: 'uppercase', letterSpacing: '0.06em', margin: '0 0 4px' }}>Type</p>
                  <p style={{ fontSize: 14, fontWeight: 600, color: '#38bdf8', margin: 0 }}>{result.chemical_type}</p>
                </div>
                <div style={{ background: '#0f172a', border: '1px solid #1e293b', borderRadius: 10, padding: '12px 14px' }}>
                  <p style={{ fontSize: 10, color: '#475569', textTransform: 'uppercase', letterSpacing: '0.06em', margin: '0 0 4px' }}>{t('estimatedCost')}</p>
                  <p style={{ fontSize: 14, fontWeight: 600, color: '#eab308', margin: 0 }}>{result.estimated_cost}</p>
                </div>
                <div style={{ background: '#0f172a', border: '1px solid #1e293b', borderRadius: 10, padding: '12px 14px' }}>
                  <p style={{ fontSize: 10, color: '#475569', textTransform: 'uppercase', letterSpacing: '0.06em', margin: '0 0 4px' }}>Dosage</p>
                  <p style={{ fontSize: 14, fontWeight: 600, color: '#f1f5f9', margin: 0 }}>{result.dosage}</p>
                </div>
                <div style={{ background: '#0f172a', border: '1px solid #1e293b', borderRadius: 10, padding: '12px 14px' }}>
                  <p style={{ fontSize: 10, color: '#475569', textTransform: 'uppercase', letterSpacing: '0.06em', margin: '0 0 4px' }}>Schedule</p>
                  <p style={{ fontSize: 14, fontWeight: 600, color: '#f1f5f9', margin: 0 }}>{result.spray_interval}</p>
                </div>
              </div>
            </div>

            {/* Organic Alternative */}
            <div style={{ background: 'rgba(34, 197, 94, 0.1)', border: '1px solid rgba(34, 197, 94, 0.3)', borderRadius: 16, padding: '18px 24px' }}>
              <h3 style={{ fontSize: 14, fontWeight: 700, color: '#22c55e', margin: '0 0 8px', display: 'flex', alignItems: 'center', gap: 8 }}>
                🌿 Eco-Friendly Organic Alternative
              </h3>
              <p style={{ color: '#166534', fontSize: 14, margin: 0, fontWeight: 500 }}>{result.organic_alternative}</p>
            </div>

            {/* Safety */}
            {result.safety_precautions && result.safety_precautions.length > 0 && (
              <div style={{ background: '#0f172a', border: '1px solid #1e293b', borderRadius: 16, padding: '20px 24px' }}>
                <h3 style={{ fontSize: 13, fontWeight: 700, color: '#f87171', margin: '0 0 12px', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
                  ⚠️ Safety Precautions
                </h3>
                <ul style={{ margin: 0, paddingLeft: 20, color: '#cbd5e1', fontSize: 13, lineHeight: 1.6 }}>
                  {result.safety_precautions.map((precaution, idx) => (
                    <li key={idx}>{precaution}</li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        )}
      </div>

      <style>{`@keyframes fadeIn { from { opacity: 0; transform: translateY(12px); } to { opacity: 1; transform: none; } }`}</style>
    </div>
  );
}

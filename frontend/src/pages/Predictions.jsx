import { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { detectDiseaseAi } from '../services/api';
import Card from '../components/Card';
import Loader from '../components/Loader';

export default function Predictions() {
  const { t, i18n } = useTranslation();
  const [imageFile, setImageFile] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleFileChange = (e) => {
    const file = e.target.files?.[0];
    setImageFile(file);
    setImagePreview(file ? URL.createObjectURL(file) : null);
    setResult(null);
    setError(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setResult(null);
    
    if (!imageFile) {
      setError(t('selectImageErr'));
      return;
    }
    
    setLoading(true);
    try {
      const formData = new FormData();
      formData.append('image', imageFile);
      
      const res = await detectDiseaseAi(formData);
      setResult(res.data);
    } catch (err) {
      setError(err.response?.data?.message || err.response?.data?.detail || err.message || 'Prediction failed');
    } finally {
      setLoading(false);
    }
  };

  const pct = Math.round((result?.confidence ?? 0) * 100);
  const color = pct >= 75 ? '#22c55e' : pct >= 50 ? '#f59e0b' : '#ef4444';

  return (
    <div style={{ maxWidth: 900, margin: '0 auto', fontFamily: "'Inter', sans-serif" }}>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 24 }}>
        <div>
          <h1 style={{ fontSize: 26, fontWeight: 800, margin: 0, background: 'linear-gradient(90deg, #38bdf8, #818cf8)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
            🩺 {t('diseaseTitle')}
          </h1>
          <p style={{ color: '#64748b', margin: '4px 0 0', fontSize: 14 }}>{t('diseaseSubtitle')}</p>
        </div>
        <button onClick={() => i18n.changeLanguage(i18n.language === 'en' ? 'ta' : 'en')}
          style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: 10, color: '#38bdf8', padding: '8px 14px', fontSize: 13, fontWeight: 700, cursor: 'pointer' }}>
          🌐 {t('switchLanguage')}
        </button>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 24 }}>
        {/* Upload Section */}
        <div style={{ background: 'linear-gradient(135deg, #1e293b 0%, #0f172a 100%)', border: '1px solid #334155', borderRadius: 16, padding: '24px 28px' }}>
          <h3 style={{ fontSize: 15, fontWeight: 700, color: '#38bdf8', marginBottom: 20, letterSpacing: '0.04em', textTransform: 'uppercase' }}>
            📸 {t('uploadImage')}
          </h3>
          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            <div>
              <input
                type="file"
                accept="image/*"
                onChange={handleFileChange}
                style={{ width: '100%', padding: '10px 14px', borderRadius: 10, border: '1px dashed #475569', background: '#0f172a', color: '#f1f5f9', cursor: 'pointer', boxSizing: 'border-box' }}
              />
              {imagePreview && (
                <div style={{ marginTop: 16, borderRadius: 12, overflow: 'hidden', border: '1px solid #334155', background: '#000', display: 'flex', justifyContent: 'center' }}>
                  <img src={imagePreview} alt="Preview" style={{ maxHeight: 240, objectFit: 'contain' }} />
                </div>
              )}
            </div>

            {error && (
              <div style={{ padding: '10px 14px', borderRadius: 10, border: '1px solid #ef444466', background: '#ef444411', color: '#fca5a5', fontSize: 13 }}>
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={loading || !imageFile}
              style={{
                width: '100%', padding: '14px', borderRadius: 12, fontSize: 15, fontWeight: 800,
                cursor: (loading || !imageFile) ? 'not-allowed' : 'pointer', border: 'none',
                background: (loading || !imageFile) ? '#1e293b' : 'linear-gradient(90deg, #38bdf8, #818cf8)',
                color: (loading || !imageFile) ? '#475569' : '#0f172a',
                display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 10,
                transition: 'all 0.3s'
              }}
            >
              {loading && <Loader size="sm" />}
              {loading ? t('analyzing') || 'Analyzing...' : `🔍 ${t('predictButton')}`}
            </button>
          </form>
        </div>

        {/* Result Section */}
        {result && (
          <div style={{ background: 'linear-gradient(135deg, #1e293b 0%, #0f172a 100%)', border: '1px solid #38bdf844', borderRadius: 16, padding: '24px 28px', animation: 'fadeIn 0.4s ease' }}>
            <h3 style={{ fontSize: 15, fontWeight: 700, color: '#38bdf8', marginBottom: 20, letterSpacing: '0.04em', textTransform: 'uppercase' }}>
              🔬 {t('predictionResult')}
            </h3>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
              <div style={{ background: '#0f172a', border: '1px solid #1e293b', borderRadius: 12, padding: '14px 16px' }}>
                <p style={{ fontSize: 11, color: '#475569', textTransform: 'uppercase', letterSpacing: '0.08em', margin: '0 0 4px' }}>🌱 {t('plantName')}</p>
                <p style={{ fontSize: 18, fontWeight: 700, color: '#f1f5f9', margin: 0 }}>{result.plant_name || '—'}</p>
              </div>
              <div style={{ background: '#0f172a', border: '1px solid #1e293b', borderRadius: 12, padding: '14px 16px' }}>
                <p style={{ fontSize: 11, color: '#475569', textTransform: 'uppercase', letterSpacing: '0.08em', margin: '0 0 4px' }}>🦠 {t('diseaseName')}</p>
                <p style={{ fontSize: 18, fontWeight: 700, color: result.disease_name?.toLowerCase() === 'healthy' ? '#22c55e' : '#f87171', margin: 0 }}>{result.disease_name || '—'}</p>
              </div>
            </div>

            <div style={{ marginTop: 20 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 6 }}>
                <span style={{ fontSize: 12, color: '#94a3b8', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.05em' }}>
                  {t('historyConf')}
                </span>
                <span style={{ fontSize: 15, fontWeight: 800, color }}>{pct}%</span>
              </div>
              <div style={{ background: '#0f172a', borderRadius: 99, height: 10, overflow: 'hidden' }}>
                <div style={{ width: `${pct}%`, height: '100%', background: `linear-gradient(90deg, ${color}88, ${color})`, borderRadius: 99, transition: 'width 0.8s ease' }} />
              </div>
            </div>

            {/* Description & Agronomy Info */}
            <div style={{ marginTop: 20, display: 'flex', flexDirection: 'column', gap: 12 }}>
              <div style={{ background: '#0f172a', border: '1px solid #1e293b', borderRadius: 12, padding: '14px 16px' }}>
                <p style={{ fontSize: 11, color: '#475569', textTransform: 'uppercase', letterSpacing: '0.08em', margin: '0 0 6px' }}>📖 {t('diseaseDescription')}</p>
                <p style={{ color: '#cbd5e1', fontSize: 13, margin: 0, lineHeight: 1.6 }}>{result.description || '—'}</p>
              </div>
              
              <div style={{ background: '#0f172a', border: '1px solid #1e293b', borderRadius: 12, padding: '14px 16px' }}>
                <p style={{ fontSize: 11, color: '#475569', textTransform: 'uppercase', letterSpacing: '0.08em', margin: '0 0 6px' }}>🩺 {t('treatmentTitle')}</p>
                <p style={{ color: '#cbd5e1', fontSize: 13, margin: 0, lineHeight: 1.6 }}>{result.treatment_steps || '—'}</p>
              </div>

              {result.disease_name?.toLowerCase() !== 'healthy' && (
                <div style={{ background: '#0f172a', border: '1px solid #1e293b', borderRadius: 12, padding: '14px 16px' }}>
                  <p style={{ fontSize: 11, color: '#475569', textTransform: 'uppercase', letterSpacing: '0.08em', margin: '0 0 6px' }}>🧪 {t('pesticideTitle')}</p>
                  <p style={{ color: '#cbd5e1', fontSize: 13, margin: 0, lineHeight: 1.6 }}>{result.recommended_pesticide || '—'}</p>
                </div>
              )}

              <div style={{ background: '#0f172a', border: '1px solid #1e293b', borderRadius: 12, padding: '14px 16px' }}>
                <p style={{ fontSize: 11, color: '#475569', textTransform: 'uppercase', letterSpacing: '0.08em', margin: '0 0 6px' }}>🛡️ {t('preventionTitle')}</p>
                <p style={{ color: '#cbd5e1', fontSize: 13, margin: 0, lineHeight: 1.6 }}>{result.prevention || '—'}</p>
              </div>
            </div>
          </div>
        )}
      </div>

      <style>{`
        @keyframes fadeIn { from { opacity: 0; transform: translateY(12px); } to { opacity: 1; transform: none; } }
      `}</style>
    </div>
  );
}

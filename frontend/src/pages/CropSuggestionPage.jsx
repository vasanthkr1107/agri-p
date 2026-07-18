import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { suggestCropAi } from '../services/api';
import Loader from '../components/Loader';

const SOIL_OPTIONS = [
  { value: 'Clay', labelKey: 'soilClay' },
  { value: 'Loamy', labelKey: 'soilLoamy' },
  { value: 'Sandy', labelKey: 'soilSandy' },
  { value: 'Black', labelKey: 'soilBlack' },
  { value: 'Red', labelKey: 'soilRed' },
  { value: 'Sandy Loam', labelKey: 'soilSandyLoam' },
];

const BUDGET_OPTIONS = [
  { value: 'Low', labelKey: 'budgetLow', color: '#6ee7b7' },
  { value: 'Medium', labelKey: 'budgetMedium', color: '#fcd34d' },
  { value: 'High', labelKey: 'budgetHigh', color: '#f87171' },
];

function fmt(v) {
  if (v == null || Number.isNaN(Number(v))) return '—';
  return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(Number(v));
}

function ConfBar({ value }) {
  const pct = Math.round((value ?? 0) * 100);
  const c = pct >= 75 ? '#22c55e' : pct >= 50 ? '#f59e0b' : '#ef4444';
  return (
    <div style={{ marginTop: 16 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
        <span style={{ fontSize: 11, color: '#64748b', textTransform: 'uppercase', letterSpacing: '0.06em' }}>Confidence</span>
        <span style={{ fontWeight: 800, color: c }}>{pct}%</span>
      </div>
      <div style={{ background: '#1e293b', borderRadius: 99, height: 10, overflow: 'hidden' }}>
        <div style={{ width: `${pct}%`, height: '100%', background: `linear-gradient(90deg,${c}88,${c})`, borderRadius: 99, transition: 'width 0.8s ease' }} />
      </div>
    </div>
  );
}

function Slider({ id, label, unit, value, onChange, min, max, step = 1 }) {
  return (
    <div style={{ marginBottom: 16 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
        <label htmlFor={id} style={{ fontSize: 12, color: '#94a3b8', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.04em' }}>{label}</label>
        <span style={{ fontSize: 13, fontWeight: 700, color: '#38bdf8' }}>{value} <span style={{ fontSize: 10, color: '#475569' }}>{unit}</span></span>
      </div>
      <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
        <input id={id} type="range" min={min} max={max} step={step} value={value}
          onChange={(e) => onChange(parseFloat(e.target.value))}
          style={{ flex: 1, accentColor: '#38bdf8', cursor: 'pointer' }} />
        <input type="number" min={min} max={max} step={step} value={value}
          onChange={(e) => onChange(parseFloat(e.target.value) || min)}
          style={{ width: 64, padding: '4px 6px', borderRadius: 8, border: '1px solid #334155', background: '#0f172a', color: '#f1f5f9', fontSize: 12, textAlign: 'center' }} />
      </div>
    </div>
  );
}

function Section({ icon, title, children }) {
  return (
    <div style={{ background: 'linear-gradient(135deg,#1e293b,#0f172a)', border: '1px solid #334155', borderRadius: 16, padding: '22px 24px', marginBottom: 18 }}>
      <h2 style={{ fontSize: 12, fontWeight: 700, color: '#38bdf8', textTransform: 'uppercase', letterSpacing: '0.08em', margin: '0 0 18px', display: 'flex', alignItems: 'center', gap: 8 }}>
        {icon} {title}
      </h2>
      {children}
    </div>
  );
}

function Cell({ icon, label, value, green }) {
  return (
    <div style={{ background: '#0f172a', border: '1px solid #1e293b', borderRadius: 12, padding: '14px 16px' }}>
      <p style={{ fontSize: 10, color: '#475569', textTransform: 'uppercase', letterSpacing: '0.07em', margin: '0 0 4px' }}>{icon} {label}</p>
      <p style={{ fontSize: 17, fontWeight: 700, color: green ? '#22c55e' : '#f1f5f9', margin: 0 }}>{value || '—'}</p>
    </div>
  );
}

function Wide({ icon, label, value }) {
  return (
    <div style={{ background: '#0f172a', border: '1px solid #1e293b', borderRadius: 12, padding: '14px 16px', marginTop: 12 }}>
      <p style={{ fontSize: 10, color: '#475569', textTransform: 'uppercase', letterSpacing: '0.07em', margin: '0 0 6px' }}>{icon} {label}</p>
      <p style={{ color: '#94a3b8', fontSize: 13, margin: 0, lineHeight: 1.7 }}>{value}</p>
    </div>
  );
}

function apiError(err) {
  if (!err?.response) return err?.message || 'Network error — is backend running on port 8080?';
  const d = err.response.data?.detail || err.response.data?.message;
  return d || `Request failed (${err.response.status})`;
}

const DEF = { nitrogen: 90, phosphorus: 42, potassium: 43, temperature: 25, humidity: 80, rainfall: 200, ph: 6.5, soilType: 'Clay', budget: 'Medium', landArea: 1, location: '' };

export default function CropSuggestionPage() {
  const { t, i18n } = useTranslation();
  const [f, setF] = useState({ ...DEF });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [result, setResult] = useState(null);
  const set = (k, v) => setF((p) => ({ ...p, [k]: v }));

  const submit = async (e) => {
    e.preventDefault();
    setError(null); setResult(null); setLoading(true);
    try {
      const res = await suggestCropAi({ nitrogen: f.nitrogen, phosphorus: f.phosphorus, potassium: f.potassium, temperature: f.temperature, humidity: f.humidity, rainfall: f.rainfall, ph: f.ph, soilType: f.soilType, budget: f.budget, landArea: f.landArea, location: f.location || undefined });
      setResult(res.data);
    } catch (err) { setError(apiError(err)); }
    finally { setLoading(false); }
  };

  return (
    <div style={{ maxWidth: 860, margin: '0 auto', fontFamily: "'Inter',sans-serif" }}>
      <style>{`@keyframes fi{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:none}}`}</style>

      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 24 }}>
        <div>
          <h1 style={{ fontSize: 24, fontWeight: 800, margin: 0, background: 'linear-gradient(90deg,#38bdf8,#818cf8)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
            🌾 {t('pageTitle')}
          </h1>
          <p style={{ color: '#64748b', margin: '4px 0 0', fontSize: 13 }}>{t('pageSubtitle')}</p>
        </div>
        <button onClick={() => i18n.changeLanguage(i18n.language === 'en' ? 'ta' : 'en')}
          style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: 10, color: '#38bdf8', padding: '8px 14px', fontSize: 12, fontWeight: 700, cursor: 'pointer' }}>
          🌐 {t('switchLanguage')}
        </button>
      </div>

      <form onSubmit={submit}>
        {/* Soil */}
        <Section icon="🧪" title={t('soilParams')}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 28px' }}>
            <Slider id="n" label={t('nitrogen')} unit={t('kgHa')} value={f.nitrogen} onChange={(v) => set('nitrogen', v)} min={0} max={140} />
            <Slider id="p" label={t('phosphorus')} unit={t('kgHa')} value={f.phosphorus} onChange={(v) => set('phosphorus', v)} min={0} max={145} />
            <Slider id="k" label={t('potassium')} unit={t('kgHa')} value={f.potassium} onChange={(v) => set('potassium', v)} min={0} max={205} />
            <Slider id="ph" label={t('ph')} unit="" value={f.ph} onChange={(v) => set('ph', v)} min={4} max={9} step={0.1} />
          </div>
          <div style={{ marginTop: 8 }}>
            <p style={{ fontSize: 12, color: '#94a3b8', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.04em', margin: '0 0 8px' }}>{t('soilType')}</p>
            <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
              {SOIL_OPTIONS.map((s) => (
                <button key={s.value} type="button" onClick={() => set('soilType', s.value)}
                  style={{ padding: '6px 14px', borderRadius: 99, fontSize: 12, fontWeight: 600, cursor: 'pointer', border: '1.5px solid', borderColor: f.soilType === s.value ? '#38bdf8' : '#334155', background: f.soilType === s.value ? 'rgba(56,189,248,0.12)' : '#0f172a', color: f.soilType === s.value ? '#38bdf8' : '#64748b', transition: 'all 0.2s' }}>
                  {t(s.labelKey)}
                </button>
              ))}
            </div>
          </div>
        </Section>

        {/* Climate */}
        <Section icon="🌡️" title={t('climateParams')}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 28px' }}>
            <Slider id="tmp" label={t('temperature')} unit={t('celsius')} value={f.temperature} onChange={(v) => set('temperature', v)} min={-10} max={60} />
            <Slider id="hum" label={t('humidity')} unit={t('percent')} value={f.humidity} onChange={(v) => set('humidity', v)} min={0} max={100} />
            <Slider id="rain" label={t('rainfall')} unit={t('mmYear')} value={f.rainfall} onChange={(v) => set('rainfall', v)} min={0} max={3000} step={10} />
          </div>
        </Section>

        {/* Farm */}
        <Section icon="🌾" title={t('farmDetails')}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 28px' }}>
            <Slider id="area" label={`${t('landArea')} (${t('acres')})`} unit={t('acres')} value={f.landArea} onChange={(v) => set('landArea', v)} min={0.1} max={100} step={0.1} />
            <div>
              <p style={{ fontSize: 12, color: '#94a3b8', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.04em', margin: '0 0 8px' }}>{t('budget')}</p>
              <div style={{ display: 'flex', gap: 8 }}>
                {BUDGET_OPTIONS.map((b) => (
                  <button key={b.value} type="button" onClick={() => set('budget', b.value)}
                    style={{ flex: 1, padding: '9px 0', borderRadius: 10, fontSize: 12, fontWeight: 700, cursor: 'pointer', border: '1.5px solid', borderColor: f.budget === b.value ? b.color : '#334155', background: f.budget === b.value ? `${b.color}22` : '#0f172a', color: f.budget === b.value ? b.color : '#64748b', transition: 'all 0.2s' }}>
                    {t(b.labelKey)}
                  </button>
                ))}
              </div>
            </div>
          </div>
          <div style={{ marginTop: 14 }}>
            <label htmlFor="loc" style={{ fontSize: 12, color: '#94a3b8', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.04em' }}>{t('location')}</label>
            <input id="loc" type="text" value={f.location} onChange={(e) => set('location', e.target.value)}
              placeholder={t('locationPlaceholder')}
              style={{ display: 'block', width: '100%', marginTop: 6, padding: '9px 12px', borderRadius: 10, border: '1px solid #334155', background: '#0f172a', color: '#f1f5f9', fontSize: 13, boxSizing: 'border-box' }} />
          </div>
        </Section>

        <button type="submit" disabled={loading}
          style={{ width: '100%', padding: 15, borderRadius: 14, fontSize: 15, fontWeight: 800, cursor: loading ? 'not-allowed' : 'pointer', border: 'none', background: loading ? '#1e293b' : 'linear-gradient(90deg,#38bdf8,#818cf8)', color: loading ? '#475569' : '#0f172a', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 10, transition: 'all 0.3s', boxShadow: loading ? 'none' : '0 4px 24px rgba(56,189,248,0.3)' }}>
          {loading && <Loader size="sm" />}
          {loading ? t('analyzing') : `🌱 ${t('getRecommendation')}`}
        </button>
      </form>

      {error && (
        <div style={{ marginTop: 18, padding: '14px 18px', borderRadius: 12, border: '1px solid #ef444466', background: '#ef444411' }}>
          <p style={{ color: '#f87171', fontWeight: 700, margin: 0 }}>{t('errorTitle')}</p>
          <p style={{ color: '#fca5a5', fontSize: 13, margin: '4px 0 0' }}>{error}</p>
        </div>
      )}

      {result && (
        <div style={{ marginTop: 24, background: 'linear-gradient(135deg,#1e293b,#0f172a)', border: '1px solid #38bdf844', borderRadius: 20, padding: '26px 28px', animation: 'fi 0.4s ease' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 6, flexWrap: 'wrap', gap: 10 }}>
            <div>
              <p style={{ fontSize: 10, color: '#64748b', textTransform: 'uppercase', letterSpacing: '0.08em', margin: 0 }}>{t('recommended')}</p>
              <h2 style={{ fontSize: 32, fontWeight: 900, margin: '2px 0 0', background: 'linear-gradient(90deg,#38bdf8,#818cf8)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>{result.cropName}</h2>
            </div>
            {result.expectedProfitLabel && (
              <span style={{ padding: '5px 16px', borderRadius: 99, fontSize: 12, fontWeight: 700, background: result.expectedProfitLabel === 'High' ? 'rgba(34,197,94,0.15)' : result.expectedProfitLabel === 'Medium' ? 'rgba(234,179,8,0.15)' : 'rgba(239,68,68,0.15)', color: result.expectedProfitLabel === 'High' ? '#22c55e' : result.expectedProfitLabel === 'Medium' ? '#eab308' : '#ef4444', border: '1px solid currentColor' }}>
                {result.expectedProfitLabel} {t('expectedProfit')}
              </span>
            )}
          </div>

          <ConfBar value={result.modelConfidence} />

          {result.reason && (
            <div style={{ marginTop: 18, padding: '13px 16px', borderRadius: 12, background: 'rgba(56,189,248,0.07)', border: '1px solid rgba(56,189,248,0.2)' }}>
              <p style={{ fontSize: 10, color: '#64748b', textTransform: 'uppercase', letterSpacing: '0.07em', margin: '0 0 5px' }}>💡 {t('whySuitable')}</p>
              <p style={{ color: '#cbd5e1', fontSize: 13, margin: 0, lineHeight: 1.65 }}>{result.reason}</p>
            </div>
          )}

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12, marginTop: 18 }}>
            <Cell icon="💰" label={t('estimatedCost')} value={result.estimatedCostLabel || fmt(result.totalCost)} />
            <Cell icon="📈" label={t('expectedProfit')} value={fmt(result.expectedProfit)} green />
            <Cell icon="📅" label={t('duration')} value={result.duration ? `${result.duration} ${t('days')}` : null} />
            <Cell icon="🌍" label={t('soilType')} value={f.soilType} />
          </div>

          {result.waterRequirements && <Wide icon="💧" label={t('waterIrrigation')} value={result.waterRequirements} />}
          {result.pesticides && <Wide icon="🔬" label={t('pesticides')} value={result.pesticides} />}
          {result.soilSuitability && <Wide icon="🌱" label={t('soilSuitability')} value={result.soilSuitability} />}
          {result.budgetNote && <Wide icon="🏦" label={t('budgetNote')} value={result.budgetNote} />}

          {result.recommendationNote && (
            <div style={{ marginTop: 14, padding: '11px 15px', borderRadius: 10, background: 'rgba(234,179,8,0.1)', border: '1px solid rgba(234,179,8,0.3)', color: '#fcd34d', fontSize: 12 }}>
              ⚠️ {result.recommendationNote}
            </div>
          )}
        </div>
      )}
    </div>
  );
}

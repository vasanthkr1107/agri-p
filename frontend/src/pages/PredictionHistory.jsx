import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { getCropHistory } from '../services/api';

function badge(pct) {
  const c = pct >= 75 ? '#22c55e' : pct >= 50 ? '#f59e0b' : '#ef4444';
  return (
    <span style={{ display: 'inline-block', padding: '2px 10px', borderRadius: 99, fontSize: 11, fontWeight: 700, background: `${c}22`, color: c, border: `1px solid ${c}66` }}>
      {pct}%
    </span>
  );
}

export default function PredictionHistory() {
  const { t, i18n } = useTranslation();
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filter, setFilter] = useState('');

  useEffect(() => {
    getCropHistory()
      .then((res) => setRows(res.data))
      .catch((err) => {
        const d = err?.response?.data?.message || err?.response?.data?.detail || err?.message;
        setError(d || 'Failed to load history.');
      })
      .finally(() => setLoading(false));
  }, []);

  const filtered = rows.filter((r) =>
    !filter || r.predictedCrop?.toLowerCase().includes(filter.toLowerCase())
  );

  return (
    <div style={{ maxWidth: 1000, margin: '0 auto', fontFamily: "'Inter',sans-serif" }}>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 24 }}>
        <div>
          <h1 style={{ fontSize: 24, fontWeight: 800, margin: 0, background: 'linear-gradient(90deg,#38bdf8,#818cf8)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
            📋 {t('historyTitle')}
          </h1>
          <p style={{ color: '#64748b', margin: '4px 0 0', fontSize: 13 }}>{t('historySubtitle')}</p>
        </div>
        <button onClick={() => i18n.changeLanguage(i18n.language === 'en' ? 'ta' : 'en')}
          style={{ background: '#1e293b', border: '1px solid #334155', borderRadius: 10, color: '#38bdf8', padding: '8px 14px', fontSize: 12, fontWeight: 700, cursor: 'pointer' }}>
          🌐 {t('switchLanguage')}
        </button>
      </div>

      {/* Search */}
      <input
        type="text"
        placeholder={`🔍 Filter by crop name…`}
        value={filter}
        onChange={(e) => setFilter(e.target.value)}
        style={{ width: '100%', padding: '10px 14px', borderRadius: 10, border: '1px solid #334155', background: '#1e293b', color: '#f1f5f9', fontSize: 13, marginBottom: 18, boxSizing: 'border-box' }}
      />

      {loading && (
        <div style={{ textAlign: 'center', color: '#475569', padding: 40 }}>Loading…</div>
      )}
      {error && (
        <div style={{ padding: '14px 18px', borderRadius: 12, border: '1px solid #ef444466', background: '#ef444411', color: '#f87171', fontSize: 13 }}>
          {error}
        </div>
      )}
      {!loading && !error && filtered.length === 0 && (
        <div style={{ textAlign: 'center', color: '#475569', padding: 60, fontSize: 15 }}>
          🌱 {t('historyEmpty')}
        </div>
      )}

      {!loading && filtered.length > 0 && (
        <div style={{ overflowX: 'auto', borderRadius: 14, border: '1px solid #334155' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 13 }}>
            <thead>
              <tr style={{ background: '#1e293b', borderBottom: '1px solid #334155' }}>
                {[t('historyDate'), t('historyCrop'), t('historyConf'), 'N/P/K', t('historySoil'), t('historyBudget'), 'Reason'].map((h) => (
                  <th key={h} style={{ padding: '12px 14px', textAlign: 'left', fontSize: 10, color: '#64748b', textTransform: 'uppercase', letterSpacing: '0.07em', fontWeight: 700, whiteSpace: 'nowrap' }}>
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {filtered.map((r, i) => (
                <tr key={r.id ?? i}
                  style={{ borderBottom: '1px solid #1e293b', background: i % 2 === 0 ? '#0f172a' : '#0a0f1a', transition: 'background 0.15s' }}
                  onMouseEnter={(e) => e.currentTarget.style.background = '#1e293b'}
                  onMouseLeave={(e) => e.currentTarget.style.background = i % 2 === 0 ? '#0f172a' : '#0a0f1a'}>
                  <td style={{ padding: '12px 14px', color: '#64748b', whiteSpace: 'nowrap' }}>
                    {r.createdAt ? new Date(r.createdAt).toLocaleString() : '—'}
                  </td>
                  <td style={{ padding: '12px 14px', fontWeight: 700, color: '#38bdf8' }}>{r.predictedCrop}</td>
                  <td style={{ padding: '12px 14px' }}>{badge(Math.round((r.confidence ?? 0) * 100))}</td>
                  <td style={{ padding: '12px 14px', color: '#94a3b8', whiteSpace: 'nowrap' }}>
                    {r.nitrogen ?? '—'} / {r.phosphorus ?? '—'} / {r.potassium ?? '—'}
                  </td>
                  <td style={{ padding: '12px 14px', color: '#94a3b8' }}>{r.soilType ?? '—'}</td>
                  <td style={{ padding: '12px 14px', color: '#94a3b8' }}>{r.budget ?? '—'}</td>
                  <td style={{ padding: '12px 14px', color: '#64748b', maxWidth: 260 }}>
                    <span style={{ display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>
                      {r.reason ?? '—'}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

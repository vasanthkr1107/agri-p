import { useState, useEffect } from 'react';
import { getAdvisories, getWeatherForecast } from '../services/api';
import Card from '../components/Card';
import Skeleton from '../components/Skeleton';

const weatherEmoji = (cond) => {
  const c = (cond || '').toLowerCase();
  if (c.includes('rain')) return '🌧️';
  if (c.includes('sun') || c.includes('clear')) return '☀️';
  if (c.includes('cloud')) return '☁️';
  return '🌤️';
};

export default function Advisories() {
  const [advisories, setAdvisories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [location, setLocation] = useState('');
  const [forecast, setForecast] = useState(null);
  const [forecastLoading, setForecastLoading] = useState(false);
  const [forecastError, setForecastError] = useState(null);

  useEffect(() => {
    getAdvisories()
      .then((r) => setAdvisories(Array.isArray(r.data) ? r.data : []))
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  const handleCheckWeather = async (e) => {
    e.preventDefault();
    setForecastError(null);
    setForecast(null);
    if (!location.trim()) {
      setForecastError('Please enter a location (e.g. city or village).');
      return;
    }
    setForecastLoading(true);
    try {
      const res = await getWeatherForecast({ location: location.trim() });
      setForecast(res.data || null);
    } catch (err) {
      setForecastError(err.response?.data?.message || err.message || 'Failed to fetch forecast');
    } finally {
      setForecastLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="space-y-6">
        <div>
          <Skeleton height={32} width={140} className="mb-2" />
          <Skeleton height={20} width={240} />
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {[1, 2, 3, 4, 5, 6].map((i) => (
            <Card key={i} hover={false}>
              <Skeleton height={28} className="mb-3" />
              <Skeleton height={60} />
            </Card>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Advisories</h1>
        <p className="text-gray-500">Weather-based crop recommendations</p>
      </div>

      {/* Weather checking menu for crop advisory */}
      <Card glass>
        <div className="flex flex-col lg:flex-row lg:items-start gap-6">
          <form onSubmit={handleCheckWeather} className="space-y-3 w-full lg:w-1/2">
            <h3 className="font-semibold text-gray-900">Weather Check</h3>
            <p className="text-sm text-gray-500">
              Enter a location to fetch short-term weather forecast for crop advisory.
            </p>
            <div className="flex flex-col sm:flex-row gap-3">
              <input
                type="text"
                value={location}
                onChange={(e) => setLocation(e.target.value)}
                placeholder="e.g. Chennai, Coimbatore, Tirunelveli"
                className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary"
              />
              <button
                type="submit"
                disabled={forecastLoading}
                className="px-5 py-2.5 bg-primary hover:bg-primary-dark text-white font-medium rounded-lg transition-colors disabled:opacity-60"
              >
                {forecastLoading ? 'Checking…' : 'Check Weather'}
              </button>
            </div>
            {forecastError && <p className="text-sm text-red-600">{forecastError}</p>}
          </form>

          {forecast && (
            <div className="w-full lg:w-1/2 border-t lg:border-t-0 lg:border-l border-gray-200 pt-4 lg:pt-0 lg:pl-6">
              <h4 className="font-semibold text-gray-900 mb-2">Forecast summary</h4>
              <p className="text-sm text-gray-600 mb-2">
                Location:{' '}
                <span className="font-medium">
                  {forecast?.timezone || forecast?.latitude + ', ' + forecast?.longitude}
                </span>
              </p>
              {forecast.daily && (
                <div className="text-sm text-gray-700 space-y-1">
                  <p>
                    <span className="font-medium">Daily max temp:</span>{' '}
                    {forecast.daily.temperature_2m_max?.[0]}°C
                  </p>
                  <p>
                    <span className="font-medium">Daily min temp:</span>{' '}
                    {forecast.daily.temperature_2m_min?.[0]}°C
                  </p>
                  <p className="text-xs text-gray-500">
                    Raw weathercode: {forecast.daily.weathercode?.[0]} (can be mapped to advisory rules).
                  </p>
                </div>
              )}
            </div>
          )}
        </div>
      </Card>

      {error && <p className="text-red-600">{error}</p>}

      {advisories.length === 0 ? (
        <Card>
          <p className="text-gray-500 text-center py-8">No advisories yet</p>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {advisories.map((a) => (
            <Card key={a.id} glass hover className="group">
              <div className="flex items-center gap-2 mb-3">
                <span className="text-2xl animate-pulse">{weatherEmoji(a.weatherCondition)}</span>
                <span className="font-semibold text-primary-dark">{a.weatherCondition}</span>
              </div>
              <p className="text-gray-700 mb-3">{a.message}</p>
              <div className="pt-3 border-t border-gray-200">
                <span className="text-sm font-medium text-gray-500">Recommendation</span>
                <p className="text-sm text-gray-700 mt-1">{a.recommendation || '—'}</p>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}

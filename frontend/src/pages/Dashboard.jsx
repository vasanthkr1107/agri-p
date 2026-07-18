import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getDiseases, getAdvisories, getPredictions, getWeather } from '../services/api';
import Card from '../components/Card';
import Skeleton from '../components/Skeleton';
import Loader from '../components/Loader';

function StatCard({ value, label, icon, to }) {
  const content = (
    <div className="flex items-center gap-4">
      <div className="w-12 h-12 rounded-xl bg-primary/10 flex items-center justify-center text-2xl">{icon}</div>
      <div>
        <p className="text-2xl font-bold text-gray-900">{value}</p>
        <p className="text-sm text-gray-500">{label}</p>
      </div>
    </div>
  );
  return to ? (
    <Link to={to} className="block">
      <Card glass hover>{content}</Card>
    </Link>
  ) : (
    <Card glass hover>{content}</Card>
  );
}

function SimpleBarChart({ data }) {
  const max = Math.max(...data.map((d) => d.value), 1);
  return (
    <div className="space-y-3">
      {data.map((item) => (
        <div key={item.label}>
          <div className="flex justify-between text-sm mb-1">
            <span className="font-medium">{item.label}</span>
            <span className="text-gray-500">{item.value}</span>
          </div>
          <div className="h-2 bg-gray-200 rounded-full overflow-hidden">
            <div
              className="h-full bg-primary rounded-full transition-all duration-500"
              style={{ width: `${(item.value / max) * 100}%` }}
            />
          </div>
        </div>
      ))}
    </div>
  );
}

export default function Dashboard() {
  const [stats, setStats] = useState({ diseases: 0, advisories: 0, predictions: [], weather: [] });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const load = async () => {
      try {
        const [d, a, p, w] = await Promise.all([
          getDiseases().then((r) => r.data).catch(() => []),
          getAdvisories().then((r) => r.data).catch(() => []),
          getPredictions().then((r) => r.data).catch(() => []),
          getWeather().then((r) => r.data).catch(() => []),
        ]);
        setStats({
          diseases: Array.isArray(d) ? d.length : 0,
          advisories: Array.isArray(a) ? a.length : 0,
          predictions: Array.isArray(p) ? p.slice(-5).reverse() : [],
          weather: Array.isArray(w) ? w : [],
        });
      } catch (e) {
        setError(e.message);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const chartData = [
    { label: 'Diseases', value: stats.diseases },
    { label: 'Advisories', value: stats.advisories },
    { label: 'Predictions', value: stats.predictions.length },
  ].filter((d) => d.value > 0);

  if (loading) {
    return (
      <div className="space-y-6">
        <div>
          <Skeleton height={32} width={200} className="mb-2" />
          <Skeleton height={20} width={300} />
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {[1, 2, 3].map((i) => (
            <Card key={i} hover={false}>
              <Skeleton height={48} />
            </Card>
          ))}
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-red-600 p-4">
        Error loading dashboard. Is the backend running on http://localhost:8080?
      </div>
    );
  }

  const latestWeather = stats.weather[stats.weather.length - 1];

  return (
    <div className="space-y-6">
<div>
        <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
        <p className="text-lg text-gray-600">Smart Crop Advisory System – Professional Farming Insights</p>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        <StatCard value={stats.diseases} label="Total Diseases" icon="🩺" to="/diseases" />
        <StatCard value={stats.advisories} label="Total Advisories" icon="📋" to="/advisories" />
        <StatCard
          value={stats.predictions.length}
          label="Recent Predictions"
          icon="🔍"
          to="/predictions"
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card glass>
          <h3 className="font-semibold text-gray-900 mb-4">Prediction Statistics</h3>
          {chartData.length > 0 ? (
            <SimpleBarChart data={chartData} />
          ) : (
            <p className="text-gray-500 text-sm">No data yet</p>
          )}
        </Card>
        <Card glass>
          <h3 className="font-semibold text-gray-900 mb-4">Weather Summary</h3>
          {latestWeather ? (
            <div className="space-y-2">
              <p><span className="font-medium">Location:</span> {latestWeather.location}</p>
              <p><span className="font-medium">Condition:</span> {latestWeather.condition}</p>
              <p><span className="font-medium">Temperature:</span> {latestWeather.temperature}°C</p>
              <p><span className="font-medium">Humidity:</span> {latestWeather.humidity}%</p>
            </div>
          ) : (
            <p className="text-gray-500 text-sm">No weather data</p>
          )}
        </Card>
      </div>

      <Card glass>
        <h3 className="font-semibold text-gray-900 mb-4">Recent Predictions</h3>
        {stats.predictions.length > 0 ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
            {stats.predictions.map((p) => (
              <Link key={p.id} to="/predictions">
                <div className="p-3 rounded-lg bg-gray-50 hover:bg-gray-100 transition-colors border border-gray-100">
                  <p className="font-medium text-primary">{p.predictedDisease || 'Unknown'}</p>
                  <p className="text-sm text-gray-500">{(p.confidence * 100).toFixed(0)}% confidence</p>
                </div>
              </Link>
            ))}
          </div>
        ) : (
          <p className="text-gray-500 text-sm">No predictions yet</p>
        )}
      </Card>
    </div>
  );
}

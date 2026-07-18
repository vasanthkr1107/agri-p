import { useState, useEffect } from 'react';
import { getDiseases } from '../services/api';
import Card from '../components/Card';
import Skeleton from '../components/Skeleton';
import Loader from '../components/Loader';

export default function Diseases() {
  const [diseases, setDiseases] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState('');

  useEffect(() => {
    getDiseases()
      .then((r) => setDiseases(Array.isArray(r.data) ? r.data : []))
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  const filtered = diseases.filter(
    (d) =>
      !search ||
      d.name?.toLowerCase().includes(search.toLowerCase()) ||
      d.symptoms?.toLowerCase().includes(search.toLowerCase()) ||
      d.solution?.toLowerCase().includes(search.toLowerCase())
  );

  if (loading) {
    return (
      <div className="space-y-6">
        <div>
          <Skeleton height={32} width={180} className="mb-2" />
          <Skeleton height={20} width={280} />
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {[1, 2, 3, 4, 5, 6].map((i) => (
            <Card key={i} hover={false}>
              <Skeleton height={24} className="mb-3" />
              <Skeleton height={16} className="mb-2" />
              <Skeleton height={48} />
            </Card>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Disease Library</h1>
        <p className="text-gray-500">Browse crop diseases with symptoms and solutions</p>
      </div>

      <div className="flex flex-col sm:flex-row gap-4">
        <input
          type="search"
          placeholder="Search diseases..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary"
        />
      </div>

      {error && <p className="text-red-600">{error}</p>}

      {filtered.length === 0 ? (
        <Card>
          <p className="text-gray-500 text-center py-8">No diseases found</p>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {filtered.map((d) => (
            <Card key={d.id} glass hover className="group">
              <h3 className="font-semibold text-primary-dark text-lg mb-2 group-hover:text-primary transition-colors">
                {d.name}
              </h3>
              <div className="space-y-2 text-sm">
                <div>
                  <span className="text-gray-500 font-medium">Symptoms</span>
                  <p className="text-gray-700 mt-0.5">{d.symptoms || '—'}</p>
                </div>
                <div>
                  <span className="text-gray-500 font-medium">Solution</span>
                  <p className="text-gray-700 mt-0.5">{d.solution || '—'}</p>
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}

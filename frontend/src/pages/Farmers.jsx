import { useState, useEffect } from 'react';
import { createUser, getAdminUsers, deleteAdminUser } from '../services/api';
import { auth } from '../services/auth';
import Card from '../components/Card';
import Skeleton from '../components/Skeleton';
import Loader from '../components/Loader';

export default function Farmers() {
  const isAdmin = auth.isAdmin();
  const [farmers, setFarmers] = useState([]);
  const [form, setForm] = useState({ name: '', phone: '', location: '', password: '' });
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [deletingId, setDeletingId] = useState(null);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  const load = () => {
    if (!isAdmin) {
      setLoading(false);
      return;
    }
    setLoading(true);
    getAdminUsers()
      .then((r) => setFarmers(Array.isArray(r.data) ? r.data : []))
      .catch((e) => setError(e.response?.data?.message || e.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps -- isAdmin stable for session lifetime
  }, [isAdmin]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!isAdmin) return;
    setError(null);
    setSuccess(false);
    setSubmitting(true);
    try {
      await createUser({
        name: form.name,
        phone: form.phone,
        location: form.location || '',
        password: form.password,
      });
      setSuccess(true);
      setForm({ name: '', phone: '', location: '', password: '' });
      load();
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Registration failed');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    if (!isAdmin || !window.confirm('Remove this user from the system?')) return;
    setDeletingId(id);
    setError(null);
    try {
      await deleteAdminUser(id);
      load();
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Delete failed');
    } finally {
      setDeletingId(null);
    }
  };

  if (!isAdmin) {
    return (
      <div className="space-y-6 max-w-2xl">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">User management</h1>
          <p className="text-gray-500">Restricted area</p>
        </div>
        <Card glass className="border-amber-200 bg-amber-50/50">
          <p className="text-gray-800 font-medium">Only administrators can view or manage user accounts.</p>
          <p className="text-sm text-gray-600 mt-2">
            Farmer lists, contact details, and account removal are hidden from standard users. If you need access,
            contact your system administrator.
          </p>
        </Card>
      </div>
    );
  }

  if (loading && farmers.length === 0) {
    return (
      <div className="space-y-6">
        <div>
          <Skeleton height={32} width={120} className="mb-2" />
          <Skeleton height={20} width={200} />
        </div>
        <Skeleton height={200} />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">User management</h1>
        <p className="text-gray-500">Register users, view directory, and remove accounts (admin only)</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card glass>
          <h3 className="font-semibold text-gray-900 mb-4">Register user</h3>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Name *</label>
              <input
                type="text"
                value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Phone *</label>
              <input
                type="tel"
                value={form.phone}
                onChange={(e) => setForm({ ...form, phone: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Location</label>
              <input
                type="text"
                value={form.location}
                onChange={(e) => setForm({ ...form, location: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Password *</label>
              <input
                type="password"
                value={form.password}
                onChange={(e) => setForm({ ...form, password: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary"
                minLength={4}
                required
              />
              <p className="text-xs text-gray-500 mt-1">Minimum 4 characters</p>
            </div>
            {error && <p className="text-sm text-red-600">{error}</p>}
            {success && <p className="text-sm text-green-600">User registered successfully.</p>}
            <button
              type="submit"
              disabled={submitting}
              className="w-full py-2.5 bg-primary hover:bg-primary-dark text-white font-medium rounded-lg transition-colors disabled:opacity-60 flex items-center justify-center gap-2"
            >
              {submitting ? <Loader size="sm" /> : 'Register user'}
            </button>
          </form>
        </Card>

        <Card glass>
          <h3 className="font-semibold text-gray-900 mb-4">All users</h3>
          {farmers.length === 0 ? (
            <p className="text-gray-500 py-8 text-center">No users yet</p>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-gray-200">
                    <th className="text-left py-2 font-medium text-gray-600">Name</th>
                    <th className="text-left py-2 font-medium text-gray-600">Phone</th>
                    <th className="text-left py-2 font-medium text-gray-600">Location</th>
                    <th className="text-left py-2 font-medium text-gray-600">Role</th>
                    <th className="text-right py-2 font-medium text-gray-600 w-24">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {farmers.map((f) => (
                    <tr key={f.id} className="border-b border-gray-100 hover:bg-gray-50">
                      <td className="py-3">{f.name}</td>
                      <td className="py-3">{f.phone}</td>
                      <td className="py-3">{f.location || '—'}</td>
                      <td className="py-3">
                        <span
                          className={
                            f.role === 'ADMIN'
                              ? 'text-amber-800 bg-amber-100 px-2 py-0.5 rounded text-xs font-medium'
                              : 'text-gray-600'
                          }
                        >
                          {f.role || 'USER'}
                        </span>
                      </td>
                      <td className="py-3 text-right">
                        <button
                          type="button"
                          onClick={() => handleDelete(f.id)}
                          disabled={deletingId === f.id}
                          className="text-red-600 hover:text-red-800 text-xs font-medium disabled:opacity-50"
                        >
                          {deletingId === f.id ? '…' : 'Remove'}
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>
      </div>
    </div>
  );
}

import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { auth } from '../services/auth';
import Card from '../components/Card';
import Loader from '../components/Loader';

export default function Login() {
  const [identifier, setIdentifier] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const session = await auth.login(identifier, password);
      if (session) {
        navigate('/dashboard');
      } else {
        setError('Invalid phone/name or password');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-green-50 to-green-100 p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-6">
          <Link to="/" className="text-sm text-gray-500 hover:text-primary-dark font-medium">
            ← Back to home
          </Link>
        </div>
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-primary-dark">🌾 Smart Crop</h1>
          <p className="text-gray-500 mt-1">Advisory System</p>
        </div>
        <Card glass className="animate-[fadeIn_0.4s_ease-out]">
          <h2 className="text-xl font-semibold mb-6">Sign in</h2>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Phone or Name</label>
              <input
                type="text"
                value={identifier}
                onChange={(e) => setIdentifier(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary"
                placeholder="Enter phone or name"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-primary"
                placeholder="Enter password"
                required
              />
            </div>
            {error && <p className="text-sm text-red-600">{error}</p>}
            <button
              type="submit"
              disabled={loading}
              className="w-full py-2.5 bg-primary hover:bg-primary-dark text-white font-medium rounded-lg transition-colors disabled:opacity-60 flex items-center justify-center gap-2"
            >
              {loading ? <Loader size="sm" /> : 'Sign in'}
            </button>
          </form>
          <p className="mt-4 text-center text-sm text-gray-500">
            Don&apos;t have an account?{' '}
            <Link to="/signup" className="text-primary font-medium hover:underline">Sign up</Link>
          </p>
          <p className="mt-4 text-xs text-gray-500 text-center leading-relaxed border-t border-gray-100 pt-4">
            Default admin (change in <code className="text-gray-600">application.properties</code>): phone{' '}
            <strong>9999999999</strong>, password <strong>Admin@123</strong>
          </p>
        </Card>
      </div>
    </div>
  );
}

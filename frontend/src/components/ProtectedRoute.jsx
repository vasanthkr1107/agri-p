import { Navigate, useLocation } from 'react-router-dom';
import { auth } from '../services/auth';

export default function ProtectedRoute({ children }) {
  const location = useLocation();
  const session = auth.getSession();

  if (!session?.token) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }
  return children;
}

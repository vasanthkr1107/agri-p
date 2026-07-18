import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { auth } from './services/auth';
import ProtectedRoute from './components/ProtectedRoute';
import Layout from './components/Layout';
import Login from './pages/Login';
import Signup from './pages/Signup';
import Dashboard from './pages/Dashboard';
import Diseases from './pages/Diseases';
import Advisories from './pages/Advisories';
import Predictions from './pages/Predictions';
import Farmers from './pages/Farmers';
import CropSuggestionPage from './pages/CropSuggestionPage';
import PredictionHistory from './pages/PredictionHistory';
import PesticideRecommendation from './pages/PesticideRecommendation';
import Landing from './pages/Landing';

import { Toaster } from 'react-hot-toast';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/"
          element={
            auth.isAuthenticated() ? (
              <Navigate to="/dashboard" replace />
            ) : (
              <Landing />
            )
          }
        />
        <Route
          path="/login"
          element={
            auth.isAuthenticated() ? (
              <Navigate to="/dashboard" replace />
            ) : (
              <Login />
            )
          }
        />
        <Route
          path="/signup"
          element={
            auth.isAuthenticated() ? (
              <Navigate to="/dashboard" replace />
            ) : (
              <Signup />
            )
          }
        />
        <Route
          element={
            <ProtectedRoute>
              <Layout />
            </ProtectedRoute>
          }
        >
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="diseases" element={<Diseases />} />
          <Route path="advisories" element={<Advisories />} />
          <Route path="predictions" element={<Predictions />} />
          <Route path="farmers" element={<Farmers />} />
          <Route path="crop-suggestion" element={<CropSuggestionPage />} />
          <Route path="crop-history" element={<PredictionHistory />} />
          <Route path="pesticide-recommendation" element={<PesticideRecommendation />} />

        </Route>
        <Route
          path="*"
          element={
            <Navigate to={auth.isAuthenticated() ? '/dashboard' : '/'} replace />
          }
        />
      </Routes>
      <Toaster position="top-right" />
    </BrowserRouter>
  );
}

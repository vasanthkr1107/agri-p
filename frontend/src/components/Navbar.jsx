import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { auth } from '../services/auth';

export default function Navbar({ onMenuClick, sidebarOpen = false, user }) {
  const isAdmin = auth.isAdmin();
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const navigate = useNavigate();
  const session = user || auth.getSession()?.user;

  const handleLogout = () => {
    auth.logout();
    setDropdownOpen(false);
    navigate('/');
  };

  return (
    <header className="bg-white border-b border-emerald-100 shadow-sm sticky top-0 z-30">
      <div className="flex items-center justify-between h-16 px-4 lg:px-6 gap-3">
        <div className="flex items-center gap-2 sm:gap-3 min-w-0 flex-1">
          <button
            type="button"
            onClick={onMenuClick}
            className="shrink-0 flex items-center gap-2 pl-1 pr-3 py-2 rounded-xl border border-emerald-200 bg-emerald-50/80 text-primary-dark hover:bg-emerald-100 hover:border-primary/40 transition-colors"
            aria-label={sidebarOpen ? 'Close menu' : 'Open menu'}
            aria-expanded={sidebarOpen}
          >
            <svg className="w-6 h-6 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden>
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
            </svg>
            <span className="text-sm font-semibold hidden sm:inline">Menu</span>
          </button>
          <div className="flex items-center gap-2 min-w-0">
            <span className="text-xl shrink-0">🌱</span>
            <span className="font-bold text-primary text-lg sm:text-xl truncate">Smart Crop Advisory</span>
          </div>
        </div>
        <div className="relative flex items-center gap-2">
          {isAdmin && (
            <span className="hidden sm:inline text-xs font-semibold uppercase tracking-wide text-amber-800 bg-amber-100 px-2 py-1 rounded-md border border-amber-200">
              Admin
            </span>
          )}
          <button
            type="button"
            onClick={() => setDropdownOpen(!dropdownOpen)}
            className="flex items-center gap-2 p-2 rounded-lg hover:bg-gray-100"
          >
            <div className="w-8 h-8 rounded-full bg-primary/20 flex items-center justify-center text-primary font-semibold">
              {session?.name?.[0] || 'U'}
            </div>
            <span className="hidden sm:block text-sm font-medium">{session?.name || 'User'}</span>
            <svg className={`w-4 h-4 transition-transform ${dropdownOpen ? 'rotate-180' : ''}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
            </svg>
          </button>
          {dropdownOpen && (
            <>
              <div className="fixed inset-0" onClick={() => setDropdownOpen(false)} aria-hidden="true" />
              <div className="absolute right-0 mt-2 w-48 bg-white rounded-xl shadow-lg border border-gray-200 py-1 z-50">
                <div className="px-4 py-2 border-b border-gray-100">
                  <p className="text-sm font-medium">{session?.name}</p>
                  <p className="text-xs text-gray-500">{session?.location || session?.phone}</p>
                </div>
                <button
                  type="button"
                  onClick={handleLogout}
                  className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 flex items-center gap-2"
                >
                  <span>🚪</span> Logout
                </button>
              </div>
            </>
          )}
        </div>
      </div>
    </header>
  );
}

import { NavLink } from 'react-router-dom';

import { auth } from '../services/auth';

const baseNavItems = [
  { to: '/dashboard', label: 'Dashboard', icon: '📊' },
  { to: '/diseases', label: 'Diseases', icon: '🩺' },
  { to: '/advisories', label: 'Advisories', icon: '📋' },
  { to: '/predictions', label: 'Predictions', icon: '🔍' },
  { to: '/farmers', label: 'Farmers', icon: '👨‍🌾', adminLabel: 'Users (admin)' },
  { to: '/crop-suggestion', label: 'Crop Suggestion', icon: '🤖' },
  { to: '/crop-history', label: 'My History', icon: '📋' },
  { to: '/pesticide-recommendation', label: 'Pesticides', icon: '🧪' },

];

export default function Sidebar({ open, onClose }) {
  const isAdmin = auth.isAdmin();
  const navItems = baseNavItems.map((item) =>
    item.to === '/farmers' && isAdmin ? { ...item, label: item.adminLabel || item.label } : item
  );

  return (
    <>
      {open && (
        <div
          className="fixed inset-0 bg-black/40 z-40"
          onClick={onClose}
          aria-hidden="true"
        />
      )}
      <aside
        className={`
          fixed top-0 left-0 h-full w-64 max-w-[85vw] bg-primary-dark text-white z-50
          transform transition-transform duration-300 ease-in-out shadow-xl shadow-black/20
          ${open ? 'translate-x-0' : '-translate-x-full'}
        `}
        aria-hidden={!open}
      >
        <div className="flex flex-col h-full p-4">
        <div className="text-lg font-semibold mb-6 pt-4 text-white">Smart Crop Advisory</div>
          <nav className="flex-1 space-y-1">
            {navItems.map(({ to, label, icon }) => (
              <NavLink
                key={to}
                to={to}
                onClick={() => onClose?.()}
                className={({ isActive }) =>
                  `flex items-center gap-3 px-4 py-3 rounded-lg transition-colors ${
                    isActive ? 'bg-white/20 text-white' : 'hover:bg-white/10'
                  }`
                }
              >
                <span className="text-lg">{icon}</span>
                <span>{label}</span>
              </NavLink>
            ))}
          </nav>
        </div>
      </aside>
    </>
  );
}

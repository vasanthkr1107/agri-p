import { useState, useEffect } from 'react';
import { Outlet, Link } from 'react-router-dom';
import Navbar from './Navbar';
import Sidebar from './Sidebar';
import { auth } from '../services/auth';

export default function Layout() {
  const [sidebarOpen, setSidebarOpen] = useState(
    () => typeof window !== 'undefined' && window.innerWidth >= 1024
  );
  const user = auth.getSession()?.user;

  useEffect(() => {
    const mq = window.matchMedia('(min-width: 1024px)');
    const onChange = () => {
      if (mq.matches) setSidebarOpen(true);
      else setSidebarOpen(false);
    };
    mq.addEventListener('change', onChange);
    return () => mq.removeEventListener('change', onChange);
  }, []);

  return (
    <div className="min-h-screen bg-gray-50 flex">
      <Sidebar open={sidebarOpen} onClose={() => setSidebarOpen(false)} />
      <div className="flex-1 flex flex-col min-w-0 min-h-screen">
        <Navbar
          sidebarOpen={sidebarOpen}
          onMenuClick={() => setSidebarOpen((o) => !o)}
          user={user}
        />
        <main className="flex-1 p-4 lg:p-6 overflow-auto relative">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

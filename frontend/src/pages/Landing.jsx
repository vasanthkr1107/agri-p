import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { auth } from '../services/auth';
import Card from '../components/Card';

const navLinks = [
  { href: '#top', label: 'Home' },
  { href: '#features', label: 'Features' },
  { href: '#pricing', label: 'Pricing' },
  { href: '#advisories', label: 'Advisories' },
  { href: '#crop-ai', label: 'Crop AI' },
];

export default function Landing() {
  const navigate = useNavigate();
  const [mobileOpen, setMobileOpen] = useState(false);
  const [activeNav, setActiveNav] = useState('Home');

  const goApp = () => {
    if (auth.isAuthenticated()) {
      navigate('/dashboard');
    } else {
      navigate('/signup');
    }
  };

  return (
    <div
      id="top"
      className="min-h-screen bg-gradient-to-b from-emerald-50/90 via-white to-green-50/50 text-gray-900"
    >
      <div
        className="fixed inset-0 pointer-events-none opacity-40"
        style={{
          backgroundImage: `radial-gradient(circle at 1px 1px, rgb(16 185 129 / 0.12) 1px, transparent 0)`,
          backgroundSize: '44px 44px',
        }}
        aria-hidden
      />

      <header className="relative z-20 sticky top-0 border-b border-emerald-100/80 bg-white/85 backdrop-blur-md shadow-sm shadow-emerald-900/5">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 flex items-center justify-between h-16 lg:h-[4.25rem]">
          <Link to="/" className="flex items-center gap-2.5 shrink-0 group">
            <span className="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-br from-primary to-primary-dark text-white text-lg shadow-lg shadow-primary/35 ring-2 ring-white">
              🌱
            </span>
            <div className="leading-tight">
              <span className="font-bold text-primary-dark text-lg tracking-tight group-hover:text-primary transition-colors">
                Smart Crop
              </span>
              <span className="block text-[10px] font-semibold uppercase tracking-[0.2em] text-primary">
                AI Powered
              </span>
            </div>
          </Link>

          <nav className="hidden lg:flex items-center gap-1">
            {navLinks.map(({ href, label }) => (
              <a
                key={href}
                href={href}
                onClick={() => setActiveNav(label)}
                className={`px-4 py-2 rounded-full text-sm font-medium transition-colors ${
                  activeNav === label
                    ? 'bg-emerald-100 text-primary-dark shadow-sm'
                    : 'text-gray-600 hover:bg-emerald-50/80 hover:text-primary-dark'
                }`}
              >
                {label}
              </a>
            ))}
          </nav>

          <div className="flex items-center gap-2 sm:gap-3">
            <Link
              to="/login"
              className="hidden sm:inline-flex px-4 py-2 rounded-full text-sm font-medium text-primary-dark border border-emerald-200 hover:bg-emerald-50 transition-colors"
            >
              Login
            </Link>
            <button
              type="button"
              onClick={goApp}
              className="inline-flex items-center justify-center rounded-full bg-primary hover:bg-primary-dark text-white text-sm font-semibold px-4 py-2.5 shadow-lg shadow-primary/35 transition-colors"
            >
              {auth.isAuthenticated() ? 'Dashboard' : 'Get started'}
            </button>
            <button
              type="button"
              className="lg:hidden p-2 rounded-lg border border-emerald-200 text-primary-dark"
              aria-label="Menu"
              onClick={() => setMobileOpen((o) => !o)}
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                {mobileOpen ? (
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                ) : (
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
                )}
              </svg>
            </button>
          </div>
        </div>

        {mobileOpen && (
          <div className="lg:hidden border-t border-emerald-100 bg-white px-4 py-4 flex flex-col gap-1 shadow-inner">
            {navLinks.map(({ href, label }) => (
              <a
                key={href}
                href={href}
                className="px-4 py-3 rounded-xl text-sm font-medium text-gray-700 hover:bg-emerald-50 hover:text-primary-dark"
                onClick={() => {
                  setMobileOpen(false);
                  setActiveNav(label);
                }}
              >
                {label}
              </a>
            ))}
            <Link
              to="/login"
              className="px-4 py-3 rounded-xl text-sm font-semibold text-primary"
              onClick={() => setMobileOpen(false)}
            >
              Login
            </Link>
          </div>
        )}
      </header>

      <main className="relative z-10">
        <section className="max-w-6xl mx-auto px-4 sm:px-6 pt-10 pb-16 lg:pt-14 lg:pb-24">
          <div className="grid lg:grid-cols-2 gap-12 lg:gap-10 items-center">
            <div className="animate-fadeIn">
              <div className="inline-flex items-center gap-2 rounded-full bg-emerald-100/90 text-primary-dark text-xs font-semibold px-3 py-1.5 mb-6 border border-emerald-200/80">
                <span aria-hidden>✨</span> AI-Powered Farm Platform
              </div>
              <h1 className="text-4xl sm:text-5xl lg:text-[3.25rem] font-extrabold tracking-tight text-gray-900 leading-[1.1]">
                Grow Smarter Harvests with{' '}
                <span className="text-transparent bg-clip-text bg-gradient-to-r from-primary via-primary-light to-gold">
                  AI
                </span>
              </h1>
              <p className="mt-5 text-lg text-gray-600 max-w-xl leading-relaxed">
                Disease detection from leaf images, crop recommendations from soil and season, and weather-aware
                advisories—plan inputs and irrigation with confidence.
              </p>
              <div className="mt-8 flex flex-wrap gap-3">
                <button
                  type="button"
                  onClick={goApp}
                  className="inline-flex items-center justify-center rounded-full bg-primary hover:bg-primary-dark text-white font-semibold px-8 py-3.5 shadow-glow-primary transition-colors"
                >
                  {auth.isAuthenticated() ? 'Open app' : 'Free analysis'}
                </button>
                <a
                  href="#demo"
                  className="inline-flex items-center justify-center rounded-full bg-white border-2 border-emerald-200 text-primary-dark font-semibold px-8 py-3.5 hover:border-primary hover:bg-emerald-50/50 transition-colors"
                >
                  Watch demo
                </a>
              </div>
              <div className="mt-12 flex flex-wrap gap-8 sm:gap-10 text-sm">
                <div className="relative pl-3 border-l-2 border-primary/40">
                  <p className="font-bold text-primary-dark text-xl">10K+</p>
                  <p className="text-gray-500">Fields guided</p>
                </div>
                <div className="relative pl-3 border-l-2 border-gold/60">
                  <p className="font-bold text-primary-dark text-xl">95%</p>
                  <p className="text-gray-500">Accuracy focus</p>
                </div>
                <div className="relative pl-3 border-l-2 border-primary-light/80">
                  <p className="font-bold text-primary-dark text-xl">24/7</p>
                  <p className="text-gray-500">Dashboard access</p>
                </div>
              </div>
            </div>

            <div className="relative lg:pl-2">
              <div
                className="absolute -inset-6 rounded-[2rem] opacity-90 blur-2xl"
                style={{
                  background:
                    'radial-gradient(ellipse at 30% 20%, rgba(52, 211, 153, 0.35), transparent 50%), radial-gradient(ellipse at 70% 80%, rgba(245, 158, 11, 0.12), transparent 45%)',
                }}
                aria-hidden
              />
              <div className="relative rounded-[1.75rem] bg-gradient-to-br from-primary-dark via-primary to-emerald-800 p-6 sm:p-8 shadow-2xl shadow-emerald-900/40 border border-white/15">
                <div className="absolute top-6 right-6 w-28 h-28 rounded-full bg-white/10 blur-2xl" aria-hidden />
                <p className="text-xs font-bold uppercase tracking-wider text-primary-light mb-4">Step 2 — AI reads your farm</p>
                <div className="rounded-2xl bg-white p-5 shadow-xl border border-emerald-100/80">
                  <div className="flex items-start gap-3">
                    <div className="h-12 w-12 rounded-xl bg-gradient-to-br from-emerald-100 to-emerald-50 flex items-center justify-center text-xl border border-emerald-200/80">
                      🌾
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="font-semibold text-gray-900">Recommended crop</p>
                      <p className="text-sm text-gray-500">Kharif · Loamy soil · 2.5 acres</p>
                      <div className="mt-3 flex flex-wrap gap-2">
                        <span className="inline-flex items-center gap-1 rounded-full bg-emerald-50 text-emerald-900 text-xs font-medium px-2.5 py-1 border border-emerald-100">
                          Rice <span className="text-primary">High fit</span>
                        </span>
                        <span className="inline-flex items-center gap-1 rounded-full bg-amber-50 text-amber-900 text-xs font-medium px-2.5 py-1 border border-amber-100">
                          Irrigation <span className="text-gold">Review</span>
                        </span>
                      </div>
                      <p className="mt-3 text-xs text-gray-500 leading-relaxed">
                        ML confidence plus DB-backed cost, yield, and pesticide notes—after you sign in.
                      </p>
                    </div>
                  </div>
                </div>
                <p className="mt-4 text-center text-xs text-emerald-100/95">
                  Disease model + crop API — same stack as your dashboard
                </p>
              </div>
            </div>
          </div>
        </section>

        <section id="features" className="border-t border-emerald-100/80 bg-gradient-to-b from-white to-emerald-50/30 py-16 sm:py-20">
          <div className="max-w-6xl mx-auto px-4 sm:px-6">
            <div className="text-center max-w-2xl mx-auto">
              <span className="text-primary font-semibold text-sm uppercase tracking-wide">Features</span>
              <h2 className="mt-2 text-2xl sm:text-3xl font-bold text-gray-900">Everything in one workspace</h2>
              <p className="mt-3 text-gray-600">
                Same look and feel as login and dashboard—fast insights without spreadsheet juggling.
              </p>
            </div>
            <div className="mt-12 grid sm:grid-cols-2 lg:grid-cols-3 gap-5">
              {[
                {
                  icon: '🔍',
                  title: 'Disease detection',
                  body: 'Upload leaf images and get model-backed disease labels with confidence scores.',
                },
                {
                  icon: '🤖',
                  title: 'Crop AI',
                  body: 'Soil, season, and area in—recommendations merged with your catalog and economics.',
                },
                {
                  icon: '📋',
                  title: 'Advisories',
                  body: 'Weather-linked guidance for irrigation and field operations.',
                },
                {
                  icon: '👨‍🌾',
                  title: 'Farmer records',
                  body: 'Profiles and prediction history in one secure dashboard.',
                },
                {
                  icon: '📊',
                  title: 'Dashboard',
                  body: 'At-a-glance stats and sidebar navigation to every module.',
                },
                {
                  icon: '🔐',
                  title: 'Accounts',
                  body: 'Simple signup and login; session stays on your device.',
                },
              ].map(({ icon, title, body }) => (
                <Card
                  key={title}
                  glass
                  className="border-emerald-100/60 !shadow-md hover:!shadow-lg hover:!border-primary/25"
                >
                  <span className="text-2xl" aria-hidden>
                    {icon}
                  </span>
                  <h3 className="mt-3 font-semibold text-gray-900">{title}</h3>
                  <p className="mt-2 text-sm text-gray-600 leading-relaxed">{body}</p>
                </Card>
              ))}
            </div>
          </div>
        </section>

        <section id="pricing" className="py-16 sm:py-20 border-t border-emerald-100/60 bg-white">
          <div className="max-w-6xl mx-auto px-4 sm:px-6">
            <div className="text-center">
              <span className="text-primary font-semibold text-sm uppercase tracking-wide">Pricing</span>
              <h2 className="mt-2 text-2xl sm:text-3xl font-bold text-gray-900">Simple tiers</h2>
              <p className="mt-2 text-gray-600">Start free; scale when your operation grows.</p>
            </div>
            <div className="mt-10 grid md:grid-cols-3 gap-6 max-w-5xl mx-auto">
              {[
                { name: 'Starter', price: '₹0', desc: 'Core dashboard, limited predictions', highlight: false },
                { name: 'Grower', price: '₹499', desc: 'Full AI crop + priority support', highlight: true },
                { name: 'Co-op', price: 'Custom', desc: 'Multi-farm reporting & API hooks', highlight: false },
              ].map(({ name, price, desc, highlight }) => (
                <div
                  key={name}
                  className={`rounded-2xl p-6 border-2 transition-shadow ${
                    highlight
                      ? 'border-primary bg-gradient-to-b from-emerald-50/80 to-white shadow-glow-primary scale-[1.02]'
                      : 'border-emerald-100 bg-white hover:border-emerald-200 hover:shadow-md'
                  }`}
                >
                  <h3 className="font-bold text-lg text-gray-900">{name}</h3>
                  <p className="mt-2 text-3xl font-extrabold text-primary">{price}</p>
                  <p className="mt-2 text-sm text-gray-600">{desc}</p>
                  <button
                    type="button"
                    onClick={goApp}
                    className={`mt-6 w-full rounded-full py-2.5 text-sm font-semibold transition-colors ${
                      highlight
                        ? 'bg-primary text-white hover:bg-primary-dark shadow-md shadow-primary/25'
                        : 'bg-emerald-50 text-primary-dark hover:bg-emerald-100'
                    }`}
                  >
                    {auth.isAuthenticated() ? 'Go to app' : 'Get started'}
                  </button>
                </div>
              ))}
            </div>
          </div>
        </section>

        <section
          id="advisories"
          className="relative overflow-hidden bg-gradient-to-br from-primary-dark via-[#065f46] to-primary-dark text-white py-16 sm:py-20"
        >
          <div
            className="absolute inset-0 opacity-20"
            style={{
              backgroundImage: `radial-gradient(circle at 2px 2px, white 1px, transparent 0)`,
              backgroundSize: '32px 32px',
            }}
            aria-hidden
          />
          <div className="relative max-w-6xl mx-auto px-4 sm:px-6 text-center">
            <h2 className="text-2xl sm:text-3xl font-bold">Advisories that match the weather</h2>
            <p className="mt-3 text-emerald-100 max-w-2xl mx-auto leading-relaxed">
              Humidity, rainfall, and season—irrigation notes that align with what you see in the field.
            </p>
            <Link
              to="/signup"
              className="inline-flex mt-8 rounded-full bg-white text-primary-dark font-semibold px-8 py-3 hover:bg-emerald-50 shadow-lg transition-colors"
            >
              Create free account
            </Link>
          </div>
        </section>

        <section id="crop-ai" className="py-16 sm:py-20 border-t border-emerald-100/60 bg-emerald-50/20">
          <div className="max-w-6xl mx-auto px-4 sm:px-6 flex flex-col md:flex-row items-center gap-10">
            <div className="flex-1">
              <span className="text-primary font-semibold text-sm uppercase tracking-wide">Pipeline</span>
              <h2 className="mt-2 text-2xl sm:text-3xl font-bold text-gray-900">Crop AI end-to-end</h2>
              <p className="mt-3 text-gray-600 leading-relaxed">
                FastAPI serves the model; Spring Boot enriches with your MySQL crop catalog—cost per acre, yield, profit,
                pesticides, and duration.
              </p>
              <ul className="mt-6 space-y-2.5 text-sm text-gray-700">
                {[
                  'Soil & season in, ranked crop out',
                  'Land area drives economics on the server',
                  'Same flow after signup from this page',
                ].map((t) => (
                  <li key={t} className="flex gap-2 items-start">
                    <span className="text-primary font-bold shrink-0">✓</span>
                    {t}
                  </li>
                ))}
              </ul>
            </div>
            <Card className="flex-1 w-full max-w-md border-emerald-200 bg-gradient-to-br from-emerald-50/90 to-white shadow-inner">
              <p className="text-sm font-mono text-primary-dark/80">POST /api/crops/suggest-ai</p>
              <pre className="mt-4 text-xs sm:text-sm text-gray-800 overflow-x-auto bg-white/90 rounded-xl p-4 border border-emerald-100">
                {`{
  "soilType": "Loamy",
  "season": "Kharif",
  "landArea": 2.5
}`}
              </pre>
            </Card>
          </div>
        </section>

        <section
          id="demo"
          className="py-16 sm:py-20 bg-gradient-to-r from-primary via-primary-dark to-primary text-white relative overflow-hidden"
        >
          <div
            className="absolute inset-0 opacity-10"
            style={{
              background: 'radial-gradient(circle at 20% 50%, #fbbf24, transparent 40%), radial-gradient(circle at 80% 50%, white, transparent 35%)',
            }}
            aria-hidden
          />
          <div className="relative max-w-3xl mx-auto px-4 text-center">
            <h2 className="text-2xl sm:text-3xl font-bold">See it in action</h2>
            <p className="mt-3 text-emerald-100 leading-relaxed">
              Sign up, open <strong className="text-white">Crop Suggestion</strong> in the sidebar, enter soil, season,
              and area—then review cost, profit, and irrigation in one card.
            </p>
            <button
              type="button"
              onClick={goApp}
              className="mt-8 inline-flex rounded-full bg-white text-primary-dark font-semibold px-8 py-3 hover:bg-emerald-50 shadow-xl transition-colors"
            >
              {auth.isAuthenticated() ? 'Open Crop Suggestion' : 'Try it now'}
            </button>
          </div>
        </section>

        <footer className="border-t border-emerald-100 bg-white py-10">
          <div className="max-w-6xl mx-auto px-4 sm:px-6 flex flex-col sm:flex-row items-center justify-between gap-4">
            <div className="flex items-center gap-2 text-gray-700">
              <span className="text-xl">🌱</span>
              <span className="font-semibold text-primary-dark">Smart Crop Advisory</span>
            </div>
            <div className="flex gap-8 text-sm text-gray-600">
              <Link to="/login" className="hover:text-primary font-medium transition-colors">
                Login
              </Link>
              <Link to="/signup" className="hover:text-primary font-medium transition-colors">
                Sign up
              </Link>
            </div>
          </div>
        </footer>
      </main>
    </div>
  );
}

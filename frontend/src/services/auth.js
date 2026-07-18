const AUTH_KEY = 'smart_crop_auth';

export const auth = {
  login: async (identifier, password) => {
    const api = (await import('./api.js')).default;
    const res = await api.post('/auth/login', { identifier, password });
    const { token, user } = res.data;
    if (!token || !user) {
      return null;
    }
    const session = { token, user };
    localStorage.setItem(AUTH_KEY, JSON.stringify(session));
    return session;
  },

  signup: async (name, phone, location, password) => {
    const api = (await import('./api.js')).default;
    await api.post('/users', {
      name,
      phone,
      location: location || '',
      password,
    });
    return auth.login(phone, password);
  },

  logout: () => localStorage.removeItem(AUTH_KEY),

  getSession: () => {
    try {
      const raw = localStorage.getItem(AUTH_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  },

  isAuthenticated: () => !!auth.getSession()?.token,

  isAdmin: () => auth.getSession()?.user?.role === 'ADMIN',
};

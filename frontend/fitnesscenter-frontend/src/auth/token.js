const KEY = "jwtToken"; 


export const tokenStore = {
  get: () => localStorage.getItem(KEY),
  set: (t) => localStorage.setItem(KEY, t),
  clear: () => localStorage.removeItem(KEY),
};

export function getToken() {
  return tokenStore.get();
}

export function setToken(token) {
  tokenStore.set(token);
}

export function clearToken() {
  tokenStore.clear();
}

export function parseJwt(token) {
  try {
    const base64Url = token.split(".")[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    return JSON.parse(atob(base64));
  } catch {
    return null;
  }
}

export function getRolesFromToken(token) {
  const payload = parseJwt(token);
  const roles = payload?.uloga ?? [];
  return Array.isArray(roles) ? roles : [roles];
}

export function getEmailFromToken(token) {
  const payload = parseJwt(token);
  return payload?.sub ?? null;
}

export function getRoles() {
  const t = getToken();
  if (!t) return [];
  return getRolesFromToken(t);
}

export function hasRole(role) {
  return getRoles().includes(role);
}

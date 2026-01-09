export function getToken() {
  return localStorage.getItem("token");
}

export function setToken(token) {
  localStorage.setItem("token", token);
}

export function clearToken() {
  localStorage.removeItem("token");
}

function parseJwt(token) {
  try {
    const payload = token.split(".")[1];
    return JSON.parse(atob(payload.replace(/-/g, "+").replace(/_/g, "/")));
  } catch {
    return null;
  }
}

export function getRoles() {
  const t = getToken();
  if (!t) return [];
  const p = parseJwt(t);
  const roles = p?.uloga ?? [];
  return Array.isArray(roles) ? roles : [roles];
}

export function hasRole(role) {
  // role: "ROLE_ADMIN"
  return getRoles().includes(role);
}

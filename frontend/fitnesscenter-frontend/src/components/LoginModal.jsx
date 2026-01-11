import { useEffect, useState } from "react";
import { setToken, getEmailFromToken, getRolesFromToken } from "../auth/token";

export default function LoginModal({ open, onClose, onSuccess }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [err, setErr] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (open) {
      setErr("");
 
    }
  }, [open]);

  if (!open) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErr("");
    setLoading(true);

    try {
      const res = await fetch("http://localhost:8082/api/member/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });

      const data = await res.json().catch(() => ({}));
      const token = data?.jwtToken || data?.token || data?.accessToken || null;

      if (!res.ok || !token) {
        setErr(data?.message || data?.error || "Pogrešan email/password.");
        return;
      }

      
      setToken(token);

      
      const roles = getRolesFromToken(token);
      const userEmail = getEmailFromToken(token);

     
      onSuccess?.({ token, roles, email: userEmail });

     
      onClose?.();
    } catch (e2) {
      setErr(e2?.message || "Network error");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.overlay} onClick={onClose}>
      <div style={styles.modal} onClick={(e) => e.stopPropagation()}>
        <h2 style={{ marginTop: 0 }}>Login</h2>

        <form onSubmit={handleSubmit} style={styles.form}>
          <label style={styles.label}>
            Email
            <input
              style={styles.input}
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              autoFocus
              required
            />
          </label>

          <label style={styles.label}>
            Password
            <input
              style={styles.input}
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </label>

          {err && <div style={{ color: "crimson" }}>{err}</div>}

          <div style={styles.actions}>
            <button disabled={loading} type="submit" style={styles.primaryBtn}>
              {loading ? "..." : "Login"}
            </button>
            <button type="button" onClick={onClose} style={styles.secondaryBtn}>
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

const styles = {
  overlay: {
    position: "fixed",
    inset: 0,
    background: "rgba(0,0,0,0.55)",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    zIndex: 999999,
  },
  modal: {
    background: "#fff",
    padding: "2rem",
    borderRadius: 12,
    width: "100%",
    maxWidth: 360,
    boxShadow: "0 20px 50px rgba(0,0,0,.25)",
  },
  form: { display: "flex", flexDirection: "column", gap: 12 },
  label: { display: "flex", flexDirection: "column", gap: 6, fontSize: 14 },
  input: {
    padding: "10px 12px",
    borderRadius: 10,
    border: "1px solid #ddd",
    outline: "none",
    width: "100%",
  },
  actions: { display: "flex", gap: 10, marginTop: 8, justifyContent: "flex-end" },
  primaryBtn: {
    padding: "10px 14px",
    borderRadius: 10,
    border: "none",
    cursor: "pointer",
    background: "#111",
    color: "#fff",
  },
  secondaryBtn: {
    padding: "10px 14px",
    borderRadius: 10,
    border: "1px solid #ddd",
    cursor: "pointer",
    background: "#fff",
  },
};

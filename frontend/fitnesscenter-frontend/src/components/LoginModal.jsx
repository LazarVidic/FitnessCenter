import { useState } from "react";

export default function LoginModal({ open, onClose, onSuccess }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  if (!open) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();

    const res = await fetch("http://localhost:8082/api/member/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        email: username,
        password: password,
      }),
    });

    const data = await res.json();

    if (!res.ok) {
      alert(data?.message ?? "Login failed");
      return;
    }


    localStorage.setItem("token", data.jwtToken);

    onSuccess?.(data.jwtToken);
    onClose();

  };

  return (
    <div style={styles.overlay}>
      <div style={styles.modal}>
        <h2 style={{ marginTop: 0 }}>Login</h2>

        <form onSubmit={handleSubmit} style={styles.form}>
          <label style={styles.label}>
            Username
            <input
              style={styles.input}
              placeholder="Username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </label>

          <label style={styles.label}>
            Password
            <input
              style={styles.input}
              type="password"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </label>

          <div style={styles.actions}>
            <button type="submit" style={styles.primaryBtn}>
              Login
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
    background: "rgba(0,0,0,0.5)",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    zIndex: 2000,
  },
  modal: {
    background: "#fff",
    padding: "2rem",
    borderRadius: 12,
    width: "100%",
    maxWidth: 360,
    boxShadow: "0 20px 50px rgba(0,0,0,.25)",
  },
  form: {
    display: "flex",
    flexDirection: "column",
    gap: "12px", // ✅ razmak jedan ispod drugog
  },
  label: {
    display: "flex",
    flexDirection: "column",
    gap: "6px",
    fontSize: 14,
  },
  input: {
    padding: "10px 12px",
    borderRadius: 10,
    border: "1px solid #ddd",
    outline: "none",
    width: "100%",
  },
  actions: {
    display: "flex",
    gap: "10px",
    marginTop: "8px",
    justifyContent: "flex-end",
  },
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

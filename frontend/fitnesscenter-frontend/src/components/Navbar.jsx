export default function Navbar({ onLoginClick }) {
  return (
    <header style={styles.nav}>
      <div style={styles.brand}>🏋️ FitnessCenter</div>
      <button style={styles.btn} onClick={onLoginClick}>
        Login
      </button>
    </header>
  );
}

const styles = {
  nav: {
    position: "fixed",
    top: 0,
    left: 0,
    right: 0,
    height: 64,
    background: "#111",
    color: "#fff",
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    padding: "0 24px",
    zIndex: 1000,
  },
  brand: {
    fontWeight: 700,
    fontSize: 18,
  },
  btn: {
    padding: "8px 14px",
    borderRadius: 8,
    border: "none",
    cursor: "pointer",
  },
};

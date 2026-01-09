import { FaInstagram, FaFacebook, FaLinkedin } from "react-icons/fa";

export default function Footer() {
  return (
    <footer style={styles.footer}>
      <div style={styles.links}>
        <a style={styles.link} href="https://andrea360.eu/" target="_blank" rel="noreferrer">
          <FaInstagram size={20} />
        </a>
        <a style={styles.link} href="https://andrea360.eu/" target="_blank" rel="noreferrer">
          <FaFacebook size={20} />
        </a>
        <a style={styles.link} href="https://andrea360.eu/" target="_blank" rel="noreferrer">
          <FaLinkedin size={20} />
        </a>
      </div>
    </footer>
  );
}

const styles = {
  footer: {
    height: 64,
    background: "#111",
    color: "#fff",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    padding: "0 24px",
    flexShrink: 0, 
  },
  links: { display: "flex", gap: 16 },
  link: { color: "#fff", display: "inline-flex" },
};

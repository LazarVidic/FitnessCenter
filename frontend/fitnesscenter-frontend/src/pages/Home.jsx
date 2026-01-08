import heroImg from "../assets/andrea360.png";

export default function Home() {
  return (
    <main style={styles.page}>
      <section style={styles.container}>
        
        
        <div style={styles.left}>
          <h1 style={styles.title}>🏋️ Fitness Center</h1>

          <p style={styles.subtitle}>
            Profesionalni treninzi, fleksibilni termini i online rezervacije.
          </p>

          <ul style={styles.list}>
            <li>✔ Fitness, Pilates, Yoga, CrossFit</li>
            <li>✔ Admin, Seller i User uloge</li>
            <li>✔ Rezervacija u realnom vremenu</li>
          </ul>

          <p style={styles.cta}>
            👉 Uloguj se da vidiš dostupne termine
          </p>
        </div>

        
        <div style={styles.right}>
          <img
            src={heroImg}
            alt="Fitness training"
            style={styles.image}
          />
        </div>

      </section>
    </main>
  );
}

const styles = {
  page: {
    minHeight: "100vh",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    background: "#f4f6f8",
    paddingTop: 64, 
  },

  container: {
    width: "100%",
    maxWidth: 1100,
    display: "flex",
    gap: "3rem",
    padding: "2.5rem",
    background: "#fff",
    borderRadius: 18,
    boxShadow: "0 20px 50px rgba(0,0,0,.15)",
  },

  left: {
    flex: 1,
  },

  right: {
    flex: 1,
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
  },

  title: {
    fontSize: "2.5rem",
    marginBottom: "0.5rem",
  },

  subtitle: {
    fontSize: "1.1rem",
    color: "#555",
    marginBottom: "1.5rem",
  },

  list: {
    listStyle: "none",
    padding: 0,
    marginBottom: "1.5rem",
  },

  cta: {
    fontWeight: 600,
    marginTop: "1rem",
  },

  image: {
    maxWidth: "100%",
    height: "auto",
  },
};

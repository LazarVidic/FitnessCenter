import { useEffect, useMemo, useState } from "react";
import { getAppointments } from "./api/appointments";
import AppointmentsTable from "./components/AppointmentsTable";

import Navbar from "./components/Navbar";
import LoginModal from "./components/LoginModal";
import Home from "./pages/Home";
import Footer from "./components/Footer"

const PAGE_SIZE = 3;

export default function App() {
  const [data, setData] = useState([]);
  const [q, setQ] = useState("");
  const [minCap, setMinCap] = useState("");
  const [page, setPage] = useState(1);
  const [error, setError] = useState("");

  const [loginOpen, setLoginOpen] = useState(false);
  const [loggedIn, setLoggedIn] = useState(false);

  useEffect(() => {
    if (!loggedIn) return;

    getAppointments()
      .then(setData)
      .catch((e) => {
        setError(e?.response?.status ? `Greška ${e.response.status}` : "Greška");
      });
  }, [loggedIn]);

  const filtered = useMemo(() => {
    const s = q.trim().toLowerCase();
    return data.filter((a) => {
      const matchesSearch =
        !s ||
        String(a.appointmentId).includes(s) ||
        a.startTime.toLowerCase().includes(s) ||
        a.endTime.toLowerCase().includes(s);

      const matchesCap = !minCap || a.maxCapacity >= Number(minCap);

      return matchesSearch && matchesCap;
    });
  }, [data, q, minCap]);

  const totalPages = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
  const safePage = Math.min(page, totalPages);

  const pageData = filtered.slice(
    (safePage - 1) * PAGE_SIZE,
    safePage * PAGE_SIZE
  );

  return (
    <div className="app-layout">
      <Navbar onLoginClick={() => setLoginOpen(true)} />

      {/* HOME */}
      {!loggedIn && <Home />}

      {/* APPOINTMENTS */}
      {loggedIn && (
        <div className="page">
          <h1>Fitness Center</h1>

          {error && <div className="error">{error}</div>}

          <div className="controls">
            <input
              placeholder="Search (id / date)"
              value={q}
              onChange={(e) => {
                setQ(e.target.value);
                setPage(1);
              }}
            />

            <input
              type="number"
              placeholder="Min capacity"
              value={minCap}
              onChange={(e) => {
                setMinCap(e.target.value);
                setPage(1);
              }}
            />
          </div>

          <AppointmentsTable items={pageData} />

          <div className="pagination">
            <button
              disabled={safePage === 1}
              onClick={() => setPage((p) => p - 1)}
            >
              ◀
            </button>

            <span>
              Page {safePage} / {totalPages}
            </span>

            <button
              disabled={safePage === totalPages}
              onClick={() => setPage((p) => p + 1)}
            >
              ▶
            </button>
          </div>
        </div>
      )}

       {/* FOOTER */}
      <Footer />

      <LoginModal
        open={loginOpen}
        onClose={() => setLoginOpen(false)}
        onSuccess={() => {
          setLoggedIn(true);
          setLoginOpen(false);
        }}
      />
    </div>
  );
}

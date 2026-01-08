import { useEffect, useMemo, useState } from "react";
import { getAppointments } from "../api/appointments";
import AppointmentsTable from "../components/AppointmentsTable";

const PAGE_SIZE = 3;

export default function Appointments() {
  const [data, setData] = useState([]);
  const [q, setQ] = useState("");
  const [minCap, setMinCap] = useState("");
  const [page, setPage] = useState(1);

  useEffect(() => {
    getAppointments().then(setData);
  }, []);

  // 🔍 FILTER + SEARCH
  const filtered = useMemo(() => {
    return data.filter(a => {
      const matchesSearch =
        String(a.appointmentId).includes(q) ||
        a.startTime.includes(q) ||
        a.endTime.includes(q);

      const matchesCap =
        !minCap || a.maxCapacity >= Number(minCap);

      return matchesSearch && matchesCap;
    });
  }, [data, q, minCap]);

  // 📄 PAGINATION
  const totalPages = Math.ceil(filtered.length / PAGE_SIZE);
  const pageData = filtered.slice(
    (page - 1) * PAGE_SIZE,
    page * PAGE_SIZE
  );

  return (
    <div className="page">
      <h2>Appointments</h2>

      {/* SEARCH + FILTER */}
      <div className="controls">
        <input
          placeholder="Search (id / date)"
          value={q}
          onChange={e => { setQ(e.target.value); setPage(1); }}
        />

        <input
          type="number"
          placeholder="Min capacity"
          value={minCap}
          onChange={e => { setMinCap(e.target.value); setPage(1); }}
        />
      </div>

      {/* TABLE */}
      <AppointmentsTable items={pageData} />

      {/* PAGINATION */}
      <div className="pagination">
        <button
          disabled={page === 1}
          onClick={() => setPage(p => p - 1)}
        >
          ◀
        </button>

        <span>
          Page {page} / {totalPages}
        </span>

        <button
          disabled={page === totalPages}
          onClick={() => setPage(p => p + 1)}
        >
          ▶
        </button>
      </div>
    </div>
  );
}

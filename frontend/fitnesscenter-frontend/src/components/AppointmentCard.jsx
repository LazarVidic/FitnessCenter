function fmt(dt) {
  // dt je npr. "2026-01-03T10:00:00"
  const d = new Date(dt);
  return new Intl.DateTimeFormat("sr-RS", {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(d);
}

export default function AppointmentCard({ a }) {
  return (
    <div className="card">
      <div className="cardHead">
        <div className="title">Termin #{a.appointmentId}</div>
        <div className="badge">{a.maxCapacity} mesta</div>
      </div>

      <div className="row">
        <span className="label">Početak</span>
        <span className="value">{fmt(a.startTime)}</span>
      </div>

      <div className="row">
        <span className="label">Kraj</span>
        <span className="value">{fmt(a.endTime)}</span>
      </div>

      <div className="row">
        <span className="label">Rezervacija</span>
        <span className="value">
          {a.reservation?.reservationId ? `#${a.reservation.reservationId}` : "—"}
        </span>
      </div>
    </div>
  );
}

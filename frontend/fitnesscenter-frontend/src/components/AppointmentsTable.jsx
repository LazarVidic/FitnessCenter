const formatDateTime = (dt) => new Date(dt).toLocaleString("sr-RS");

export default function AppointmentsTable({ items }) {
  return (
    <table className="table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Početak</th>
          <th>Kraj</th>
          <th>Kapacitet</th>
          <th>Rezervacija</th>
        </tr>
      </thead>

      <tbody>
        {items.map((a) => (
          <tr key={a.appointmentId}>
            <td>{a.appointmentId}</td>
            <td>{formatDateTime(a.startTime)}</td>
            <td>{formatDateTime(a.endTime)}</td>
            <td>{a.maxCapacity}</td>
            <td>{a.reservation?.reservationId ?? "—"}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

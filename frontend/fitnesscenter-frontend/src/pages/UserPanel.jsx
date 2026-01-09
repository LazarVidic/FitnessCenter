import { useEffect, useMemo, useRef, useState } from "react";
import { getToken, hasRole } from "../auth/token";
import { MapPin, Dumbbell, Calendar, CreditCard, RefreshCw, Lock } from "lucide-react";

function cx(...c) {
  return c.filter(Boolean).join(" ");
}

const TABS = [
  { key: "services", label: "Usluge", icon: Dumbbell },
  { key: "appointments", label: "Termini", icon: Calendar },
  { key: "credits", label: "Moji treninzi", icon: CreditCard },
  { key: "locations", label: "Lokacije", icon: MapPin },
];

async function apiFetch(url, options = {}) {
  const token = getToken();
  const res = await fetch(url, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {}),
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
  });

  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(data?.message || data?.error || "Greška na serveru.");
  return data;
}

function Badge({ children, variant = "default" }) {
  const styles =
    variant === "green"
      ? "bg-green-50 text-green-700 border-green-200"
      : variant === "blue"
      ? "bg-blue-50 text-blue-700 border-blue-200"
      : variant === "red"
      ? "bg-red-50 text-red-700 border-red-200"
      : "bg-gray-50 text-gray-700 border-gray-200";

  return (
    <span className={cx("inline-flex items-center rounded-full border px-2 py-0.5 text-xs font-medium", styles)}>
      {children}
    </span>
  );
}

export default function UserPanel() {
  const [tab, setTab] = useState("services");
  const [err, setErr] = useState("");
  const [loading, setLoading] = useState(false);

  const [services, setServices] = useState([]);     
  const [locations, setLocations] = useState([]);   

  
  const [appointments, setAppointments] = useState([]); 
  const [credits, setCredits] = useState([]);           


  const [serviceFilter, setServiceFilter] = useState("all");

  
  const pollingRef = useRef(null);

  const isSellerOrAdmin = useMemo(() => hasRole("ROLE_SELLER") || hasRole("ROLE_ADMIN"), []);

  async function loadAll() {
    setErr("");
    setLoading(true);
    try {
      // Services su paged: /api/services?page=0&size=100
      const [svcPage, locs] = await Promise.all([
        apiFetch("http://localhost:8082/api/services?page=0&size=100"),
        apiFetch("http://localhost:8082/api/locations"),
      ]);

      setServices(Array.isArray(svcPage?.content) ? svcPage.content : []);
      setLocations(Array.isArray(locs) ? locs : []);

      

    } catch (e) {
      setErr(e?.message || "Greška pri učitavanju.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadAll();

    // dok ne uradiš WS/SSE, polling je najlakši realtime fallback
    pollingRef.current = setInterval(() => {
      // load appointments / credits kad ih budeš imao
      // loadAll();
    }, 3000);

    return () => {
      if (pollingRef.current) clearInterval(pollingRef.current);
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const filteredAppointments = useMemo(() => {
    if (serviceFilter === "all") return appointments;
    return appointments.filter((a) => String(a.serviceId) === String(serviceFilter));
  }, [appointments, serviceFilter]);

  return (
    <div className="min-h-screen bg-gray-50">
      <main className="mx-auto max-w-6xl px-4 pt-20 pb-8">
        {/* Tabs + refresh */}
        <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
          <div className="flex flex-wrap gap-2">
            {TABS.map(({ key, label, icon: Icon }) => (
              <button
                key={key}
                type="button"
                onClick={() => setTab(key)}
                className={cx(
                  "inline-flex items-center gap-2 rounded-2xl px-4 py-2 text-sm font-semibold transition",
                  tab === key
                    ? "bg-gray-900 text-white"
                    : "bg-white text-gray-700 border border-gray-200 hover:bg-gray-50"
                )}
              >
                <Icon className="h-4 w-4" />
                {label}
              </button>
            ))}
          </div>

          <button
            type="button"
            onClick={loadAll}
            className={cx(
              "inline-flex items-center gap-2 rounded-xl px-3 py-2 text-sm font-medium",
              "border border-gray-200 bg-white hover:bg-gray-50"
            )}
            disabled={loading}
          >
            <RefreshCw className={cx("h-4 w-4", loading ? "animate-spin" : "")} />
            Osveži
          </button>
        </div>

        {err && (
          <div className="mb-4 rounded-2xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">
            {err}
          </div>
        )}

        {/* USLUGE */}
        {tab === "services" && (
          <div className="rounded-2xl border border-gray-200 bg-white shadow-sm">
            <div className="flex items-center justify-between gap-3 border-b border-gray-100 px-5 py-4">
              <div className="text-base font-semibold text-gray-900">Usluge</div>
              <div className="flex items-center gap-2">
                <Badge variant="blue">{services.length} ukupno</Badge>
                {!isSellerOrAdmin && (
                  <Badge variant="red">
                    <Lock className="mr-1 h-3.5 w-3.5" />
                    USER view
                  </Badge>
                )}
              </div>
            </div>

            <div className="p-5">
              <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                {services.map((s) => (
                  <div key={s.serviceId} className="rounded-2xl border border-gray-200 bg-gray-50 p-4">
                    <div className="flex items-start justify-between gap-3">
                      <div>
                        <div className="text-base font-semibold text-gray-900">{s.nameService || s.name || "Usluga"}</div>
                        <div className="mt-1 text-sm text-gray-700">
                          Cena: <b>{Number(s.priceService ?? s.price ?? 0).toFixed(2)} EUR</b>
                        </div>
                      </div>

                      {/* Stripe još nemaš -> disable */}
                      <button
                        type="button"
                        disabled
                        className="rounded-xl bg-gray-200 px-4 py-2 text-sm font-semibold text-gray-500 cursor-not-allowed"
                        title="Stripe još nije implementiran"
                      >
                        Kupi
                      </button>
                    </div>

                    <div className="mt-3 text-xs text-gray-500">
                      Kupovina će biti dostupna kada dodaš Stripe checkout.
                    </div>
                  </div>
                ))}

                {services.length === 0 && (
                  <div className="rounded-2xl border border-gray-200 bg-white p-6 text-sm text-gray-600">
                    Nema usluga.
                  </div>
                )}
              </div>
            </div>
          </div>
        )}

        {/* TERMINI (placeholder dok ne napraviš controller) */}
        {tab === "appointments" && (
          <div className="rounded-2xl border border-gray-200 bg-white shadow-sm">
            <div className="flex flex-wrap items-center justify-between gap-3 border-b border-gray-100 px-5 py-4">
              <div className="text-base font-semibold text-gray-900">Termini</div>

              <select
                className="h-10 rounded-xl border border-gray-200 bg-white px-3 text-sm outline-none"
                value={serviceFilter}
                onChange={(e) => setServiceFilter(e.target.value)}
              >
                <option value="all">Sve usluge</option>
                {services.map((s) => (
                  <option key={s.serviceId} value={s.serviceId}>
                    {s.nameService || s.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="p-6 text-sm text-gray-700">
              Još nema endpoint-a za Termine / Rezervacije.  
              Kad dodaš `AppointmentController` i `ReservationController`, ovde će ići tabela sa kapacitetom i rezervacijom + realtime.
            </div>

            {filteredAppointments.length > 0 && (
              <div className="overflow-auto">
                <table className="w-full text-left text-sm">
                  <thead className="bg-gray-50 text-xs text-gray-500">
                    <tr className="border-b">
                      <th className="px-4 py-3">Usluga</th>
                      <th className="px-4 py-3">Vreme</th>
                      <th className="px-4 py-3">Slobodna mesta</th>
                      <th className="px-4 py-3 text-right">Akcija</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-100">
                    {filteredAppointments.map((a) => (
                      <tr key={a.appointmentId} className="hover:bg-gray-50">
                        <td className="px-4 py-3 font-medium text-gray-900">{a.serviceName}</td>
                        <td className="px-4 py-3 text-gray-700">
                          {a.startTime} – {a.endTime}
                        </td>
                        <td className="px-4 py-3 text-gray-900">{a.remaining}</td>
                        <td className="px-4 py-3 text-right">
                          <button
                            type="button"
                            className="rounded-xl bg-gray-200 px-4 py-2 text-sm font-semibold text-gray-500 cursor-not-allowed"
                            disabled
                          >
                            Rezerviši
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}

        {/* MOJI TRENINZI (placeholder dok ne napraviš credits endpoint) */}
        {tab === "credits" && (
          <div className="rounded-2xl border border-gray-200 bg-white shadow-sm">
            <div className="border-b border-gray-100 px-5 py-4">
              <div className="text-base font-semibold text-gray-900">Moji treninzi (krediti)</div>
              <div className="mt-1 text-sm text-gray-600">
                Ovo se smanjuje kada rezervišeš termin. (Biće aktivno kad dodaš endpoint za kupljene treninge.)
              </div>
            </div>

            <div className="p-6 text-sm text-gray-700">
              Trenutno nema Stripe kupovine + nema endpoint-a za kredite, zato je ovaj tab placeholder.
            </div>

            {credits.length > 0 && (
              <div className="p-5">
                <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
                  {credits.map((c) => (
                    <div key={c.serviceId} className="rounded-2xl border border-gray-200 bg-gray-50 p-4">
                      <div className="font-semibold text-gray-900">{c.serviceName}</div>
                      <div className="mt-1 text-sm text-gray-700">
                        Preostalo: <b>{c.count}</b>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}

        {/* LOKACIJE */}
        {tab === "locations" && (
          <div className="rounded-2xl border border-gray-200 bg-white shadow-sm">
            <div className="flex items-center justify-between gap-3 border-b border-gray-100 px-5 py-4">
              <div className="text-base font-semibold text-gray-900">Lokacije</div>
              <Badge variant="blue">{locations.length} ukupno</Badge>
            </div>

            <div className="overflow-auto">
              <table className="w-full text-left text-sm">
                <thead className="bg-gray-50 text-xs text-gray-500">
                  <tr className="border-b">
                    <th className="px-4 py-3">ID</th>
                    <th className="px-4 py-3">Naziv</th>
                    <th className="px-4 py-3">Adresa</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {locations.map((l, idx) => (
                    <tr key={l.locationId} className={cx("hover:bg-gray-50", idx % 2 ? "bg-gray-50/30" : "bg-white")}>
                      <td className="px-4 py-3 text-gray-700">#{l.locationId}</td>
                      <td className="px-4 py-3 font-medium text-gray-900">{l.locationName || l.name}</td>
                      <td className="px-4 py-3 text-gray-700">{l.locationAddress || l.address}</td>
                    </tr>
                  ))}

                  {locations.length === 0 && (
                    <tr>
                      <td colSpan={3} className="px-4 py-10 text-center text-sm text-gray-600">
                        Nema lokacija.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

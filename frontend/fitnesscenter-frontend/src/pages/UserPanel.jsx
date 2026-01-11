import { useEffect, useMemo, useRef, useState } from "react";
import { MapPin, Dumbbell, Calendar, CreditCard, RefreshCw, Search } from "lucide-react";
import { getToken } from "../auth/token";
import { createCheckoutSession } from "../api/stripeApi";

import yogaImg from "../assets/yoga.jpg";
import pilatesImg from "../assets/pilates.jpg";
import crossfitImg from "../assets/crossfit.jpg";
import fitnessImg from "../assets/fitness.jpg";
import gymImg from "../assets/gym.jpg";

import spensImg from "../assets/spens.png";
import promenadaImg from "../assets/promenada.png";
import centarImg from "../assets/centar.png";
import detelinaraImg from "../assets/detelinara.png";

function cx(...c) {
  return c.filter(Boolean).join(" ");
}

/** ✅ Zakucana veličina za SVE slike (radi i bez Tailwind-a) */
const IMG_W = 144; // ~ w-36
const IMG_H = 96;  // ~ h-24

const IMAGE_BOX_CLASSES =
  "shrink-0 overflow-hidden rounded-xl border border-gray-200 bg-white";
const IMAGE_CLASSES = "h-full w-full object-cover";

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

function formatDateTime(input) {
  if (!input) return "-";
  const raw = String(input);
  const d = new Date(raw);

  if (!Number.isNaN(d.getTime())) {
    const dd = String(d.getDate()).padStart(2, "0");
    const mm = String(d.getMonth() + 1).padStart(2, "0");
    const yyyy = d.getFullYear();
    const hh = String(d.getHours()).padStart(2, "0");
    const min = String(d.getMinutes()).padStart(2, "0");
    return `${dd}.${mm}.${yyyy} ${hh}:${min}`;
  }

  return raw.replace(/(\d{1,2}:\d{2}):\d{2}/, "$1").trim();
}

// ✅ Map service name -> local image
function serviceLocalImage(name = "") {
  const n = name.toLowerCase();
  if (n.includes("yoga")) return yogaImg;
  if (n.includes("pilates")) return pilatesImg;
  if (n.includes("crossfit") || n.includes("cross fit")) return crossfitImg;
  if (n.includes("gym") || n.includes("teret") || n.includes("teretana")) return gymImg;
  if (n.includes("fit")) return fitnessImg;
  return fitnessImg;
}

// ✅ Map location name -> local image
function locationLocalImage(name = "") {
  const n = name.toLowerCase();
  if (n.includes("spens")) return spensImg;
  if (n.includes("promenada")) return promenadaImg;
  if (n.includes("centar")) return centarImg;
  if (n.includes("detelin")) return detelinaraImg;
  return centarImg;
}

export default function UserPanel() {
  const [tab, setTab] = useState("services");
  const [err, setErr] = useState("");
  const [loading, setLoading] = useState(false);

  const [services, setServices] = useState([]);
  const [locations, setLocations] = useState([]);
  const [appointments, setAppointments] = useState([]);
  const [credits, setCredits] = useState([]);

  // filters (appointments)
  const [serviceFilter, setServiceFilter] = useState("all");
  const [search, setSearch] = useState("");
  const [minCapacity, setMinCapacity] = useState("");

  const [reserveBusyId, setReserveBusyId] = useState(null);

  const pollingRef = useRef(null);

  const SERVICES_URL = "http://localhost:8082/api/services?page=0&size=100";
  const LOCATIONS_URL = "http://localhost:8082/api/locations";
  const APPOINTMENTS_URL = "http://localhost:8082/api/appointments";

  async function loadAll() {
    setErr("");
    setLoading(true);
    try {
      const [svcPage, locs, appts] = await Promise.all([
        apiFetch(SERVICES_URL),
        apiFetch(LOCATIONS_URL),
        apiFetch(APPOINTMENTS_URL),
      ]);

      setServices(Array.isArray(svcPage?.content) ? svcPage.content : []);
      setLocations(Array.isArray(locs) ? locs : []);
      setAppointments(Array.isArray(appts) ? appts : appts?.content ?? []);
    } catch (e) {
      setErr(e?.message || "Greška pri učitavanju.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadAll();

    pollingRef.current = setInterval(() => {
      apiFetch(APPOINTMENTS_URL)
        .then((appts) => setAppointments(Array.isArray(appts) ? appts : appts?.content ?? []))
        .catch(() => {});
    }, 5000);

    return () => {
      if (pollingRef.current) clearInterval(pollingRef.current);
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const servicesById = useMemo(() => {
    const m = new Map();
    for (const s of services) {
      const id = s.serviceId ?? s.id;
      if (id != null) m.set(String(id), s);
    }
    return m;
  }, [services]);

  const locationsById = useMemo(() => {
    const m = new Map();
    for (const l of locations) {
      const id = l.locationId ?? l.id;
      if (id != null) m.set(String(id), l);
    }
    return m;
  }, [locations]);

  function getAppointmentFields(a) {
    const appointmentId = a.appointmentId ?? a.id;

    const startRaw = a.startTime ?? a.start ?? a.begin ?? a.pocetak;
    const endRaw = a.endTime ?? a.end ?? a.finish ?? a.kraj;

    const capRaw = a.maxCapacity ?? a.capacity ?? a.kapacitet ?? a.max ?? "-";
    const capNum = Number(capRaw);
    const capacity = Number.isFinite(capNum) ? capNum : capRaw;

    const serviceId = a.serviceId ?? a.service?.serviceId ?? a.service?.id;
    const svc = serviceId != null ? servicesById.get(String(serviceId)) : null;
    const serviceName = a.serviceName || a.service?.nameService || svc?.nameService || svc?.name || "-";

    const price = Number(svc?.price ?? svc?.priceService ?? a.price ?? 0);

    const locationId = a.locationId ?? a.location?.locationId ?? a.location?.id;
    const loc = locationId != null ? locationsById.get(String(locationId)) : null;
    const locationName =
      a.locationName ||
      a.location?.locationName ||
      loc?.locationName ||
      a.location?.name ||
      loc?.name ||
      "-";

    const remainingRaw = a.remaining ?? a.free ?? a.slobodno ?? null;
    const remaining = remainingRaw == null ? capacity : Number(remainingRaw);

    const canReserve = Number.isFinite(remaining) ? remaining > 0 : true;

    return {
      appointmentId,
      startFmt: formatDateTime(startRaw),
      endFmt: formatDateTime(endRaw),
      capacity,
      serviceName,
      price,
      locationName,
      remaining,
      canReserve,
    };
  }

  const finalAppointments = useMemo(() => {
    const q = search.trim().toLowerCase();
    const minCap = minCapacity === "" ? null : Number(minCapacity);

    return appointments
      .filter((raw) => {
        if (serviceFilter === "all") return true;
        const rawServiceId = raw.serviceId ?? raw.service?.serviceId ?? raw.service?.id;
        return String(rawServiceId) === String(serviceFilter);
      })
      .map(getAppointmentFields)
      .filter((a) => {
        if (minCap != null) {
          const cap = Number(a.capacity);
          if (!Number.isNaN(cap) && cap < minCap) return false;
        }

        if (!q) return true;

        const hay = [a.appointmentId, a.startFmt, a.endFmt, a.serviceName, a.locationName, a.capacity, a.price]
          .map((x) => String(x ?? ""))
          .join(" ")
          .toLowerCase();

        return hay.includes(q);
      });
  }, [appointments, serviceFilter, search, minCapacity, servicesById, locationsById]);

  async function handleReserve(appointmentId) {
    try {
      setReserveBusyId(appointmentId);

      const data = await createCheckoutSession(appointmentId);
      if (!data?.url) throw new Error("Backend nije vratio checkout url.");

      window.location.assign(data.url);
    } catch (e) {
      console.error(e);
      alert(e?.message || "Greška pri plaćanju. Pokušajte ponovo.");
    } finally {
      setReserveBusyId(null);
    }
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* ✅ centriranje celog sadržaja */}
      <div className="flex justify-center">
        <main className="w-full max-w-6xl px-4 pt-20 pb-10">
          {/* Tabs + refresh */}
          <div className="mb-5 flex flex-col items-center gap-3">
            <div className="flex flex-wrap justify-center gap-2">
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
            <div className="mb-4 mx-auto max-w-3xl rounded-2xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">
              {err}
            </div>
          )}

          {/* USLUGE */}
          {tab === "services" && (
            <section className="mx-auto w-full max-w-3xl">
              <div className="rounded-2xl border border-gray-200 bg-white shadow-sm">
                <div className="flex items-center justify-between gap-3 border-b border-gray-100 px-5 py-4">
                  <div className="text-base font-semibold text-gray-900">Usluge</div>
                  <Badge variant="blue">{services.length} ukupno</Badge>
                </div>

                <div className="p-5">
                  <div className="space-y-3">
                    {services.map((s) => {
                      const name = s.nameService || s.name || "Usluga";
                      const price = Number(s.price ?? s.priceService ?? 0);
                      const img = serviceLocalImage(name);

                      return (
                        <div
                          key={s.serviceId ?? s.id ?? name}
                          className="flex items-center gap-4 rounded-2xl border border-gray-200 bg-gray-50 p-4"
                        >
                          <div
                            className={IMAGE_BOX_CLASSES}
                            style={{ width: IMG_W, height: IMG_H }}
                          >
                            <img src={img} alt={name} className={IMAGE_CLASSES} />
                          </div>

                          <div className="flex-1">
                            <div className="text-base font-semibold text-gray-900">{name}</div>
                            <div className="mt-1 text-sm text-gray-700">
                              Cena: <b>{price.toFixed(2)} EUR</b>
                            </div>
                          </div>
                        </div>
                      );
                    })}

                    {services.length === 0 && (
                      <div className="rounded-2xl border border-gray-200 bg-white p-6 text-sm text-gray-600 text-center">
                        Nema usluga.
                      </div>
                    )}
                  </div>
                </div>
              </div>
            </section>
          )}

          {/* TERMINI */}
          {tab === "appointments" && (
            <section className="mx-auto w-full max-w-6xl">
              <div className="rounded-2xl border border-gray-200 bg-white shadow-sm">
                <div className="border-b border-gray-100 px-5 py-4">
                  <div className="flex flex-col gap-3">
                    <div className="flex items-center justify-between">
                      <div className="text-base font-semibold text-gray-900">Termini</div>
                      <Badge variant="blue">Ukupno: {finalAppointments.length}</Badge>
                    </div>

                    {/* ✅ Filteri: search (2) + min kapacitet (1) + sve usluge (1) u istom redu */}
                    <div className="grid grid-cols-1 gap-2 md:grid-cols-4 md:items-start">
                      <div className="relative md:col-span-2">
                        <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-400" />
                        <input
                          value={search}
                          onChange={(e) => setSearch(e.target.value)}
                          placeholder="Pretraga (id / datum / usluga / lokacija)"
                          className="h-10 w-full rounded-xl border border-gray-200 bg-white pl-10 pr-3 text-sm outline-none"
                        />
                      </div>

                      {/* Min kapacitet + label ispod */}
                      <div className="grid gap-1">
                        <input
                          value={minCapacity}
                          onChange={(e) => setMinCapacity(e.target.value)}
                          type="number"
                          min="0"
                          placeholder="Min kapacitet"
                          className="h-10 w-full rounded-xl border border-gray-200 bg-white px-3 text-sm outline-none"
                        />
                        <div className="text-xs font-medium text-gray-600">
                          Min kapacitet
                        </div>
                      </div>

                      {/* Sve usluge u ravni sa Min kapacitet inputom */}
                      <select
                        className="h-10 w-full rounded-xl border border-gray-200 bg-white px-3 text-sm outline-none"
                        value={serviceFilter}
                        onChange={(e) => setServiceFilter(e.target.value)}
                      >
                        <option value="all">Sve usluge</option>
                        {services.map((s) => (
                          <option key={s.serviceId ?? s.id} value={s.serviceId ?? s.id}>
                            {s.nameService || s.name}
                          </option>
                        ))}
                      </select>
                    </div>

                    {(search || minCapacity || serviceFilter !== "all") && (
                      <button
                        type="button"
                        onClick={() => {
                          setSearch("");
                          setMinCapacity("");
                          setServiceFilter("all");
                        }}
                        className="self-start rounded-xl border border-gray-200 bg-white px-3 py-1.5 text-xs font-semibold text-gray-700 hover:bg-gray-50"
                      >
                        Reset filtera
                      </button>
                    )}
                  </div>
                </div>

                {finalAppointments.length === 0 ? (
                  <div className="p-6 text-center text-sm text-gray-700">Nema termina.</div>
                ) : (
                  <div className="p-4">
                    <div className="overflow-auto rounded-xl border border-gray-200">
                      <table className="w-full text-center text-sm">
                        <thead className="bg-gray-900 text-white text-xs">
                          <tr>
                            <th className="px-3 py-3">ID</th>
                            <th className="px-3 py-3">Početak</th>
                            <th className="px-3 py-3">Kraj</th>
                            <th className="px-3 py-3">Lokacija</th>
                            <th className="px-3 py-3">Kapacitet</th>
                            <th className="px-3 py-3">Status</th>
                            <th className="px-3 py-3">Cena</th>
                            <th className="px-3 py-3">Rezervacija</th>
                          </tr>
                        </thead>

                        <tbody className="divide-y divide-gray-100">
                          {finalAppointments.map((f) => {
                            const isFull = !f.canReserve || Number(f.remaining) <= 0;

                            return (
                              <tr key={f.appointmentId} className="hover:bg-gray-50">
                                <td className="px-3 py-3 align-middle">#{f.appointmentId}</td>
                                <td className="px-3 py-3 align-middle">{f.startFmt}</td>
                                <td className="px-3 py-3 align-middle">{f.endFmt}</td>
                                <td className="px-3 py-3 align-middle">{f.locationName}</td>
                                <td className="px-3 py-3 align-middle">{f.capacity}</td>
                                <td className="px-3 py-3 align-middle">
                                  {isFull ? <Badge variant="red">✗</Badge> : <Badge variant="green">✓</Badge>}
                                </td>
                                <td className="px-3 py-3 align-middle">
                                  <b>{Number(f.price || 0).toFixed(2)} EUR</b>
                                </td>
                                <td className="px-3 py-3 align-middle">
                                  <button
                                    type="button"
                                    onClick={() => handleReserve(f.appointmentId)}
                                    disabled={isFull || reserveBusyId === f.appointmentId}
                                    className={cx(
                                      "rounded-xl px-3 py-2 text-xs font-semibold transition",
                                      !(isFull || reserveBusyId === f.appointmentId)
                                        ? "bg-gray-900 text-white hover:bg-black"
                                        : "bg-gray-200 text-gray-500 cursor-not-allowed"
                                    )}
                                  >
                                    {reserveBusyId === f.appointmentId ? "Otvaram..." : "Rezerviši"}
                                  </button>
                                </td>
                              </tr>
                            );
                          })}
                        </tbody>
                      </table>
                    </div>
                  </div>
                )}
              </div>
            </section>
          )}

          {/* MOJI TRENINZI */}
          {tab === "credits" && (
            <section className="mx-auto max-w-3xl">
              <div className="rounded-2xl border border-gray-200 bg-white shadow-sm">
                <div className="border-b border-gray-100 px-5 py-4">
                  <div className="text-base font-semibold text-gray-900">Moji treninzi</div>
                </div>

                <div className="p-6 text-center text-sm text-gray-700">
                  Ovde će ići tvoji rezervisani termini (kad dodaš endpoint za reservations).
                </div>

                {credits.length > 0 && (
                  <div className="p-5 space-y-3">
                    {credits.map((c) => (
                      <div key={c.id ?? c.reservationId} className="rounded-2xl border border-gray-200 bg-gray-50 p-4">
                        <div className="font-semibold text-gray-900">{c.serviceName}</div>
                        <div className="mt-1 text-sm text-gray-700">{c.startFmt}</div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </section>
          )}

          {/* LOKACIJE */}
          {tab === "locations" && (
            <section className="mx-auto max-w-3xl">
              <div className="rounded-2xl border border-gray-200 bg-white shadow-sm">
                <div className="border-b border-gray-100 px-5 py-4">
                  <div className="flex items-center justify-between gap-3">
                    <div className="text-base font-semibold text-gray-900">Lokacije</div>
                    <Badge variant="blue">{locations.length} ukupno</Badge>
                  </div>
                </div>

                <div className="p-6">
                  <div className="text-center text-sm font-semibold text-gray-900">
                    Možete trenirati na našim dostupnim lokacijama:
                  </div>

                  <div className="mt-4 space-y-3">
                    {locations.map((l) => {
                      const name = l.locationName || l.name || "Lokacija";
                      const address = l.locationAddress || l.address || "-";
                      const img = locationLocalImage(name);

                      return (
                        <div
                          key={l.locationId ?? l.id ?? name}
                          className="flex items-center gap-4 rounded-2xl border border-gray-200 bg-gray-50 p-4"
                        >
                          <div
                            className={IMAGE_BOX_CLASSES}
                            style={{ width: IMG_W, height: IMG_H }}
                          >
                            <img src={img} alt={name} className={IMAGE_CLASSES} />
                          </div>

                          <div className="flex-1">
                            <div className="text-sm font-semibold text-gray-900">{name}</div>
                            <div className="mt-1 text-sm text-gray-700">{address}</div>
                          </div>

                          <div className="text-xs text-gray-500">#{l.locationId ?? l.id}</div>
                        </div>
                      );
                    })}

                    {locations.length === 0 && (
                      <div className="rounded-2xl border border-gray-200 bg-white p-6 text-sm text-gray-600 text-center">
                        Nema lokacija.
                      </div>
                    )}
                  </div>
                </div>
              </div>
            </section>
          )}
        </main>
      </div>
    </div>
  );
}

import { useEffect, useMemo, useState } from "react";
import { locationsApi } from "../api/locations";
import { membersApi } from "../api/members";

import {
  Image as ImageIcon,
  MapPin,
  Users,
  UserCog,
  Plus,
  Trash2,
  RefreshCw,
  Building2,
  Mail,
  Phone,
  Store,
  AlertTriangle,
  Pencil,
  X,
  ChevronLeft,
  ChevronRight,
  ArrowUpDown,
} from "lucide-react";

const TABS = [
  { key: "gallery", label: "Galerija", icon: ImageIcon },
  { key: "locations", label: "Lokacija", icon: MapPin },
  { key: "employees", label: "Zaposleni", icon: UserCog },
  { key: "members", label: "Svi članovi", icon: Users },
];

// Tačno 4 slike za galeriju
const GALLERY_IMAGES = [
  "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?auto=format&fit=crop&w=1400&q=60",
  "https://images.unsplash.com/photo-1550345332-09e3ac987658?auto=format&fit=crop&w=1400&q=60",
  "https://images.unsplash.com/photo-1571902943202-507ec2618e8f?auto=format&fit=crop&w=1400&q=60",
  "https://images.unsplash.com/photo-1517836357463-d25dfeac3438?auto=format&fit=crop&w=1400&q=60",
];

function cx(...classes) {
  return classes.filter(Boolean).join(" ");
}

function IconButton({ children, className, ...props }) {
  return (
    <button
      className={cx(
        "inline-flex items-center gap-2 rounded-xl px-3 py-2 text-sm font-medium",
        "border border-gray-200 bg-white hover:bg-gray-50 active:bg-gray-100",
        "disabled:opacity-60 disabled:cursor-not-allowed",
        className
      )}
      {...props}
    >
      {children}
    </button>
  );
}

function PrimaryButton({ children, className, ...props }) {
  return (
    <button
      className={cx(
        "inline-flex items-center justify-center gap-2 rounded-xl px-4 py-2 text-sm font-semibold",
        "bg-gray-900 text-white hover:bg-gray-800 active:bg-black",
        "disabled:opacity-60 disabled:cursor-not-allowed",
        className
      )}
      {...props}
    >
      {children}
    </button>
  );
}

function Card({ title, icon: Icon, right, children }) {
  return (
    <div className="rounded-2xl border border-gray-200 bg-white shadow-sm">
      <div className="flex items-center justify-between gap-3 border-b border-gray-100 px-5 py-4">
        <div className="flex items-center gap-2">
          {Icon ? <Icon className="h-5 w-5 text-gray-700" /> : null}
          <h3 className="text-base font-semibold text-gray-900">{title}</h3>
        </div>
        {right}
      </div>
      <div className="px-5 py-4">{children}</div>
    </div>
  );
}

function Field({ label, children }) {
  return (
    <label className="grid gap-1">
      <span className="text-xs font-medium text-gray-600">{label}</span>
      {children}
    </label>
  );
}

function Input({ className, ...props }) {
  return (
    <input
      className={cx(
        "h-10 w-full rounded-xl border border-gray-200 bg-white px-3 text-sm outline-none focus:border-gray-400",
        className
      )}
      {...props}
    />
  );
}

function Select({ className, ...props }) {
  return (
    <select
      className={cx(
        "h-10 w-full rounded-xl border border-gray-200 bg-white px-3 text-sm outline-none focus:border-gray-400",
        className
      )}
      {...props}
    />
  );
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

function Modal({ open, title, onClose, children }) {
  if (!open) return null;
  return (
    <div className="fixed inset-0 z-50">
      <div className="absolute inset-0 bg-black/40" onClick={onClose} role="button" tabIndex={-1} />
      <div className="absolute left-1/2 top-1/2 w-[92vw] max-w-xl -translate-x-1/2 -translate-y-1/2 rounded-2xl border border-gray-200 bg-white shadow-xl">
        <div className="flex items-center justify-between border-b border-gray-100 px-5 py-4">
          <div className="text-base font-semibold text-gray-900">{title}</div>
          <button className="rounded-xl p-2 hover:bg-gray-50" onClick={onClose} aria-label="Close" type="button">
            <X className="h-5 w-5" />
          </button>
        </div>
        <div className="px-5 py-4">{children}</div>
      </div>
    </div>
  );
}

function RoleBadge({ role }) {
  if (role === "ROLE_ADMIN") return <Badge>ADMIN</Badge>;
  if (role === "ROLE_SELLER") return <Badge variant="blue">SELLER</Badge>;
  return <Badge variant="green">USER</Badge>;
}

function safeRole(m) {
  return m?.roll || m?.role || "";
}

export default function AdminPanel() {
  const [tab, setTab] = useState("gallery");
  const [err, setErr] = useState("");

  const [locations, setLocations] = useState([]);
  const [members, setMembers] = useState([]);

  const [loading, setLoading] = useState(false);
  const [busyId, setBusyId] = useState(null);
  const [busyGlobal, setBusyGlobal] = useState(false);

  // Gallery carousel
  const [imgIndex, setImgIndex] = useState(0);

  // Add Location modal
  const [addLocOpen, setAddLocOpen] = useState(false);
  const [locForm, setLocForm] = useState({ locationName: "", locationAddress: "" });

  // Edit Location modal
  const [editLocOpen, setEditLocOpen] = useState(false);
  const [editLocForm, setEditLocForm] = useState({ locationId: null, locationName: "", locationAddress: "" });

  // Add Seller modal
  const [addSellerOpen, setAddSellerOpen] = useState(false);
  const [sellerForm, setSellerForm] = useState({
    memberName: "",
    memberSurname: "",
    email: "",
    phone: "",
    username: "",
    password: "",
    locationId: 1,
  });

  // Edit Seller modal
  const [editSellerOpen, setEditSellerOpen] = useState(false);
  const [editSellerForm, setEditSellerForm] = useState({
    memberId: null,
    memberName: "",
    memberSurname: "",
    email: "",
    phone: "",
    username: "",
    locationId: 1,
  });

  // Members role filter / sort
  const [roleFilter, setRoleFilter] = useState("ALL"); // ALL | ROLE_USER | ROLE_SELLER | ROLE_ADMIN
  const [roleSort, setRoleSort] = useState("NONE"); // NONE | ASC | DESC

  async function loadAll() {
    setErr("");
    setLoading(true);
    try {
      const [locs, mems] = await Promise.all([locationsApi.list(), membersApi.list()]);
      setLocations(locs || []);
      setMembers(mems || []);

      if ((locs || []).length) {
        setSellerForm((s) => ({
          ...s,
          locationId: Number(s.locationId) ? s.locationId : locs[0].locationId,
        }));
        setEditSellerForm((s) => ({
          ...s,
          locationId: Number(s.locationId) ? s.locationId : locs[0].locationId,
        }));
      }
    } catch (e) {
      setErr(e?.message || "Greška pri učitavanju.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadAll();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // ===== Gallery controls =====
  function prevImg() {
    setImgIndex((i) => (i - 1 + GALLERY_IMAGES.length) % GALLERY_IMAGES.length);
  }
  function nextImg() {
    setImgIndex((i) => (i + 1) % GALLERY_IMAGES.length);
  }

  // ===== Locations =====
  async function createLocation(e) {
    e.preventDefault();
    setErr("");
    setBusyGlobal(true);
    try {
      await locationsApi.create(locForm);
      setLocForm({ locationName: "", locationAddress: "" });
      setAddLocOpen(false);
      await loadAll();
    } catch (e2) {
      setErr(e2?.message || "Greška pri kreiranju lokacije.");
    } finally {
      setBusyGlobal(false);
    }
  }

  function openEditLocation(l) {
    setEditLocForm({
      locationId: l.locationId,
      locationName: l.locationName || "",
      locationAddress: l.locationAddress || "",
    });
    setEditLocOpen(true);
  }

  async function updateLocation(e) {
    e.preventDefault();
    setErr("");
    setBusyGlobal(true);
    try {
      if (typeof locationsApi.update !== "function") throw new Error("locationsApi.update nije implementiran.");
      await locationsApi.update(editLocForm.locationId, {
        locationName: editLocForm.locationName,
        locationAddress: editLocForm.locationAddress,
      });
      setEditLocOpen(false);
      await loadAll();
    } catch (e2) {
      setErr(e2?.message || "Greška pri izmeni lokacije.");
    } finally {
      setBusyGlobal(false);
    }
  }

  async function deleteLocation(id) {
    if (!window.confirm("Da li sigurno želiš da obrišeš ovu lokaciju?")) return;
    setErr("");
    setBusyId(`loc-${id}`);
    try {
      await locationsApi.remove(id);
      await loadAll();
    } catch (e2) {
      setErr(e2?.message || "Greška pri brisanju lokacije.");
    } finally {
      setBusyId(null);
    }
  }

  // ===== Sellers =====
  async function createSeller(e) {
    e.preventDefault();
    setErr("");
    setBusyGlobal(true);
    try {
      await membersApi.createSeller({
        ...sellerForm,
        locationId: Number(sellerForm.locationId),
      });
      setSellerForm((s) => ({
        ...s,
        memberName: "",
        memberSurname: "",
        email: "",
        phone: "",
        username: "",
        password: "",
      }));
      setAddSellerOpen(false);
      await loadAll();
    } catch (e2) {
      setErr(e2?.message || "Greška pri kreiranju SELLER-a.");
    } finally {
      setBusyGlobal(false);
    }
  }

  function openEditSeller(m) {
    setEditSellerForm({
      memberId: m.memberId,
      memberName: m.memberName || "",
      memberSurname: m.memberSurname || "",
      email: m.email || "",
      phone: m.phone || "",
      username: m.username || "",
      locationId: m.location?.locationId ?? 1,
    });
    setEditSellerOpen(true);
  }

  async function updateSeller(e) {
    e.preventDefault();
    setErr("");
    setBusyGlobal(true);
    try {
      const updater = membersApi.updateSeller || membersApi.update || null;
      if (typeof updater !== "function") throw new Error("membersApi.updateSeller/update nije implementiran.");

      await updater(editSellerForm.memberId, {
        memberName: editSellerForm.memberName,
        memberSurname: editSellerForm.memberSurname,
        email: editSellerForm.email,
        phone: editSellerForm.phone,
        username: editSellerForm.username,
        locationId: Number(editSellerForm.locationId),
      });

      setEditSellerOpen(false);
      await loadAll();
    } catch (e2) {
      setErr(e2?.message || "Greška pri izmeni SELLER-a.");
    } finally {
      setBusyGlobal(false);
    }
  }

  async function deleteMember(id) {
    if (!window.confirm("Da li sigurno želiš da obrišeš ovog člana?")) return;
    setErr("");
    setBusyId(`mem-${id}`);
    try {
      await membersApi.remove(id);
      await loadAll();
    } catch (e2) {
      setErr(e2?.message || "Greška pri brisanju člana.");
    } finally {
      setBusyId(null);
    }
  }

  // ===== Derived data =====
  const sellers = useMemo(() => members.filter((m) => safeRole(m) === "ROLE_SELLER"), [members]);

  const filteredMembers = useMemo(() => {
    let list = [...members];

    if (roleFilter !== "ALL") {
      list = list.filter((m) => safeRole(m) === roleFilter);
    }

    if (roleSort !== "NONE") {
      const order = { ROLE_ADMIN: 1, ROLE_SELLER: 2, ROLE_USER: 3, "": 99 };
      list.sort((a, b) => {
        const ra = order[safeRole(a)] ?? 99;
        const rb = order[safeRole(b)] ?? 99;
        return roleSort === "ASC" ? ra - rb : rb - ra;
      });
    }

    return list;
  }, [members, roleFilter, roleSort]);

  // ===== UI helpers =====
  function TabButton({ t }) {
    const Icon = t.icon;
    const active = tab === t.key;
    return (
      <button
        type="button"
        onClick={() => setTab(t.key)}
        className={cx(
          "inline-flex items-center gap-2 rounded-2xl px-4 py-2 text-sm font-semibold transition",
          active ? "bg-gray-900 text-white" : "bg-white text-gray-700 border border-gray-200 hover:bg-gray-50"
        )}
      >
        <Icon className="h-4 w-4" />
        {t.label}
      </button>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <main className="mx-auto max-w-6xl px-4 py-8">
        {/* Tabs + global actions */}
        <div className="mb-6 flex flex-wrap items-center gap-2">
          {TABS.map((t) => (
            <TabButton key={t.key} t={t} />
          ))}

          <div className="ml-auto flex items-center gap-2">
            <IconButton type="button" onClick={loadAll} disabled={loading || busyGlobal}>
              <RefreshCw className={cx("h-4 w-4", loading ? "animate-spin" : "")} />
              Osveži
            </IconButton>
          </div>
        </div>

        {/* Error */}
        {err && (
          <div className="mb-6 rounded-2xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">
            <div className="flex items-start gap-2">
              <AlertTriangle className="mt-0.5 h-4 w-4" />
              <div>{err}</div>
            </div>
          </div>
        )}

        {/* ================= GALERIJA ================= */}
        {tab === "gallery" && (
          <Card
            title="Galerija"
            icon={ImageIcon}
            right={<Badge variant="blue">{imgIndex + 1} / {GALLERY_IMAGES.length}</Badge>}
          >
            <div className="flex items-center justify-center gap-3">
              {/* Leva strelica */}
              <IconButton type="button" onClick={prevImg} aria-label="Prethodna slika">
                <ChevronLeft className="h-4 w-4" />
              </IconButton>

              {/* Slika (manja) */}
              <div className="overflow-hidden rounded-2xl border border-gray-200 bg-gray-50">
                <img
                  src={GALLERY_IMAGES[imgIndex]}
                  alt={`gallery-${imgIndex}`}
                  className="h-[180px] w-[320px] object-cover sm:h-[220px] sm:w-[420px]"
                  loading="lazy"
                />
              </div>

              {/* Desna strelica */}
              <IconButton type="button" onClick={nextImg} aria-label="Sledeća slika">
                <ChevronRight className="h-4 w-4" />
              </IconButton>
            </div>

            {/* tačkice */}
            <div className="mt-4 flex justify-center gap-2">
              {GALLERY_IMAGES.map((_, i) => (
                <button
                  key={i}
                  type="button"
                  onClick={() => setImgIndex(i)}
                  className={cx(
                    "h-2.5 w-2.5 rounded-full border",
                    i === imgIndex ? "bg-gray-900 border-gray-900" : "bg-white border-gray-300"
                  )}
                  aria-label={`Go to ${i + 1}`}
                />
              ))}
            </div>
          </Card>
        )}


        {/* ================= LOKACIJA ================= */}
        {tab === "locations" && (
          <Card
            title="Lokacija — Pregled"
            icon={Building2}
            right={
              <div className="flex items-center gap-2">
                <Badge variant="blue">{locations.length} ukupno</Badge>
                <PrimaryButton type="button" onClick={() => setAddLocOpen(true)} disabled={busyGlobal || loading}>
                  <Plus className="h-4 w-4" />
                  Dodaj
                </PrimaryButton>
              </div>
            }
          >
            <div className="overflow-auto rounded-2xl border border-gray-200">
              <table className="w-full text-left text-sm">
                <thead className="bg-gray-50 text-xs text-gray-500">
                  <tr className="border-b">
                    <th className="px-4 py-3">ID</th>
                    <th className="px-4 py-3">Naziv</th>
                    <th className="px-4 py-3">Adresa</th>
                    <th className="px-4 py-3 text-right">Akcije</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {locations.map((l, idx) => (
                    <tr key={l.locationId} className={cx("hover:bg-gray-50", idx % 2 ? "bg-white" : "bg-gray-50/30")}>
                      <td className="px-4 py-3 text-gray-700">#{l.locationId}</td>
                      <td className="px-4 py-3 font-medium text-gray-900">{l.locationName}</td>
                      <td className="px-4 py-3 text-gray-700">{l.locationAddress}</td>
                      <td className="px-4 py-3 text-right">
                        <div className="inline-flex items-center gap-2">
                          <button
                            type="button"
                            onClick={() => openEditLocation(l)}
                            className="inline-flex h-10 w-10 items-center justify-center rounded-xl border border-gray-200 bg-white hover:bg-gray-50"
                            aria-label="Edit"
                          >
                            <Pencil className="h-4 w-4 text-gray-700" />
                          </button>

                          <button
                            type="button"
                            onClick={() => deleteLocation(l.locationId)}
                            disabled={busyId === `loc-${l.locationId}` || loading || busyGlobal}
                            className={cx(
                              "inline-flex h-10 w-10 items-center justify-center rounded-xl border",
                              "border-red-200 bg-white hover:bg-red-50",
                              busyId === `loc-${l.locationId}` || loading || busyGlobal ? "opacity-60 cursor-not-allowed" : ""
                            )}
                            aria-label="Delete"
                          >
                            <Trash2 className="h-4 w-4 text-red-700" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}

                  {locations.length === 0 && (
                    <tr>
                      <td colSpan={4} className="px-4 py-10 text-center text-sm text-gray-600">
                        Nema lokacija.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </Card>
        )}

        {/* ================= ZAPOSLENI ================= */}
        {tab === "employees" && (
          <Card
            title="Zaposleni (SELLER) — Pregled"
            icon={Store}
            right={
              <div className="flex items-center gap-2">
                <Badge variant="blue">{sellers.length} SELLER</Badge>
                <PrimaryButton
                  type="button"
                  onClick={() => setAddSellerOpen(true)}
                  disabled={busyGlobal || loading || locations.length === 0}
                >
                  <Plus className="h-4 w-4" />
                  Dodaj
                </PrimaryButton>
              </div>
            }
          >
            {locations.length === 0 && (
              <div className="mb-4 rounded-2xl border border-amber-200 bg-amber-50 p-4 text-sm text-amber-800">
                Ne može dodavanje SELLER-a dok nema lokacija. Prvo dodaj lokaciju.
              </div>
            )}

            <div className="overflow-auto rounded-2xl border border-gray-200">
              <table className="w-full text-left text-sm">
                <thead className="bg-gray-50 text-xs text-gray-500">
                  <tr className="border-b">
                    <th className="px-4 py-3">ID</th>
                    <th className="px-4 py-3">Ime</th>
                    <th className="px-4 py-3">Email</th>
                    <th className="px-4 py-3">Lokacija</th>
                    <th className="px-4 py-3 text-right">Akcije</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {sellers.map((m, idx) => (
                    <tr key={m.memberId} className={cx("hover:bg-gray-50", idx % 2 ? "bg-white" : "bg-gray-50/30")}>
                      <td className="px-4 py-3 text-gray-700">#{m.memberId}</td>
                      <td className="px-4 py-3 font-medium text-gray-900">
                        {m.memberName} {m.memberSurname}
                      </td>
                      <td className="px-4 py-3 text-gray-700">{m.email}</td>
                      <td className="px-4 py-3 text-gray-700">
                        {m.location?.locationName || `ID: ${m.location?.locationId ?? "-"}`}
                      </td>
                      <td className="px-4 py-3 text-right">
                        <div className="inline-flex items-center gap-2">
                          <button
                            type="button"
                            onClick={() => openEditSeller(m)}
                            className="inline-flex h-10 w-10 items-center justify-center rounded-xl border border-gray-200 bg-white hover:bg-gray-50"
                            aria-label="Edit"
                          >
                            <Pencil className="h-4 w-4 text-gray-700" />
                          </button>

                          <button
                            type="button"
                            onClick={() => deleteMember(m.memberId)}
                            disabled={busyId === `mem-${m.memberId}` || loading || busyGlobal}
                            className={cx(
                              "inline-flex h-10 w-10 items-center justify-center rounded-xl border",
                              "border-red-200 bg-white hover:bg-red-50",
                              busyId === `mem-${m.memberId}` || loading || busyGlobal ? "opacity-60 cursor-not-allowed" : ""
                            )}
                            aria-label="Delete"
                          >
                            <Trash2 className="h-4 w-4 text-red-700" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}

                  {sellers.length === 0 && (
                    <tr>
                      <td colSpan={5} className="px-4 py-10 text-center text-sm text-gray-600">
                        Nema SELLER naloga.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </Card>
        )}

        {/* ================= SVI ČLANOVI ================= */}
        {tab === "members" && (
          <Card
            title="Svi članovi — Tabela"
            icon={Users}
            right={
              <div className="flex flex-wrap items-center gap-2">
                {/* Filter po roli */}
                <div className="flex items-center gap-2">
                  <span className="text-xs font-medium text-gray-600">Filter rola:</span>
                  <Select value={roleFilter} onChange={(e) => setRoleFilter(e.target.value)} className="h-9">
                    <option value="ALL">Svi</option>
                    <option value="ROLE_USER">USER</option>
                    <option value="ROLE_SELLER">SELLER</option>
                    <option value="ROLE_ADMIN">ADMIN</option>
                  </Select>
                </div>

                {/* Sort po roli (grupisanje) */}
                <IconButton
                  type="button"
                  onClick={() => setRoleSort((s) => (s === "NONE" ? "ASC" : s === "ASC" ? "DESC" : "NONE"))}
                  title="Sort po roli"
                >
                  <ArrowUpDown className="h-4 w-4" />
                  {roleSort === "NONE" ? "Sort" : roleSort === "ASC" ? "Rola ↑" : "Rola ↓"}
                </IconButton>

                <Badge variant="blue">{filteredMembers.length} prikaz</Badge>
              </div>
            }
          >
            <div className="overflow-auto rounded-2xl border border-gray-200">
              <table className="w-full text-left text-sm">
                <thead className="bg-gray-50 text-xs text-gray-500">
                  <tr className="border-b">
                    <th className="px-4 py-3">ID</th>
                    <th className="px-4 py-3">Ime</th>
                    <th className="px-4 py-3">Email</th>
                    <th className="px-4 py-3">Telefon</th>
                    <th className="px-4 py-3">Rola</th>
                    <th className="px-4 py-3">Lokacija</th>
                    <th className="px-4 py-3 text-right">Akcije</th>
                  </tr>
                </thead>

                <tbody className="divide-y divide-gray-100">
                  {filteredMembers.map((m, idx) => (
                    <tr key={m.memberId} className={cx("hover:bg-gray-50", idx % 2 ? "bg-white" : "bg-gray-50/30")}>
                      <td className="px-4 py-3 text-gray-700">#{m.memberId}</td>
                      <td className="px-4 py-3 font-medium text-gray-900">
                        {m.memberName} {m.memberSurname}
                      </td>
                      <td className="px-4 py-3 text-gray-700">
                        <span className="inline-flex items-center gap-2">
                          <Mail className="h-4 w-4 text-gray-400" />
                          {m.email || "-"}
                        </span>
                      </td>
                      <td className="px-4 py-3 text-gray-700">
                        <span className="inline-flex items-center gap-2">
                          <Phone className="h-4 w-4 text-gray-400" />
                          {m.phone || "-"}
                        </span>
                      </td>
                      <td className="px-4 py-3">
                        <RoleBadge role={safeRole(m)} />
                      </td>
                      <td className="px-4 py-3 text-gray-700">
                        {m.location?.locationName || (m.location?.locationId ? `ID: ${m.location.locationId}` : "-")}
                      </td>

                      <td className="px-4 py-3 text-right">
                        <div className="inline-flex items-center gap-2">
                          {/* Izmena: smisleno (SELLER može, ostale po potrebi možeš proširiti) */}
                          <button
                            type="button"
                            onClick={() => openEditSeller(m)}
                            disabled={safeRole(m) !== "ROLE_SELLER"}
                            className={cx(
                              "inline-flex h-10 w-10 items-center justify-center rounded-xl border border-gray-200 bg-white",
                              safeRole(m) !== "ROLE_SELLER" ? "opacity-40 cursor-not-allowed" : "hover:bg-gray-50"
                            )}
                            aria-label="Edit"
                            title={safeRole(m) !== "ROLE_SELLER" ? "Izmena samo za SELLER (trenutno)" : "Izmeni"}
                          >
                            <Pencil className="h-4 w-4 text-gray-700" />
                          </button>

                          <button
                            type="button"
                            onClick={() => deleteMember(m.memberId)}
                            disabled={busyId === `mem-${m.memberId}` || loading || busyGlobal}
                            className={cx(
                              "inline-flex h-10 w-10 items-center justify-center rounded-xl border",
                              "border-red-200 bg-white hover:bg-red-50",
                              busyId === `mem-${m.memberId}` || loading || busyGlobal ? "opacity-60 cursor-not-allowed" : ""
                            )}
                            aria-label="Delete"
                            title="Obriši"
                          >
                            <Trash2 className="h-4 w-4 text-red-700" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}

                  {filteredMembers.length === 0 && (
                    <tr>
                      <td colSpan={7} className="px-4 py-10 text-center text-sm text-gray-600">
                        Nema članova za izabrani filter.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </Card>
        )}
      </main>

      {/* ================= MODALS ================= */}

      {/* Add Location */}
      <Modal open={addLocOpen} title="Dodaj novi centar / lokaciju" onClose={() => setAddLocOpen(false)}>
        <form onSubmit={createLocation} className="grid gap-3">
          <Field label="Naziv">
            <Input
              placeholder="npr. Novi Sad - Centar"
              value={locForm.locationName}
              onChange={(e) => setLocForm({ ...locForm, locationName: e.target.value })}
              required
            />
          </Field>
          <Field label="Adresa">
            <Input
              placeholder="npr. Bulevar Oslobođenja 12"
              value={locForm.locationAddress}
              onChange={(e) => setLocForm({ ...locForm, locationAddress: e.target.value })}
              required
            />
          </Field>
          <div className="mt-2 flex items-center justify-end gap-2">
            <IconButton type="button" onClick={() => setAddLocOpen(false)}>
              Otkaži
            </IconButton>
            <PrimaryButton type="submit" disabled={busyGlobal || loading}>
              <Plus className="h-4 w-4" />
              Dodaj
            </PrimaryButton>
          </div>
        </form>
      </Modal>

      {/* Edit Location */}
      <Modal open={editLocOpen} title="Izmeni lokaciju" onClose={() => setEditLocOpen(false)}>
        <form onSubmit={updateLocation} className="grid gap-3">
          <Field label="Naziv">
            <Input
              value={editLocForm.locationName}
              onChange={(e) => setEditLocForm((s) => ({ ...s, locationName: e.target.value }))}
              required
            />
          </Field>
          <Field label="Adresa">
            <Input
              value={editLocForm.locationAddress}
              onChange={(e) => setEditLocForm((s) => ({ ...s, locationAddress: e.target.value }))}
              required
            />
          </Field>
          <div className="mt-2 flex items-center justify-end gap-2">
            <IconButton type="button" onClick={() => setEditLocOpen(false)}>
              Otkaži
            </IconButton>
            <PrimaryButton type="submit" disabled={busyGlobal || loading}>
              <Pencil className="h-4 w-4" />
              Sačuvaj
            </PrimaryButton>
          </div>
        </form>
      </Modal>

      {/* Add Seller */}
      <Modal open={addSellerOpen} title="Dodaj zaposlenog (SELLER)" onClose={() => setAddSellerOpen(false)}>
        <form onSubmit={createSeller} className="grid gap-3">
          <div className="grid gap-3 sm:grid-cols-2">
            <Field label="Ime">
              <Input value={sellerForm.memberName} onChange={(e) => setSellerForm({ ...sellerForm, memberName: e.target.value })} required />
            </Field>
            <Field label="Prezime">
              <Input value={sellerForm.memberSurname} onChange={(e) => setSellerForm({ ...sellerForm, memberSurname: e.target.value })} required />
            </Field>
          </div>

          <Field label="Email">
            <Input value={sellerForm.email} onChange={(e) => setSellerForm({ ...sellerForm, email: e.target.value })} required />
          </Field>

          <Field label="Telefon">
            <Input value={sellerForm.phone} onChange={(e) => setSellerForm({ ...sellerForm, phone: e.target.value })} required />
          </Field>

          <Field label="Username">
            <Input value={sellerForm.username} onChange={(e) => setSellerForm({ ...sellerForm, username: e.target.value })} required />
          </Field>

          <Field label="Password">
            <Input type="password" value={sellerForm.password} onChange={(e) => setSellerForm({ ...sellerForm, password: e.target.value })} required />
          </Field>

          <Field label="Lokacija">
            <Select
              value={sellerForm.locationId}
              onChange={(e) => setSellerForm({ ...sellerForm, locationId: e.target.value })}
              disabled={locations.length === 0}
            >
              {locations.length === 0 ? (
                <option value="">Nema lokacija</option>
              ) : (
                locations.map((l) => (
                  <option key={l.locationId} value={l.locationId}>
                    {l.locationName}
                  </option>
                ))
              )}
            </Select>
          </Field>

          <div className="mt-2 flex items-center justify-end gap-2">
            <IconButton type="button" onClick={() => setAddSellerOpen(false)}>
              Otkaži
            </IconButton>
            <PrimaryButton type="submit" disabled={busyGlobal || loading || locations.length === 0}>
              <Plus className="h-4 w-4" />
              Dodaj
            </PrimaryButton>
          </div>
        </form>
      </Modal>

      {/* Edit Seller */}
      <Modal open={editSellerOpen} title="Izmeni SELLER menadžera" onClose={() => setEditSellerOpen(false)}>
        <form onSubmit={updateSeller} className="grid gap-3">
          <div className="grid gap-3 sm:grid-cols-2">
            <Field label="Ime">
              <Input value={editSellerForm.memberName} onChange={(e) => setEditSellerForm((s) => ({ ...s, memberName: e.target.value }))} required />
            </Field>
            <Field label="Prezime">
              <Input value={editSellerForm.memberSurname} onChange={(e) => setEditSellerForm((s) => ({ ...s, memberSurname: e.target.value }))} required />
            </Field>
          </div>

          <Field label="Email">
            <Input value={editSellerForm.email} onChange={(e) => setEditSellerForm((s) => ({ ...s, email: e.target.value }))} required />
          </Field>

          <Field label="Telefon">
            <Input value={editSellerForm.phone} onChange={(e) => setEditSellerForm((s) => ({ ...s, phone: e.target.value }))} required />
          </Field>

          <Field label="Username">
            <Input value={editSellerForm.username} onChange={(e) => setEditSellerForm((s) => ({ ...s, username: e.target.value }))} required />
          </Field>

          <Field label="Lokacija">
            <Select value={editSellerForm.locationId} onChange={(e) => setEditSellerForm((s) => ({ ...s, locationId: e.target.value }))}>
              {locations.map((l) => (
                <option key={l.locationId} value={l.locationId}>
                  {l.locationName}
                </option>
              ))}
            </Select>
          </Field>

          <div className="mt-2 flex items-center justify-end gap-2">
            <IconButton type="button" onClick={() => setEditSellerOpen(false)}>
              Otkaži
            </IconButton>
            <PrimaryButton type="submit" disabled={busyGlobal || loading}>
              <Pencil className="h-4 w-4" />
              Sačuvaj
            </PrimaryButton>
          </div>
        </form>
      </Modal>
    </div>
  );
}

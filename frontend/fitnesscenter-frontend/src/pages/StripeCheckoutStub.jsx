import { useMemo, useState } from "react";

export default function StripeCheckoutStub() {
  const params = useMemo(() => new URLSearchParams(window.location.search), []);
  const appointmentId = params.get("appointmentId");
  const price = params.get("price");
  const currency = params.get("currency") || "EUR";
  const start = params.get("start");
  const end = params.get("end");
  const location = params.get("location");

  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState("");

  const pay = async () => {
    setErr("");

    if (!appointmentId) {
      setErr("Nedostaje appointmentId u URL-u.");
      return;
    }

    try {
      setLoading(true);

      const res = await fetch("/api/stripe/checkout-session", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        // memberId: ako ga imaš na frontu (iz auth/user store), pošalji ga ovde
        body: JSON.stringify({ appointmentId: Number(appointmentId) }),
      });

      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || `HTTP ${res.status}`);
      }

      const data = await res.json();
      if (!data?.url) throw new Error("Backend nije vratio Stripe Checkout url.");

      window.location.href = data.url;
    } catch (e) {
      setErr(e?.message || "Greška pri kreiranju Stripe sesije.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 px-4 py-10">
      <div className="mx-auto max-w-xl rounded-2xl border border-gray-200 bg-white p-6 shadow-sm">
        <h1 className="text-xl font-bold text-gray-900">Stripe plaćanje</h1>
        <p className="mt-2 text-sm text-gray-600">
          Bićete preusmereni na Stripe Checkout.
        </p>

        <div className="mt-6 rounded-xl bg-gray-50 p-4 text-sm">
          <div><b>Termin ID:</b> {appointmentId || "-"}</div>
          <div className="mt-1"><b>Vreme:</b> {start || "-"} – {end || "-"}</div>
          <div className="mt-1"><b>Lokacija:</b> {location || "-"}</div>
          <div className="mt-1"><b>Cena:</b> {price || "0.00"} {currency}</div>
        </div>

        {err && (
          <div className="mt-4 rounded-xl border border-red-200 bg-red-50 p-3 text-sm text-red-700">
            {err}
          </div>
        )}

        <button
          type="button"
          onClick={pay}
          disabled={loading}
          className={`mt-5 w-full rounded-xl px-4 py-3 text-sm font-semibold text-white ${
            loading ? "bg-gray-400 cursor-wait" : "bg-black hover:bg-gray-900"
          }`}
        >
          {loading ? "Preusmeravam..." : "Plati (Stripe)"}
        </button>

        <button
          type="button"
          onClick={() => window.close()}
          className="mt-3 w-full rounded-xl border border-gray-200 bg-white px-4 py-3 text-sm font-semibold text-gray-700 hover:bg-gray-50"
        >
          Zatvori tab
        </button>
      </div>
    </div>
  );
}

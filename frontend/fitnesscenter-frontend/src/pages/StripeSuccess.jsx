import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";

export default function StripeSuccess() {
  const [params] = useSearchParams();
  const sessionId = params.get("session_id");

  const [status, setStatus] = useState("loading"); // loading | ok | error
  const [message, setMessage] = useState("");

  useEffect(() => {
    const run = async () => {
      if (!sessionId) {
        setStatus("error");
        setMessage("Nedostaje session_id u URL-u.");
        return;
      }

      try {
        const res = await fetch("/api/stripe/confirm", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          // memberId: idealno uzmi iz auth/user store-a, ne iz URL-a
          body: JSON.stringify({ sessionId }),
        });

        if (!res.ok) {
          const text = await res.text();
          throw new Error(text || `HTTP ${res.status}`);
        }

        setStatus("ok");
        setMessage("Hvala! Vaša rezervacija je potvrđena.");
      } catch (e) {
        setStatus("error");
        setMessage(e?.message || "Nije uspelo potvrđivanje uplate.");
      }
    };

    run();
  }, [sessionId]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
      <div className="w-full max-w-md rounded-xl border bg-white p-8 text-center shadow">
        {status === "loading" && (
          <>
            <h2 className="text-2xl font-bold mb-3">Proveravam uplatu…</h2>
            <p className="text-gray-600">Sačekajte trenutak.</p>
          </>
        )}

        {status === "ok" && (
          <>
            <h2 className="text-2xl font-bold mb-3">✅ Plaćanje uspešno</h2>
            <p className="text-gray-600">{message}</p>
          </>
        )}

        {status === "error" && (
          <>
            <h2 className="text-2xl font-bold mb-3">⚠️ Nešto nije u redu</h2>
            <p className="text-gray-600">{message}</p>
            <p className="text-sm text-gray-500 mt-3">
              Ako vam je kartica naplaćena, javite se podršci.
            </p>
          </>
        )}

        <p className="text-xs mt-4 text-gray-400 break-all">
          Session ID: {sessionId || "-"}
        </p>
      </div>
    </div>
  );
}

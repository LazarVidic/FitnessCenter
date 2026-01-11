import { getToken } from "../auth/token";

const BASE = "http://localhost:8082";

export async function createCheckoutSession(appointmentId) {
  const token = getToken();

  const res = await fetch(`${BASE}/api/stripe/checkout-session`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: JSON.stringify({ appointmentId }),
  });

  // ✅ uvek pročitaj kao text, pa pokušaj JSON
  const text = await res.text();
  let body = null;
  try {
    body = text ? JSON.parse(text) : null;
  } catch {
    body = { raw: text };
  }

  if (!res.ok) {
    const msg = body?.message || body?.error || body?.raw || "Unauthorized";
    throw new Error(`Stripe checkout greška (${res.status}): ${msg}`);
  }

  return body; // očekujemo { url: "..." }
}

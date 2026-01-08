import { http } from "./http";
import { tokenStore } from "../auth/token";

export async function login(email, password) {
  const res = await http.post("/api/member/login", { email, password });
  const token = res.data?.jwtToken;
  if (!token) throw new Error("Nema jwtToken u odgovoru.");
  tokenStore.set(token);
  return token;
}

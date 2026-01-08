import { http } from "./http";

export async function getAppointments() {
  const res = await http.get("/api/appointments");
  return res.data;
}

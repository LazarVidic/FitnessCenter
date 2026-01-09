
import { http } from "./http";

export const locationsApi = {
  list: async () => (await http.get("/api/locations")).data,

  create: async (payload) =>
    (await http.post("/api/locations/create", payload)).data,

  update: async (id, payload) =>
    (await http.put(`/api/locations/update/${id}`, payload)).data,

  remove: async (id) =>
    (await http.delete(`/api/locations/delete/${id}`)).data,
};

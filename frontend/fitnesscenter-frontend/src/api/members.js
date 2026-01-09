
import { http } from "./http";

export const membersApi = {
  list: async () => (await http.get("/api/members")).data,

  createAdmin: async (payload) =>
    (await http.post("/api/members/create/admin", payload)).data,

  createSeller: async (payload) =>
    (await http.post("/api/members/create/seller", payload)).data,

  createUser: async (payload) =>
    (await http.post("/api/members/create/user", payload)).data,

  update: async (id, payload) =>
    (await http.put(`/api/members/update/${id}`, payload)).data,

  remove: async (id) =>
    (await http.delete(`/api/members/delete/${id}`)).data,
};

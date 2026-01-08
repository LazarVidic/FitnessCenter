import { Navigate, useLocation } from "react-router-dom";
import { tokenStore } from "./token";

export default function RequireAuth({ children }) {
  const token = tokenStore.get();
  const location = useLocation();

  if (!token) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  }

  return children;
}

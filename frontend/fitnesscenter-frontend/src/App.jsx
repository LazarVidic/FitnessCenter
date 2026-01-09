import { Routes, Route, Navigate, useNavigate } from "react-router-dom";
import { useState, useMemo } from "react";
import Navbar from "./components/Navbar";
import LoginModal from "./components/LoginModal";
import Home from "./pages/Home";
import Footer from "./components/Footer";
import AdminPanel from "./pages/AdminPanel";
import AppointmentsPage from "./pages/Appointment";
import { hasRole, getToken } from "./auth/token";

function RequireAuth({ children }) {
  const token = getToken();
  if (!token) return <Navigate to="/" replace />;
  return children;
}

function RequireRole({ role, children }) {
  const token = getToken();
  if (!token) return <Navigate to="/" replace />;
  if (!hasRole(role)) return <Navigate to="/appointments" replace />;
  return children;
}

export default function App() {
  const [loginOpen, setLoginOpen] = useState(false);
  const nav = useNavigate();

  // bolje da zavisi od tokena iz tvog auth helpera
  const isAuthenticated = useMemo(() => Boolean(getToken()), [loginOpen]);

  return (
    <div className="app-layout">
      <Navbar
        isAuthenticated={isAuthenticated}
        onLoginClick={() => setLoginOpen(true)}  // ✅ FIX
        onLogoutClick={() => {
          localStorage.removeItem("token");
          localStorage.removeItem("accessToken");
          nav("/"); // ✅ FIX (nav umesto navigate)
        }}
      />

      {/* da sadržaj ne ide ispod fixed navbara */}
      <div style={{ paddingTop: 64 }}>
        <Routes>
          <Route path="/" element={<Home />} />

          <Route
            path="/appointments"
            element={
              <RequireAuth>
                <AppointmentsPage />
              </RequireAuth>
            }
          />

          <Route
            path="/admin"
            element={
              <RequireRole role="ROLE_ADMIN">
                <AdminPanel />
              </RequireRole>
            }
          />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>

        <Footer />
      </div>

      <LoginModal
        open={loginOpen}
        onClose={() => setLoginOpen(false)}
        onSuccess={({ roles }) => {
          setLoginOpen(false);

          if (roles?.includes("ROLE_ADMIN")) nav("/admin");
          else nav("/appointments");
        }}
      />
    </div>
  );
}

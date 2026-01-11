import { Routes, Route, Navigate, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import Navbar from "./components/Navbar";
import LoginModal from "./components/LoginModal";
import Home from "./pages/Home";
import Footer from "./components/Footer";
import AdminPanel from "./pages/AdminPanel";
import UserPanel from "./pages/UserPanel";
import StripeCheckoutStub from "./pages/StripeCheckoutStub";
import StripeSuccess from "./pages/StripeSuccess";

import { hasRole, getToken, clearToken } from "./auth/token";

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
  const [isAuthenticated, setIsAuthenticated] = useState(!!getToken());
  const nav = useNavigate();

  // sync kad zatvoriš/otvoriš login modal (ili ako token promeni login)
  useEffect(() => {
    setIsAuthenticated(!!getToken());
  }, [loginOpen]);

  return (
    <div className="app-layout">
      <Navbar
        isAuthenticated={isAuthenticated}
        onLoginClick={() => setLoginOpen(true)}
        onLogoutClick={() => {
          clearToken();
          setIsAuthenticated(false);
          nav("/");
        }}
      />

      {/* main gura footer na dno */}
      <main className="app-main" style={{ paddingTop: 64 }}>
        <Routes>
          <Route path="/" element={<Home />} />

          <Route
            path="/appointments"
            element={
              <RequireAuth>
                <UserPanel />
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

          <Route
            path="/stripe-checkout"
            element={
              <RequireAuth>
                <StripeCheckoutStub />
              </RequireAuth>
            }
          />

          <Route
            path="/stripe-success"
            element={
              <RequireAuth>
                <StripeSuccess />
              </RequireAuth>
            }
          />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>

      <Footer />

      <LoginModal
        open={loginOpen}
        onClose={() => setLoginOpen(false)}
        onSuccess={({ roles }) => {
          setLoginOpen(false);
          setIsAuthenticated(true);
          if (roles?.includes("ROLE_ADMIN")) nav("/admin");
          else nav("/appointments");
        }}
      />
    </div>
  );

}

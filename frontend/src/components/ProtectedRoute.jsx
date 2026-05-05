import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function ProtectedRoute({ requiredRoles }) {
  const { user } = useAuth();

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (requiredRoles && requiredRoles.length > 0) {
    const userRoles = (user.roles || []).map((r) => r.toUpperCase());
    const needed = requiredRoles.map((r) => r.toUpperCase());
    const hasRole = needed.some((role) => userRoles.includes(role));
    if (!hasRole) {
      return <div>Access denied</div>;
    }
  }

  return <Outlet />;
}
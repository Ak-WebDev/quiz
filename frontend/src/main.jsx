import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import App from "./App.jsx";
import { AuthProvider } from "./context/AuthContext.jsx";
import LoginPage from "./pages/LoginPage.jsx";
import RegisterPage from "./pages/RegisterPage.jsx";
import ProtectedRoute from "./components/ProtectedRoute.jsx";
import AdminDashboard from "./pages/admin/AdminDashboard.jsx";
import ParticipantDashboard from "./pages/participant/ParticipantDashboard.jsx";
import QuizTakingPage from "./pages/participant/QuizTakingPage.jsx"

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/" element={<App />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          <Route element={<ProtectedRoute requiredRoles={["ROLE_ADMIN"]} />}>
            <Route path="/admin" element={<AdminDashboard />} />
          </Route>

          <Route element={<ProtectedRoute requiredRoles={["ROLE_PARTICIPANT"]} />}>
            <Route path="/participant" element={<ParticipantDashboard />} />
          </Route>

          <Route element={<ProtectedRoute requiredRoles={["ROLE_PARTICIPANT"]} />}>
              <Route path="/participant" element={<ParticipantDashboard/>}/>
              <Route path="/participant/quizzes/:id" element={<QuizTakingPage/>}/>
          </Route>
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  </React.StrictMode>
);
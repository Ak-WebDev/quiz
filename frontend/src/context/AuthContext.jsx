import { createContext, useContext, useEffect, useState } from "react";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null); // { userId, username, email, roles }
  const [token, setToken] = useState(null);

  useEffect(() => {
    const savedToken = localStorage.getItem("authToken");
    const savedUser = localStorage.getItem("authUser");
    if (savedToken && savedUser) {
      setToken(savedToken);
      setUser(JSON.parse(savedUser));
    }
  }, []);

  const login = (authResponse) => {
    localStorage.setItem("authToken", authResponse.token);
    localStorage.setItem(
      "authUser",
      JSON.stringify({
        userId: authResponse.userId,
        username: authResponse.username,
        email: authResponse.email,
        roles: authResponse.roles,
      })
    );
    setToken(authResponse.token);
    setUser({
      userId: authResponse.userId,
      username: authResponse.username,
      email: authResponse.email,
      roles: authResponse.roles,
    });
  };

  const logout = () => {
    localStorage.removeItem("authToken");
    localStorage.removeItem("authUser");
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, token, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
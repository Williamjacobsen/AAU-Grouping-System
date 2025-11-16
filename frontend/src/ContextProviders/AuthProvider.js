import React, { createContext, useContext, useEffect, useState } from "react";
import { fetchWithDefaultErrorHandling } from "../utils/fetchHelpers";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    (async () => {
      try {
        const response = await fetchWithDefaultErrorHandling("/auth/getUser", {
          credentials: "include",
        });
        if (response.ok) {
          const data = await response.json();
          setUser(data);
        } else {
          setUser(null);
					// maybe redirect to login screen?
        }
      } catch (error) {
        //alert(error); 
      } finally {
        setIsLoading(false);
      }
    })();
  }, []); 

  const value = {
    user,
    setUser,
    isLoading,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within <AuthProvider>");
  return ctx;
}

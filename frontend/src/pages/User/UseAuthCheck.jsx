import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

export function useAuthCheck() {
	
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  const navigate = useNavigate();

  useEffect(() => {
    fetch("http://localhost:8080/auth/me", {
      method: "GET",
      credentials: "include",
    })
      .then(async (response) => {
        if (response.ok) {
          const data = await response.json();
          setUser(data);
        } else {
          navigate("/sign-in");
        }
      })
      .catch(() => {
        navigate("/sign-in");
      })
      .finally(() => {
        setLoading(false);
      });
  }, [navigate]);

  return { user, loading };
}

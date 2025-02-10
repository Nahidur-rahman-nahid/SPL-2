"use client";
import { useEffect } from "react";
import { useRouter } from "next/navigation";

export default function HomePage() {
  const router = useRouter();
  useEffect(() => {
      const fetchTasks = async () => {
        try {
          const response = await fetch("/api/users/account/personal");
          const userData = await response.json();
          if (userData) {
            localStorage.setItem("TimeWiseUserData", JSON.stringify(userData));
          } else {
            router.push("/welcome"); 
          }
        } catch (err) {
          setError(err.message);
          setLoading(false);
        }
      };
  
      fetchTasks();
    }, []);
 
  return (
    <div>
      {/* This is the root page for the home route. */}
      {/* You can add content here if needed, but it will be rendered within the layout. */}
    </div>
  );
}

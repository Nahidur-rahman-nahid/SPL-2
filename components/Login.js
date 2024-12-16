"use client";
import { useState } from "react";
import { motion } from "framer-motion";
import Link from "next/link";
import Image from "next/image";
import { useRouter } from "next/navigation";

function Login() {
  const router = useRouter();
  const [username, setUserName] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const validateForm = () => {
    if (!username.trim()) return "Username is required.";
    if (password.length < 0) return "Password must be at least 8 characters long.";
    return "";
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errorMsg = validateForm();
    if (errorMsg) {
      setError(errorMsg);
      return;
    }
    setError("");
  
    try {
      const response = await fetch("/api/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ userName: username, password }),
      });
  
      // Check if the response is OK
      if (!response.ok) {
        const text = await response.text(); // Read plain text error message
        setError(text || "Please check your credentials and try again.");
        return;
      }
  
      // Handle plain text JWT response
      const token = await response.text();
      localStorage.setItem("jwtTokenTimeWise", token); // Save JWT token in localStorage
      alert("Login successful!");
      router.push("/"); // Redirect to home
    } catch (error) {
      console.error("Login error:", error.message);
      setError("An error occurred. Please try again later.");
    }
  };
  

  return (
    <div className="min-h-screen flex items-center justify-center bg-cover bg-center bg-gray-400">
      <motion.div
        initial={{ opacity: 0, y: -50 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="w-full max-w-lg p-8 bg-gray-800 bg-opacity-90 rounded-lg shadow-lg"
      >
        <div className="flex items-center justify-center space-x-4 mb-4">
          <Image
            src="/images/timewise_logo.png"
            alt="TimeWise Logo"
            width={60}
            height={60}
          />
          <h1 className="text-4xl font-bold text-blue-400">TimeWise</h1>
        </div>
        <h2 className="text-3xl font-bold text-white text-center mb-6">Login</h2>
        {error && <p className="text-red-500 text-center mb-4">{error}</p>}
        <form onSubmit={handleSubmit} className="space-y-4">
          <input
            type="text"
            id="username"
            name="userName"
            placeholder="Username"
            value={username}
            onChange={(e) => setUserName(e.target.value)}
            className="w-full p-3 border border-gray-300 rounded-md"
          />
          <input
            type="password"
            id="password"
            name="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full p-3 border border-gray-300 rounded-md"
          />
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            className="w-full py-3 bg-gray-500 text-white font-semibold rounded-lg hover:bg-gray-900 transition duration-300"
            type="submit"
          >
            Login
          </motion.button>
        </form>

        <p className="text-center text-sm text-blue-400 mt-4">
          Don’t have an account?{" "}
          <Link href="/register" className="text-white hover:underline">
            Register
          </Link>
        </p>
      </motion.div>
    </div>
  );
}

export default Login;

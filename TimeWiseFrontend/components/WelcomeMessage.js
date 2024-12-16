"use client";
import { useState } from "react";
import { motion } from "framer-motion";
import Link from "next/link";
import Image from "next/image";
import { FaBars, FaTimes, FaSignInAlt, FaUserPlus, FaInfoCircle, FaPhoneAlt } from "react-icons/fa";

function WelcomeMessage() {
  const [menuOpen, setMenuOpen] = useState(false);

  const features = [
    "User Management: Create and manage your profile with ease.",
    "Task Management: Maintain a to-do list for short and long-term goals.",
    "Progress Tracking: Monitor your progress on assigned tasks.",
    "Performance Tracking: Evaluate performance based on completed tasks.",
    "Reminders & Notifications: Stay on track with timely alerts.",
    "Statistics: View detailed stats about your performance and participation.",
    "Deep Work Tracking: Discover your peak productivity hours.",
    "Collaborative Environment: Share tasks with teams and get feedback.",
  ];

  const featureVariants = {
    hidden: { opacity: 0 },
    visible: { opacity: 1, transition: { duration: 1 } },
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-navy-800 to-blue-900 text-gray-100">
      {/* Fixed Top Bar */}
      <header className="sticky top-0 bg-gray-900/80 backdrop-blur-md z-30">
        <div className="flex items-center justify-between px-4 py-3">
          {/* Logo Section */}
          <div className="flex items-center space-x-3">
            <Image
              src="/images/timewise_logo.png"
              alt="TimeWise Logo"
              width={50}
              height={50}
            />
            <h1 className="text-2xl font-bold text-blue-400">TimeWise</h1>
          </div>

          {/* Hamburger Menu for Small Screens */}
          <div className="sm:hidden">
            <button
              onClick={() => setMenuOpen(!menuOpen)}
              className="text-gray-100 focus:outline-none"
            >
              {menuOpen ? <FaTimes size={24} /> : <FaBars size={24} />}
            </button>
          </div>

          {/* Navigation Buttons for Larger Screens */}
          <div className="hidden sm:flex space-x-3">
            <Link href="/login">
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                className="px-2 py-1 text-sm font-bold bg-blue-600 text-white rounded-md hover:bg-blue-700 transition duration-300"
              >
                <FaSignInAlt className="inline mr-1" /> Login
              </motion.button>
            </Link>
            <Link href="/register">
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                className="px-2 py-1 text-sm font-bold bg-gray-700 text-gray-100 rounded-md hover:bg-gray-600 transition duration-300"
              >
                <FaUserPlus className="inline mr-1" /> Register
              </motion.button>
            </Link>
            <Link href="/about-us">
              <button className="px-2 py-1 text-sm font-bold bg-gray-700 text-gray-100 rounded-md hover:bg-gray-600 transition duration-300">
                <FaInfoCircle className="inline mr-1" /> About Us
              </button>
            </Link>
            <Link href="/contact-us">
              <button className="px-2 py-1 text-sm font-bold bg-gray-700 text-gray-100 rounded-md hover:bg-gray-600 transition duration-300">
                <FaPhoneAlt className="inline mr-1" /> Contact Us
              </button>
            </Link>
          </div>
        </div>

        {/* Dropdown Menu for Small Screens */}
        {menuOpen && (
          <div className="sm:hidden bg-gray-800/90">
            <Link href="/login">
              <button className="block w-full text-left px-4 py-2 text-sm text-gray-100 hover:bg-gray-700 transition duration-300">
                <FaSignInAlt className="inline mr-2" /> Sign In
              </button>
            </Link>
            <Link href="/register">
              <button className="block w-full text-left px-4 py-2 text-sm text-gray-100 hover:bg-gray-700 transition duration-300">
                <FaUserPlus className="inline mr-2" /> Sign Up
              </button>
            </Link>
            <Link href="/about-us">
              <button className="block w-full text-left px-4 py-2 text-sm text-gray-100 hover:bg-gray-700 transition duration-300">
                <FaInfoCircle className="inline mr-2" /> About Us
              </button>
            </Link>
            <Link href="/contact-us">
              <button className="block w-full text-left px-4 py-2 text-sm text-gray-100 hover:bg-gray-700 transition duration-300">
                <FaPhoneAlt className="inline mr-2" /> Contact Us
              </button>
            </Link>
          </div>
        )}
      </header>

      {/* Content Section */}
      <main className="flex flex-col items-center justify-start pt-10 px-6">
        {/* Welcome Message */}
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 1 }}
          className="text-center max-w-4xl mb-8"
        >
          <h1 className="text-3xl sm:text-5xl font-semibold mb-4 text-blue-400">
            Welcome to TimeWise
          </h1>
          <h2 className="text-2xl sm:text-3xl font-semibold mb-4 text-blue-400">
            Your ultimate tool for time and productivity management through proper task management
          </h2>
          <p className="text-lg text-gray-300 sm:text-xl">
            Organize, track, and achieve your goals with precision.
          </p>
        </motion.div>

        {/* Features Section */}
        <div className="w-full max-w-6xl grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 mb-16">
          {features.map((feature, index) => (
            <motion.div
              key={index}
              variants={featureVariants}
              initial="hidden"
              animate="visible"
              className="bg-gray-800/70 p-6 rounded-lg shadow-lg text-center text-1xl text-gray-200 hover:shadow-blue-400/50 transition-shadow duration-300"
            >
              <h3 className="text-xl sm:text-2xl font-semibold text-blue-400">
                {feature.split(":")[0]}
              </h3>
              <p>{feature.split(":")[1]}</p>
            </motion.div>
          ))}
        </div>
      </main>
    </div>
  );
}

export default WelcomeMessage;

"use client";

import { useState } from "react";
import { motion } from "framer-motion";

const Session = () => {
  const [sessionStartTime, setSessionStartTime] = useState("");
  const [sessionEndTime, setSessionEndTime] = useState("");
  const [duration, setDuration] = useState(0);
  const [activitySummary, setActivitySummary] = useState("");

  const handleFormSubmit = (e) => {
    e.preventDefault();
    
    // Calculate duration if end time and start time are provided
    if (sessionStartTime && sessionEndTime) {
      const start = new Date(sessionStartTime);
      const end = new Date(sessionEndTime);
      const sessionDuration = (end - start) / 60000; // duration in minutes
      setDuration(sessionDuration);
    }

    // Submit session data to backend (this will be replaced with actual API call later)
    const sessionData = {
      session_start_time: new Date(sessionStartTime),
      session_end_time: new Date(sessionEndTime),
      duration: duration,
      activity_summary: activitySummary,
    };
    
    console.log("Session Created:", sessionData);
  };

  return (
    <div className="min-h-screen bg-gray-100 p-8">
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="max-w-3xl mx-auto bg-white p-6 rounded-lg shadow-md"
      >
        <h1 className="text-3xl font-bold text-blue-600 text-center mb-6">
          Create New Session
        </h1>
        <form onSubmit={handleFormSubmit} className="space-y-6">
          <div>
            <label className="block text-gray-700">Session Start Time</label>
            <input
              type="datetime-local"
              value={sessionStartTime}
              onChange={(e) => setSessionStartTime(e.target.value)}
              className="w-full p-3 border rounded-lg"
              required
            />
          </div>
          <div>
            <label className="block text-gray-700">Session End Time</label>
            <input
              type="datetime-local"
              value={sessionEndTime}
              onChange={(e) => setSessionEndTime(e.target.value)}
              className="w-full p-3 border rounded-lg"
              required
            />
          </div>
          <div>
            <label className="block text-gray-700">Activity Summary</label>
            <textarea
              value={activitySummary}
              onChange={(e) => setActivitySummary(e.target.value)}
              className="w-full p-3 border rounded-lg"
              placeholder="Brief summary of the session activity"
              required
            />
          </div>
          <div>
            <label className="block text-gray-700">Session Duration</label>
            <input
              type="text"
              value={duration}
              readOnly
              className="w-full p-3 border rounded-lg bg-gray-100"
            />
          </div>
          <button
            type="submit"
            className="w-full bg-blue-600 text-white p-3 rounded-lg font-semibold"
          >
            Create Session
          </button>
        </form>
      </motion.div>
    </div>
  );
};

export default Session;

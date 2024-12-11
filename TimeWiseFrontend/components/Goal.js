"use client";

import { useState } from "react";
import { motion } from "framer-motion";

const Goal = () => {
  const [goalDescription, setGoalDescription] = useState("");
  const [goalDeadline, setGoalDeadline] = useState("");
  const [progressStatus, setProgressStatus] = useState("Not Started");
  const [goalComments, setGoalComments] = useState("");

  const handleFormSubmit = (e) => {
    e.preventDefault();
    // Submit goal data to backend (to be replaced with API call later)
    const goalData = {
      goal_description: goalDescription,
      goal_deadline: new Date(goalDeadline),
      progress_status: progressStatus,
      goal_creation_date: new Date(),
      goal_completion_status: "Incomplete",
      goal_comments: goalComments.split(","),
    };
    console.log("Goal Created:", goalData);
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
          Create New Goal
        </h1>
        <form onSubmit={handleFormSubmit} className="space-y-6">
          <div>
            <label className="block text-gray-700">Goal Description</label>
            <input
              type="text"
              value={goalDescription}
              onChange={(e) => setGoalDescription(e.target.value)}
              className="w-full p-3 border rounded-lg"
              required
            />
          </div>
          <div>
            <label className="block text-gray-700">Goal Deadline</label>
            <input
              type="date"
              value={goalDeadline}
              onChange={(e) => setGoalDeadline(e.target.value)}
              className="w-full p-3 border rounded-lg"
              required
            />
          </div>
          <div>
            <label className="block text-gray-700">Progress Status</label>
            <select
              value={progressStatus}
              onChange={(e) => setProgressStatus(e.target.value)}
              className="w-full p-3 border rounded-lg"
            >
              <option value="Not Started">Not Started</option>
              <option value="In Progress">In Progress</option>
              <option value="Completed">Completed</option>
            </select>
          </div>
          <div>
            <label className="block text-gray-700">Comments</label>
            <input
              type="text"
              value={goalComments}
              onChange={(e) => setGoalComments(e.target.value)}
              className="w-full p-3 border rounded-lg"
              placeholder="Comma-separated comments"
            />
          </div>
          <button
            type="submit"
            className="w-full bg-blue-600 text-white p-3 rounded-lg font-semibold"
          >
            Create Goal
          </button>
        </form>
      </motion.div>
    </div>
  );
};

export default Goal;

"use client";

import { useState } from "react";
import { motion } from "framer-motion";

const Team = () => {
  const [teamName, setTeamName] = useState("");
  const [teamMembers, setTeamMembers] = useState("");
  const [createdBy, setCreatedBy] = useState("");
  const [teamStatus, setTeamStatus] = useState("Active");

  const handleFormSubmit = (e) => {
    e.preventDefault();
    // Later, this data will be sent to the backend for team creation.
    const teamData = {
      team_name: teamName,
      team_members: teamMembers.split(","),
      created_by: createdBy,
      creation_date: new Date(),
      team_status: teamStatus,
    };
    console.log("Team created:", teamData);
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
          Create a New Team
        </h1>
        <form onSubmit={handleFormSubmit} className="space-y-6">
          <div>
            <label className="block text-gray-700">Team Name</label>
            <input
              type="text"
              value={teamName}
              onChange={(e) => setTeamName(e.target.value)}
              className="w-full p-3 border rounded-lg"
              required
            />
          </div>
          <div>
            <label className="block text-gray-700">Team Members (Comma Separated)</label>
            <input
              type="text"
              value={teamMembers}
              onChange={(e) => setTeamMembers(e.target.value)}
              className="w-full p-3 border rounded-lg"
              required
            />
          </div>
          <div>
            <label className="block text-gray-700">Created By (User ID)</label>
            <input
              type="text"
              value={createdBy}
              onChange={(e) => setCreatedBy(e.target.value)}
              className="w-full p-3 border rounded-lg"
              required
            />
          </div>
          <div>
            <label className="block text-gray-700">Team Status</label>
            <select
              value={teamStatus}
              onChange={(e) => setTeamStatus(e.target.value)}
              className="w-full p-3 border rounded-lg"
            >
              <option value="Active">Active</option>
              <option value="Inactive">Inactive</option>
              <option value="Archived">Archived</option>
            </select>
          </div>
          <button
            type="submit"
            className="w-full bg-blue-600 text-white p-3 rounded-lg font-semibold"
          >
            Create Team
          </button>
        </form>
      </motion.div>
    </div>
  );
};

export default Team;

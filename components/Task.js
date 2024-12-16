"use client";

import { useState } from "react";
import { motion } from "framer-motion";
import Image from "next/image";

function Task() {
  const [taskDetails, setTaskDetails] = useState({
    taskName: "",
    taskDescription: "",
    taskPriority: "",
    taskDeadline: "",
    taskOwner: "",
    taskParticipants: "",
    taskStatus: "Pending",
    taskCategory: "",
    taskComments: "",
  });

  const [error, setError] = useState("");

  const validateForm = () => {
    if (!taskDetails.taskName.trim()) return "Task name is required.";
    if (!taskDetails.taskDescription.trim())
      return "Task description is required.";
    if (!taskDetails.taskPriority.trim())
      return "Please select a task priority.";
    if (!taskDetails.taskDeadline.trim()) return "Task deadline is required.";
    if (!taskDetails.taskOwner.trim()) return "Task owner is required.";
    return "";
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const errorMsg = validateForm();
    if (errorMsg) {
      setError(errorMsg);
      return;
    }
    setError("");
    alert("Task created successfully!");
    console.log("Task Details:", taskDetails);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setTaskDetails({ ...taskDetails, [name]: value });
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-cover bg-center bg-gray-400">
      <motion.div
        initial={{ opacity: 0, y: -50 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="w-full max-w-2xl p-8 bg-gray-800 bg-opacity-90 rounded-lg shadow-lg"
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
        <h2 className="text-3xl font-bold text-white text-center mb-6">
          Create a Task
        </h2>
        {error && <p className="text-red-500 text-center mb-4">{error}</p>}
        <form onSubmit={handleSubmit} className="space-y-4">
          <input
            type="text"
            name="taskName"
            placeholder="Task Name"
            value={taskDetails.taskName}
            onChange={handleInputChange}
            className="w-full p-3 border border-gray-300 rounded-md"
          />
          <textarea
            name="taskDescription"
            placeholder="Task Description"
            value={taskDetails.taskDescription}
            onChange={handleInputChange}
            className="w-full p-3 border border-gray-300 rounded-md"
          ></textarea>
          <select
            name="taskPriority"
            value={taskDetails.taskPriority}
            onChange={handleInputChange}
            className="w-full p-3 border border-gray-300 rounded-md"
          >
            <option value="">Select Priority</option>
            <option value="High">High</option>
            <option value="Medium">Medium</option>
            <option value="Low">Low</option>
          </select>
          <input
            type="date"
            name="taskDeadline"
            value={taskDetails.taskDeadline}
            onChange={handleInputChange}
            className="w-full p-3 border border-gray-300 rounded-md"
          />
          <input
            type="text"
            name="taskOwner"
            placeholder="Task Owner"
            value={taskDetails.taskOwner}
            onChange={handleInputChange}
            className="w-full p-3 border border-gray-300 rounded-md"
          />
          <input
            type="text"
            name="taskParticipants"
            placeholder="Task Participants (comma-separated IDs)"
            value={taskDetails.taskParticipants}
            onChange={handleInputChange}
            className="w-full p-3 border border-gray-300 rounded-md"
          />
          <select
            name="taskStatus"
            value={taskDetails.taskStatus}
            onChange={handleInputChange}
            className="w-full p-3 border border-gray-300 rounded-md"
          >
            <option value="Pending">Pending</option>
            <option value="In Progress">In Progress</option>
            <option value="Completed">Completed</option>
          </select>
          <input
            type="text"
            name="taskCategory"
            placeholder="Task Category"
            value={taskDetails.taskCategory}
            onChange={handleInputChange}
            className="w-full p-3 border border-gray-300 rounded-md"
          />
          <textarea
            name="taskComments"
            placeholder="Task Comments"
            value={taskDetails.taskComments}
            onChange={handleInputChange}
            className="w-full p-3 border border-gray-300 rounded-md"
          ></textarea>
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            className="w-full py-3 bg-gray-500 text-white font-semibold rounded-lg hover:bg-gray-900 transition duration-300"
            type="submit"
          >
            Create Task
          </motion.button>
        </form>
      </motion.div>
    </div>
  );
}

export default Task;

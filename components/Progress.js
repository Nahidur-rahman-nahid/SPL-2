"use client";

import { motion } from "framer-motion";

const Progress = () => {
  const progressData = [
    {
      _id: "1",
      user_id: "U001",
      task_id: "T001",
      progress_amount: 70,
      progress_history: ["Started on 2024-11-20", "50% by 2024-11-21"],
      progress_report: "The task is on track. Keep up the good work!",
    },
    {
      _id: "2",
      user_id: "U002",
      task_id: "T002",
      progress_amount: 40,
      progress_history: [
        "Started on 2024-11-19",
        "Paused due to technical issues",
      ],
      progress_report:
        "Consider focusing more on this task to meet the deadline.",
    },
  ];

  return (
    <div className="min-h-screen bg-gray-100 p-8">
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="max-w-5xl mx-auto bg-white p-6 rounded-lg shadow-md"
      >
        <h1 className="text-3xl font-bold text-blue-600 text-center mb-6">
          Progress Overview
        </h1>
        <div className="space-y-6">
          {progressData.map((progress) => (
            <div
              key={progress._id}
              className="border border-gray-300 p-4 rounded-lg shadow-sm"
            >
              <h2 className="text-xl font-semibold text-gray-800">
                Task ID: {progress.task_id}
              </h2>
              <p className="text-sm text-gray-500">
                User ID: {progress.user_id}
              </p>
              <div className="my-4">
                <div className="w-full bg-gray-200 rounded-full h-4">
                  <div
                    className="bg-blue-500 h-4 rounded-full"
                    style={{ width: `${progress.progress_amount}%` }}
                  ></div>
                </div>
                <p className="text-sm text-gray-600 mt-2">
                  Progress: {progress.progress_amount}%
                </p>
              </div>
              <h3 className="text-lg font-semibold">Progress History:</h3>
              <ul className="list-disc list-inside text-gray-700">
                {progress.progress_history.map((history, index) => (
                  <li key={index}>{history}</li>
                ))}
              </ul>
              <p className="mt-4 text-gray-800">
                <strong>Report:</strong> {progress.progress_report}
              </p>
            </div>
          ))}
        </div>
      </motion.div>
    </div>
  );
};

export default Progress;

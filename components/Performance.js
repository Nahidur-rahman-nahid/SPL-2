"use client";
import { motion } from "framer-motion";

const Performance = () => {
  const performanceData = [
    {
      _id: "1",
      user_id: "U001",
      performance_history: [
        "Completed 3 tasks last week",
        "Missed 1 deadline",
        "Achieved 90% goal this month",
      ],
      performance_score: 85,
      performance_report:
        "Great performance! Focus on reducing missed deadlines to improve further.",
    },
    {
      _id: "2",
      user_id: "U002",
      performance_history: [
        "Completed 2 tasks last week",
        "Delayed on 2 tasks",
        "Achieved 60% goal this month",
      ],
      performance_score: 60,
      performance_report:
        "Average performance. Try to stay consistent and manage time effectively.",
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
        <h1 className="text-3xl font-bold text-green-600 text-center mb-6">
          Performance Overview
        </h1>
        <div className="space-y-6">
          {performanceData.map((performance) => (
            <div
              key={performance._id}
              className="border border-gray-300 p-4 rounded-lg shadow-sm"
            >
              <h2 className="text-xl font-semibold text-gray-800">
                User ID: {performance.user_id}
              </h2>
              <div className="my-4">
                <h3 className="text-lg font-semibold">Performance Score:</h3>
                <div className="w-full bg-gray-200 rounded-full h-4">
                  <div
                    className={`h-4 rounded-full ${
                      performance.performance_score >= 75
                        ? "bg-green-500"
                        : "bg-red-500"
                    }`}
                    style={{ width: `${performance.performance_score}%` }}
                  ></div>
                </div>
                <p className="text-sm text-gray-600 mt-2">
                  Score: {performance.performance_score}%
                </p>
              </div>
              <h3 className="text-lg font-semibold">Performance History:</h3>
              <ul className="list-disc list-inside text-gray-700">
                {performance.performance_history.map((history, index) => (
                  <li key={index}>{history}</li>
                ))}
              </ul>
              <p className="mt-4 text-gray-800">
                <strong>Report:</strong> {performance.performance_report}
              </p>
            </div>
          ))}
        </div>
      </motion.div>
    </div>
  );
};

export default Performance;

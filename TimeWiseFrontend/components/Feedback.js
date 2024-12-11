"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";

const Feedback = () => {
  const [formattedFeedback, setFormattedFeedback] = useState([]);
  const feedbackData = [
    {
      _id: "1",
      user_id: "U001",
      feedback_type: "Feature Request",
      feedback_message: "I would love to see dark mode implemented.",
      date_submitted: new Date("2024-11-20T14:00:00"),
      rating: 4,
    },
    {
      _id: "2",
      user_id: "U002",
      feedback_type: "Bug Report",
      feedback_message: "The app crashes when trying to submit a task.",
      date_submitted: new Date("2024-11-19T16:30:00"),
      rating: 2,
    },
  ];

  useEffect(() => {
    const formattedData = feedbackData.map((feedback) => ({
      ...feedback,
      formattedDateSubmitted: new Date(feedback.date_submitted).toLocaleString(),
    }));
    setFormattedFeedback(formattedData);
  }, []);

  return (
    <div className="min-h-screen bg-gray-100 p-8">
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="max-w-4xl mx-auto bg-white p-6 rounded-lg shadow-md"
      >
        <h1 className="text-3xl font-bold text-green-600 text-center mb-6">
          User Feedback
        </h1>
        <div className="space-y-6">
          {formattedFeedback.map((feedback) => (
            <div
              key={feedback._id}
              className="border border-gray-300 p-4 rounded-lg shadow-sm"
            >
              <h2 className="text-lg font-semibold text-gray-800">
                Feedback Type: {feedback.feedback_type}
              </h2>
              <p className="text-gray-600">{feedback.feedback_message}</p>
              <p className="text-sm text-gray-500 mt-2">
                Rating: {feedback.rating} / 5
              </p>
              <p className="text-sm text-gray-500">
                Date Submitted: {feedback.formattedDateSubmitted}
              </p>
            </div>
          ))}
        </div>
      </motion.div>
    </div>
  );
};

export default Feedback;

"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";

const Reminders = () => {
  const [formattedReminders, setFormattedReminders] = useState([]);
  const reminderData = [
    {
      _id: "1",
      user_id: "U001",
      task_id: "T001",
      reminder_message: "Weekly status update due tomorrow.",
      reminder_date_time: new Date("2024-11-22T09:00:00"),
      reminder_frequency: "Weekly",
    },
    {
      _id: "2",
      user_id: "U002",
      task_id: "T002",
      reminder_message: "Prepare slides for the presentation.",
      reminder_date_time: new Date("2024-11-23T14:30:00"),
      reminder_frequency: "Once",
    },
  ];

  useEffect(() => {
    const formattedData = reminderData.map((reminder) => ({
      ...reminder,
      formattedReminderDateTime: new Date(reminder.reminder_date_time).toLocaleString(),
    }));
    setFormattedReminders(formattedData);
  }, []);

  return (
    <div className="min-h-screen bg-gray-100 p-8">
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="max-w-5xl mx-auto bg-white p-6 rounded-lg shadow-md"
      >
        <h1 className="text-3xl font-bold text-teal-600 text-center mb-6">
          Reminders
        </h1>
        <div className="space-y-6">
          {formattedReminders.map((reminder) => (
            <div
              key={reminder._id}
              className="border border-gray-300 p-4 rounded-lg shadow-sm"
            >
              <h2 className="text-lg font-semibold text-gray-800">
                Task ID: {reminder.task_id}
              </h2>
              <p className="text-gray-600">{reminder.reminder_message}</p>
              <p className="text-sm text-gray-500 mt-2">
                Reminder Date/Time: {reminder.formattedReminderDateTime}
              </p>
              <p className="text-sm text-gray-500">
                Frequency: {reminder.reminder_frequency}
              </p>
            </div>
          ))}
        </div>
      </motion.div>
    </div>
  );
};

export default Reminders;

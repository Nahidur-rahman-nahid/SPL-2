"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";

const Notifications = () => {
  const [formattedNotifications, setFormattedNotifications] = useState([]);
  const notificationData = [
    {
      _id: "1",
      user_id: "U001",
      notification_type: "Info",
      notification_message: "Your task deadline is approaching!",
      notification_status: "Unread",
      timestamp: new Date("2024-11-21T10:30:00"),
    },
    {
      _id: "2",
      user_id: "U002",
      notification_type: "Alert",
      notification_message: "Task submission is overdue.",
      notification_status: "Read",
      timestamp: new Date("2024-11-20T08:00:00"),
    },
  ];

  useEffect(() => {
    const formattedData = notificationData.map((notification) => ({
      ...notification,
      formattedTimestamp: new Date(notification.timestamp).toLocaleString(),
    }));
    setFormattedNotifications(formattedData);
  }, []);

  return (
    <div className="min-h-screen bg-gray-100 p-8">
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="max-w-5xl mx-auto bg-white p-6 rounded-lg shadow-md"
      >
        <h1 className="text-3xl font-bold text-purple-600 text-center mb-6">
          Notifications
        </h1>
        <div className="space-y-6">
          {formattedNotifications.map((notification) => (
            <div
              key={notification._id}
              className={`border p-4 rounded-lg shadow-sm ${
                notification.notification_status === "Unread"
                  ? "bg-purple-100 border-purple-400"
                  : "bg-gray-50 border-gray-300"
              }`}
            >
              <h2 className="text-lg font-semibold text-gray-800">
                {notification.notification_type}
              </h2>
              <p className="text-gray-600">{notification.notification_message}</p>
              <p className="text-sm text-gray-500 mt-2">
                Status: {notification.notification_status}
              </p>
              <p className="text-sm text-gray-500">
                Timestamp: {notification.formattedTimestamp}
              </p>
            </div>
          ))}
        </div>
      </motion.div>
    </div>
  );
};

export default Notifications;

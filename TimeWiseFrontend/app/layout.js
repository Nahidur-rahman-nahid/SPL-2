// app/layout.js
import "../styles/globals.css";

export const metadata = {
  title: "TimeWise",
  description: "A system for managing time and productivity efficiently through task management.",
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
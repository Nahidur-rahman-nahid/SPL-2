import SignIn from "@/components/SignIn";
import SignUp from "@/components/SignUp";
import "../styles/globals.css";
import WelcomeMessage from "@/components/WelcomeMessage";
import Task from "@/components/Task";


export const metadata = {
  title: "TimeWise",
  description:
    "A system for managing time and productivity efficiently through task management.",
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body>
      {/* <WelcomeMessage /> */}
      {/* <SignUp/> */}
      {/* <SignIn/> */}
      <Task/>
      </body>
    </html>
  );
}

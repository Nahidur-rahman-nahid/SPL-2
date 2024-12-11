import SignIn from "@/components/SignIn";
import SignUp from "@/components/SignUp";
import "../styles/globals.css";
import WelcomeMessage from "@/components/WelcomeMessage";
import Task from "@/components/Task";
import Progress from "@/components/Progress";
import Performance from "@/components/Performance";
import Notifications from "@/components/Notification";
import Reminders from "@/components/Reminder";
import Feedback from "@/components/Feedback";
import Team from "@/components/Team";
import Goal from "@/components/Goal";
import Session from "@/components/Session";
import SidebarLeft from "@/components/SidebarLeft";
import SidebarRight from "@/components/SidebarRight";
import Navbar from "@/components/Navbar";


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
      {/* <SignIn/> 
      <Task/>
      <Progress/>
      <Performance/>
      */}
      {/* <Notifications/>
      <Reminders/>
      <Team/>
      <Feedback/>
      <Goal/>
      <Session/> */}
      <Navbar/>
      <SidebarLeft/>
      <SidebarRight/>
      </body>
    </html>
  );
}

import "../styles/globals.css";
import WelcomeMessage from "@/components/WelcomeMessage";
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
      <WelcomeMessage />
      
      {/* <Navbar/>
      <SidebarLeft/>
      <SidebarRight/> */}
      </body>
    </html>
  );
}

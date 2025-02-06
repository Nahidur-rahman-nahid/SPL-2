// app/home/layout.js (or whatever your path is)
import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import Navbar from "@/components/Navbar";
import SidebarLeft from "@/components/SidebarLeft";
import SidebarRight from "@/components/SidebarRight";

async function fetchUserData() {
  const cookieStore = await cookies();
  const token = cookieStore.get("timewise-auth-token")?.value;

  if (!token) {
    redirect("/welcome");
  }

  try {
    const res = await fetch(
      `${process.env.BACKEND_SERVER_URL}/api/users/account/personal/details`,
      {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
        },
        cache: "no-store",
      }
    );

    if (!res.ok) {
      redirect("/welcome");
    }

    return res.json();
  } catch (error) {
    console.error("Failed to fetch user data:", error);
    redirect("/welcome");
  }
}

export default async function HomeLayout({ children }) {
  const userData = await fetchUserData();

  return (
    <div className="flex flex-col h-screen">
      <Navbar />
      <div className="flex flex-1 overflow-hidden">
        <SidebarLeft />
        <main className="flex-1 overflow-y-auto p-6 md:ml-24 md:mr-24 lg:ml-48 lg:mr-48">
          {children}
        </main>
        {/* <SidebarRight userData={userData} /> */}
      </div>
    </div>
  );
}
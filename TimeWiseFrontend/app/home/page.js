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
        cache: "no-store", // Disable caching for dynamic data
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

export default async function HomePage() {
  const userData = await fetchUserData();

  return (
    <div>
      <Navbar />
      <SidebarLeft />
      <SidebarRight userData={userData} />
    </div>
  );
}

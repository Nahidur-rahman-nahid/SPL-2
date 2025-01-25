import { cookies } from "next/headers";

export async function GET(req) {
  try {
    // Await cookies() before accessing the cookie
    const cookieStore = await cookies();
    const token = cookieStore.get('timewise-auth-token')?.value;

    if (!token) {
      return new Response("Unauthorized", { status: 401 });
    }

    // Fetch user details from Spring Boot backend or any other service
    const response = await fetch(
      `${process.env.BACKEND_SERVER_URL}/api/users/account/personal/details`,
      {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`, // Pass token to backend service
        },
      }
    );

    if (!response.ok) {
      return new Response("Unauthorized", { status: 401 });
    }

    const userData = await response.json();
    return new Response(JSON.stringify(userData), { status: 200 });
  } catch (error) {
    console.error("Error:", error);
    return new Response("Internal Server Error", { status: 500 });
  }
}

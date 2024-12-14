import fetch from 'node-fetch'; // Import fetch for making external requests

// POST request handler for /api/login
export async function POST(req) {
  try {
    const { userName, password } = await req.json(); // Parse the incoming request body

    // Send a request to your backend for authentication
    const response = await fetch(`${process.env.BACKEND_SERVER_URL}/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ userName, password }), // Pass the login data to the backend
    });

    // Check if the backend response is successful
    if (!response.ok) {
      const result = await response.text();
      console.error("Backend login error:", result);
      return new Response(result || "Login failed please check your credentials", { status: response.status });
    }

    // If successful, return the token from the backend
    const token = await response.text(); // Expecting JWT token
    return new Response(token, { status: 200 });

  } catch (error) {
    console.error("Error during login:", error);
    return new Response("Internal server error.", { status: 500 });
  }
}

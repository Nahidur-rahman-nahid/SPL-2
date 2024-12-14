import fetch from 'node-fetch'; // Import fetch to make API requests

// POST request handler for /api/register
export async function POST(req) {
  try {
    const userData = await req.json(); // Parse the incoming request body as JSON
    const response = await fetch(`${process.env.BACKEND_SERVER_URL}/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(userData), // Forward the data to the backend
    });

    // If the backend response is not OK, return the error message
    if (!response.ok) {
      const result = await response.text(); // Get error message as text for debugging
      console.error("Backend error response:", result);
      return new Response(result || "Registration failed.", { status: response.status });
    }

    // If successful, return the plain text (JWT token) to the frontend
    const token = await response.text();  // Expecting a JWT token as plain text
    return new Response(token, { status: 200 }); // Return the token as plain text

  } catch (error) {
    console.error('Error:', error);
    return new Response('Internal server error.', { status: 500 });
  }
}

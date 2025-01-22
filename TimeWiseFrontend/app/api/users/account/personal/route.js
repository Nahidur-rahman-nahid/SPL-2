// app/api/user/current/details/route.js

export async function GET(req) {
    try {
      // Get the token from the Authorization header
      const token = req.headers.get('Authorization')?.split(' ')[1]; // Extract token from "Bearer <token>"
  
      if (!token) {
        return new Response('Unauthorized', { status: 401 });
      }
  
      // Fetch user details from Spring Boot backend or any other service
      const response = await fetch(`${process.env.BACKEND_SERVER_URL}/api/users/account/personal/details`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`, // Pass token to backend service
        },
      });
  
      if (!response.ok) {
        return new Response('Unauthorized', { status: 401 });
      }
  
      const userData = await response.json();
      return new Response(JSON.stringify(userData), { status: 200 });
    } catch (error) {
      console.error('Error:', error);
      return new Response('Internal Server Error', { status: 500 });
    }
  }
  
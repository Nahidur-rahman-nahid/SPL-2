import { NextResponse } from 'next/server';
import { cookies } from 'next/headers';

export async function POST(req) {
  try {
    // Parse the incoming request body to extract user credentials (username and password)
    const { userName, password } = await req.json();

    // Send a POST request to the backend to authenticate the user
    const response = await fetch(`${process.env.BACKEND_SERVER_URL}/api/users/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ userName, password }), // Send login data to the backend
    });

    // Check if the backend response was successful
    if (!response.ok) {
      const result = await response.text();
      console.error("Backend login error:", result);
      return NextResponse.json(
        { error: result || "Login failed, please check your credentials" },
        { status: response.status }
      );
    }

    // Extract the token from the backend's response (assuming it's returned as text)
    const token = await response.text();
    const cookiesInstance = await cookies();
    
    // Set the authentication token in a cookie
    cookiesInstance.set('timewise-auth-token', token, {
      httpOnly: true,
      secure: process.env.NODE_ENV === 'production',
      sameSite: 'strict',
      path: '/',
      maxAge: 7 * 24 * 60 * 60, // Token will expire in 1 week
    });

    // Return the token along with a success message
    return NextResponse.json({
      token,
      message: 'Login successful!',
    });

  } catch (error) {
    console.error("Error during login:", error);
    return NextResponse.json(
      { error: 'Internal server error' },
      { status: 500 }
    );
  }
}

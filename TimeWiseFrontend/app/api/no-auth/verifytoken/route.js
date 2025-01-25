// app/api/verifyToken/route.js

import { NextResponse } from 'next/server';

export async function POST(req) {
  try {
    const { token } = await req.json();

    // Replace with your backend token verification logic
    const serverUrl = process.env.BACKEND_SERVER_URL || "http://localhost:8081";
    const response = await fetch(`${serverUrl}/verifyToken`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ token }),
    });

    // If the response from the backend is OK, return 200
    if (response.ok) {
      return NextResponse.json({ authenticated: true });
    } else {
      return NextResponse.json({ authenticated: false });
    }
  } catch (error) {
    console.error("Error during token verification:", error);
    return NextResponse.json({ authenticated: false });
  }
}

import { NextResponse } from 'next/server';
import { cookies } from 'next/headers';

export async function POST(req) {
  try {
    const credentials = await req.json();
    console.log('Forwarding credentials to backend:', credentials);
    
    const response = await fetch(
      `${process.env.BACKEND_SERVER_URL}/api/users/login/forgot/verify`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(credentials),
      }
    );
    
    // Check if the backend response was successful
    if (!response.ok) {
      const result = await response.text();
      console.error('Account recovery error:', result);
      return NextResponse.json(
        {
          error: result || 'Account recovery failed, please check your credentials and verification code',
        },
        { status: response.status }
      );
    }
    
    // Extract the token from the backend's response
    const token = await response.text();
    const cookiesInstance = cookies();
    
    // Set the authentication token in a cookie
    cookiesInstance.set('timewise-auth-token', token, {
      httpOnly: true,
      secure: process.env.NODE_ENV === 'production',
      sameSite: 'strict',
      path: '/',
      maxAge: 7 * 24 * 60 * 60, // Token will expire in 1 week
    });
    
    // Instead of redirecting, return a JSON response with a redirect URL
    return NextResponse.json({
      success: true,
      message: 'Account recovery successful!'
    });
    
  } catch (error) {
    console.error('Account recovery Error:', error);
    return NextResponse.json(
      { error: 'Internal server error' },
      { status: 500 }
    );
  }
}
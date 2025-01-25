import { NextResponse } from 'next/server';
import { cookies } from 'next/headers';

export async function POST(req) {
  try {
    const url = new URL(req.url);
    const code = url.searchParams.get('code');
    const userData = await req.json();

    if (!code) {
      return NextResponse.json(
        { error: 'Verification code is required' }, 
        { status: 400 }
      );
    }

    const response = await fetch(
      `${process.env.BACKEND_SERVER_URL}/api/users/register/complete?code=${code}`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(userData),
      }
    );

    if (!response.ok) {
      const errorText = await response.text();
      return NextResponse.json(
        { error: errorText || 'Failed to complete registration' },
        { status: response.status }
      );
    }

    const token = await response.text();
    const cookiesInstance = await cookies();
    
    // Set the authentication token in a cookie
    cookiesInstance.set('timewise-auth-token', token, {
      httpOnly: true,
      secure: process.env.NODE_ENV === 'production',
      sameSite: 'strict',
      path: '/',
      maxAge: 7 * 24 * 60 * 60 // 1 week
    });

    return NextResponse.json({ 
      token, 
      message: 'Registration completed successfully!' 
    });

  } catch (error) {
    console.error('Registration Error:', error);
    return NextResponse.json(
      { error: 'Internal server error' }, 
      { status: 500 }
    );
  }
}
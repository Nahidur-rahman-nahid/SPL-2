import { NextResponse } from 'next/server';
import { cookies } from 'next/headers';

export async function GET(req) {
  try {
    const url = new URL(req.url);
    const userEmail = url.searchParams.get('userEmail');

   
    if (!userEmail) {
      return NextResponse.json(
        { error: 'User Email is required' }, 
        { status: 400 }
      );
    }
     

    const response = await fetch(
      `${process.env.BACKEND_SERVER_URL}/api/users/login/forgot?userEmail=${userEmail}`,
      {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
          },
      }
    );

    if (!response.ok) {
      const errorText = await response.text();
      return NextResponse.json(
        { error: errorText || 'Failed to send verfication code' },
        { status: response.status }
      );
    }
    
    return NextResponse.json({ status: 200 });
  } catch (error) {
    console.error('Verfication code sending Error:', error);
    return NextResponse.json(
      { error: 'Internal server error' },
      { status: 500 }
    );
  }
}
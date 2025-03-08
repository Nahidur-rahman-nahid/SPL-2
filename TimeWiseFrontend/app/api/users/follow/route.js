import { NextResponse } from 'next/server';
import { cookies } from 'next/headers';
import { redirect } from 'next/dist/server/api-utils';

export async function POST(req) {
  try {
    const cookieStore = await cookies();
  const token = cookieStore.get("timewise-auth-token")?.value;

    if (!token) {
      redirect("/login");
    }
    const url = new URL(req.url);
    const userName = url.searchParams.get('userName');

    const response = await fetch(
      `${process.env.BACKEND_SERVER_URL}/api/users/follow?userName=${encodeURIComponent(userName)}`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        
      }
    );

    if (!response.ok) {
      const errorText = await response.text();
      return NextResponse.json(
        { error: errorText || 'Failed to update following status' },
        { status: response.status }
      );
    }

    const text = await response.text();
    return NextResponse.json(text, { status: 201 });
  } catch (error) {
    console.error('Following status modification Error:', error);
    return NextResponse.json(
      { error: 'Internal server error' },
      { status: 500 }
    );
  }
}

import { NextResponse } from 'next/server';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation'; 

export async function POST(req) {
  try {
    const  userTasks = await req.json();
    const url = new URL(req.url);
    const userPrompt = url.searchParams.get('userPrompt');
   

    const cookieStore = await cookies();
    const token = cookieStore.get('timewise-auth-token')?.value;

    if (!token) {
      return NextResponse.redirect('/login'); 
    }
    

    
    const response = await fetch( `${process.env.BACKEND_SERVER_URL}/api/ai/session/generate?userPrompt=${userPrompt}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(userTasks),
    });

    if (!response.ok) {
      const errorText = await response.text();
      return NextResponse.json(
        { error: errorText || 'Failed to generate Session.' },
        { status: response.status }
      );
    }

    const generatedSession = await response.json();
    return NextResponse.json(generatedSession, { status: 201 });
  } catch (error) {
    console.error('Session Generation Error:', error);
    return NextResponse.json(
      { error: 'Internal server error' },
      { status: 500 }
    );
  }
}
import { NextResponse } from 'next/server';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation'; 

export async function POST(req) {
  try {
    const data = await req.json();
    const url = new URL(req.url);
    const context = url.searchParams.get('context');
    const analysisType = url.searchParams.get('analysisType');

    const cookieStore = await cookies();
    const token = cookieStore.get('timewise-auth-token')?.value;

    if (!token) {
      return NextResponse.redirect('/login'); 
    }
    
    const response = await fetch( `${process.env.BACKEND_SERVER_URL}/api/ai/analyze-list?context=${context}&analysisType=${analysisType}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(data),
    });

    if (!response.ok) {
      const errorText = await response.text();
      return NextResponse.json(
        { error: errorText || 'Failed to generate Response.' },
        { status: response.status }
      );
    }

    const generatedResponse = await response.text();
    return NextResponse.json(generatedResponse, { status: 201 });
  } catch (error) {
    console.error('Response Generation Error:', error);
    return NextResponse.json(
      { error: 'Internal server error' },
      { status: 500 }
    );
  }
}
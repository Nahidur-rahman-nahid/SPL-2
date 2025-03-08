import { NextResponse } from 'next/server';
import { cookies } from 'next/headers';

export async function GET(req) {
  try {
    const url = new URL(req.url);
    const feedbackSubject = url.searchParams.get('feedbackSubject');

   
    if (!feedbackSubject) {
      return NextResponse.json(
        { error: 'Feedback subject is required' }, 
        { status: 400 }
      );
    }
     const cookieStore = await cookies();
      const token = cookieStore.get("timewise-auth-token")?.value;
    
        if (!token) {
          redirect("/login");
        }

    const response = await fetch(
      `${process.env.BACKEND_SERVER_URL}/api/ai/feedback/autocomplete?feedbackSubject=${feedbackSubject}`,
      {
        method: 'GET',
        headers: { 'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
         },
      }
    );

    if (!response.ok) {
      const errorText = await response.text();
      return NextResponse.json(
        { error: errorText || 'Failed to get autocompleted feedback' },
        { status: response.status }
      );
    }
    
    const data = await response.text(); 
    return NextResponse.json(data);
  } catch (error) {
    console.error('Autocompleted feedback getting error:', error);
    return NextResponse.json(
      { error: 'Internal server error' },
      { status: 500 }
    );
  }
}
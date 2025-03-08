import { NextResponse } from 'next/server';
import { cookies } from 'next/headers';

export async function DELETE(req) {
  try {
    
    const feedback=await req.json();
   
    if (!feedback) {
      return NextResponse.json(
        { error: 'Feedback  is required' }, 
        { status: 400 }
      );
    }
     const cookieStore = await cookies();
      const token = cookieStore.get("timewise-auth-token")?.value;
    
        if (!token) {
          redirect("/login");
        }

    const response = await fetch(
      `${process.env.BACKEND_SERVER_URL}/api/feedbacks/remove`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
         },
         body: JSON.stringify(feedback),
      }
    );

    if (!response.ok) {
      const errorText = await response.text();
      return NextResponse.json(
        { error: errorText || 'Failed to remove feedback' },
        { status: response.status }
      );
    }
    
    return NextResponse.json({ status: 200 });
  } catch (error) {
    console.error('Feedback removing Error:', error);
    return NextResponse.json(
      { error: 'Internal server error' },
      { status: 500 }
    );
  }
}
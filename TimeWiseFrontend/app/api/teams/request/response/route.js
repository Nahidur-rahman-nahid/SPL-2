import { NextResponse } from 'next/server';
import { cookies } from 'next/headers';
import { redirect } from 'next/dist/server/api-utils';

export async function PUT(req) { 
    try {
        
        const cookieStore = await cookies();
        const token = cookieStore.get("timewise-auth-token")?.value;
      

        if (!token) {
          redirect("/login");
        }
    
        const url = new URL(req.url);
        const teamName = url.searchParams.get('teamName');
        const respondedTo = url.searchParams.get('respondedTo');
        const reply = url.searchParams.get('response');
       

        if (!teamName || !respondedTo || !reply) {
            return NextResponse.json(
                { error: 'Missing teamName or recipient or response' },
                { status: 400 }
            );
        }

        const response = await fetch(
            `${process.env.BACKEND_SERVER_URL}/api/teams/user/join/request/response?teamName=${teamName}&respondedTo=${respondedTo}&response=${reply}`,
            {
              method: 'PUT',
              headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${token}`,
              },
            }
          );

        if (!response.ok) {
            const errorText = await response.text();
            return NextResponse.json(
                { error: errorText || 'Failed to send team joining request response' },
                { status: response.status }
            );
        }

        const responseText = await response.text();
        return NextResponse.json(responseText);

    } catch (error) {
        console.error('Error sending team joining request:', error);
        return NextResponse.json(
            { error: 'Internal server error' },
            { status: 500 }
        );
    }
}
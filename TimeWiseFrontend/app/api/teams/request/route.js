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
        const teamName = url.searchParams.get('teamName');
       

        if (!teamName) {
            return NextResponse.json(
                { error: 'Missing teamName' },
                { status: 400 }
            );
        }

        const response = await fetch(
            `${process.env.BACKEND_SERVER_URL}/api/teams/user/join/request?teamName=${teamName}`,
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
                { error: errorText || 'Failed to send team request' },
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
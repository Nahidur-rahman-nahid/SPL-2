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
        const recipient = url.searchParams.get('recipient');

        if (!teamName || !recipient) {
            return NextResponse.json(
                { error: 'Missing teamName or recepient' },
                { status: 400 }
            );
        }

        const response = await fetch(
            `${process.env.BACKEND_SERVER_URL}/api/teams/user/invite?teamName=${teamName}&recipient=${recipient}`,
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
                { error: errorText || 'Failed to fetch from backend' },
                { status: response.status }
            );
        }

        const updatedTeam = await response.json();
        return NextResponse.json(updatedTeam);

    } catch (error) {
        console.error('Error inviting member:', error);
        return NextResponse.json(
            { error: 'Internal server error' },
            { status: 500 }
        );
    }
}
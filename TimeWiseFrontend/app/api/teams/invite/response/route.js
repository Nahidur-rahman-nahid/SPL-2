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
        const action = url.searchParams.get('response');

        if (!teamName || !action) {
            return NextResponse.json(
                { error: 'Missing teamName or action' },
                { status: 400 }
            );
        }

        const response = await fetch(
            `${process.env.BACKEND_SERVER_URL}/api/teams/user/invite/response?teamName=${teamName}&response=${action}`,
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
                { error: errorText || 'Failed to send response'},
                { status: response.status }
            );
        }

        const reply = await response.text();
        return NextResponse.json(reply);

    } catch (error) {
        console.error('Error sending respone:', error);
        return NextResponse.json(
            { error: 'Internal server error' },
            { status: 500 }
        );
    }
}
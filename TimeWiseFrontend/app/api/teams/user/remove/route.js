import { NextResponse } from 'next/server';
import { cookies } from 'next/headers';
import { redirect } from 'next/dist/server/api-utils';

export async function DELETE(req) {
  try {
    
    const cookieStore = await cookies();
    const token = cookieStore.get("timewise-auth-token")?.value;

    if (!token) {
      redirect("/login");
    }

    const url = new URL(req.url);
    const teamName = url.searchParams.get('teamName');
    const memberName = url.searchParams.get('memberName');

    const response = await fetch(
        `${process.env.BACKEND_SERVER_URL}/api/teams/user/remove?teamName=${teamName}&userName=${memberName}`,
        {
          method: 'DELETE',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
          },
        }
      );


    if (!response.ok) {
      const errorText = await response.text();
      return NextResponse.json(
        { error: errorText || 'Failed to remove team member' },
        { status: response.status }
      );
    }

    const updatedTeam = await response.json();
    return NextResponse.json(updatedTeam, { status: 201 });
  } catch (error) {
    console.error('Team members removal Error:', error);
    return NextResponse.json(
      { error: 'Internal server error' },
      { status: 500 }
    );
  }
}

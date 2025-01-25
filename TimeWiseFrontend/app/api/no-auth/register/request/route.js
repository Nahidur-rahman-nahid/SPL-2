// import fetch from 'node-fetch';

export async function POST(req) {
  try {
    console.log("inside post");
    const userData = await req.json();
    const response = await fetch(`${process.env.BACKEND_SERVER_URL}/api/users/register/request`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(userData),
    });

    if (!response.ok) {
      const errorText = await response.text();
      return new Response(errorText || 'Failed to request registration code.', { status: response.status });
    }

    return new Response('Verification code sent!', { status: 200 });
  } catch (error) {
    console.error('Error:', error);
    return new Response('Internal server error.', { status: 500 });
  }
}

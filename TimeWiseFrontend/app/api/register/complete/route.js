// import fetch from 'node-fetch';

export async function POST(req) {
  try {
    const { code, ...userData } = await req.json();
    const response = await fetch(`${process.env.BACKEND_SERVER_URL}/api/users/register/complete?code=${code}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(userData),
    });

    if (!response.ok) {
      const errorText = await response.text();
      return new Response(errorText || 'Failed to complete registration.', { status: response.status });
    }

    return new Response('Registration completed successfully!', { status: 200 });
  } catch (error) {
    console.error('Error:', error);
    return new Response('Internal server error.', { status: 500 });
  }
}

// app/page.js
import { redirect } from 'next/navigation';

export const metadata = {
  title: "TimeWise - Home",
  description: "Welcome to TimeWise, manage your time and productivity."
};

// Mark the component as async
async function HomePage() { 
      redirect('/home');
}

export default HomePage;
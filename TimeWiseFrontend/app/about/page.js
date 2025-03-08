"use client"
// pages/about.js
import React, { useEffect, useState, useRef } from 'react';
import { motion, useAnimation } from 'framer-motion';
import { useInView } from 'react-intersection-observer';
import Link from 'next/link';
import {
  CalendarDays, Clock, BarChart3, Users, CheckCircle2, Bell,
  MessageCircle, LineChart, Briefcase, Award
} from 'lucide-react';

export default function AboutPage() {
  const [hoveredFeature, setHoveredFeature] = useState(null);
  const parallaxRef = useRef(null);
  
  // Refs and animations for sections
  const controls1 = useAnimation();
  const controls2 = useAnimation();
  const controls3 = useAnimation();
  const [ref1, inView1] = useInView({ triggerOnce: true, threshold: 0.1 });
  const [ref2, inView2] = useInView({ triggerOnce: true, threshold: 0.1 });
  const [ref3, inView3] = useInView({ triggerOnce: true, threshold: 0.1 });

  // Start animations when sections come into view
  useEffect(() => {
    if (inView1) controls1.start("visible");
    if (inView2) controls2.start("visible");
    if (inView3) controls3.start("visible");
  }, [controls1, controls2, controls3, inView1, inView2, inView3]);

  // Parallax effect when scrolling
  useEffect(() => {
    const handleScroll = () => {
      if (parallaxRef.current) {
        const scrollY = window.scrollY;
        parallaxRef.current.style.transform = `translateY(${scrollY * 0.5}px)`;
      }
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  // Animation variants
  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.2
      }
    }
  };

  const itemVariants = {
    hidden: { y: 20, opacity: 0 },
    visible: {
      y: 0,
      opacity: 1,
      transition: { duration: 0.6, ease: "easeOut" }
    }
  };

  // Features data
  const features = [
    {
      id: 'account',
      title: 'User Account Management',
      description: 'Create a personalized profile to access all TimeWise features securely. Follow others, update your status, and manage your visibility preferences.',
      icon: <Users className="h-8 w-8" />,
      color: 'bg-blue-500'
    },
    {
      id: 'tasks',
      title: 'Task Management',
      description: 'Create, organize, and track your tasks with detailed attributes including priorities, deadlines, and progress tracking.',
      icon: <CheckCircle2 className="h-8 w-8" />,
      color: 'bg-green-500'
    },
    {
      id: 'notifications',
      title: 'Reminders & Notifications',
      description: 'Stay on track with timely notifications about tasks, deadlines, and team activities.',
      icon: <Bell className="h-8 w-8" />,
      color: 'bg-yellow-500'
    },
    {
      id: 'progress',
      title: 'Progress & Performance Reports',
      description: 'Monitor your task completion with daily progress reports and comprehensive performance analytics.',
      icon: <BarChart3 className="h-8 w-8" />,
      color: 'bg-purple-500'
    },
    {
      id: 'stats',
      title: 'Productivity Statistics',
      description: 'Visualize your productivity with insightful charts and graphs to identify patterns and improvement areas.',
      icon: <LineChart className="h-8 w-8" />,
      color: 'bg-indigo-500'
    },
    {
      id: 'feedback',
      title: 'Messages & Feedback',
      description: 'Communicate with team members, share insights, and provide structured feedback on tasks.',
      icon: <MessageCircle className="h-8 w-8" />,
      color: 'bg-red-500'
    },
    {
      id: 'sessions',
      title: 'Session Management',
      description: 'Create focused work sessions with goals, timers, and environment settings to maximize productivity.',
      icon: <Clock className="h-8 w-8" />,
      color: 'bg-teal-500'
    },
    {
      id: 'analytics',
      title: 'Deep Work Analytics',
      description: 'Track your most productive hours and gain insights into your deep work patterns.',
      icon: <Briefcase className="h-8 w-8" />,
      color: 'bg-cyan-500'
    },
    {
      id: 'collaboration',
      title: 'Team Collaboration',
      description: 'Create teams, share tasks, and collaborate effectively with messaging and task assignments.',
      icon: <Users className="h-8 w-8" />,
      color: 'bg-orange-500'
    }
  ];

  return (
    <div className="min-h-screen bg-gradient-to-b from-slate-900 via-slate-800 to-slate-900 text-white">
      {/* Hero Section */}
      <div className="relative h-screen flex items-center justify-center overflow-hidden">
        <div ref={parallaxRef} className="absolute inset-0 z-0">
          <div className="absolute top-20 left-20 w-64 h-64 rounded-full bg-blue-500 opacity-10 blur-3xl"></div>
          <div className="absolute bottom-20 right-20 w-96 h-96 rounded-full bg-purple-500 opacity-10 blur-3xl"></div>
          <div className="absolute top-1/2 left-1/3 w-80 h-80 rounded-full bg-teal-500 opacity-10 blur-3xl"></div>
        </div>
        
        <div className="container mx-auto px-6 z-10 text-center">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8 }}
          >
            <h1 className="text-6xl md:text-7xl font-bold mb-6 bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-purple-600">
              TimeWise
            </h1>
            <p className="text-2xl md:text-3xl text-slate-300 mb-8 max-w-3xl mx-auto">
              Master your time, elevate your productivity
            </p>
            <div className="flex flex-wrap justify-center gap-4">
              <Link href="/register" className="px-8 py-3 bg-gradient-to-r from-blue-600 to-indigo-600 text-white rounded-lg font-medium hover:from-blue-700 hover:to-indigo-700 transition-all duration-300 shadow-lg shadow-blue-500/20">
                Get Started
              </Link>
              <Link href="/about#" className="px-8 py-3 bg-slate-800 text-white rounded-lg font-medium border border-slate-700 hover:bg-slate-700 transition-all duration-300">
                Explore Features
              </Link>
            </div>
          </motion.div>
        </div>
        
        <div className="absolute bottom-10 left-1/2 transform -translate-x-1/2">
          <motion.div 
            animate={{ y: [0, 10, 0] }} 
            transition={{ repeat: Infinity, duration: 1.5 }}
            className="cursor-pointer"
            onClick={() => window.scrollTo({
              top: window.innerHeight,
              behavior: 'smooth'
            })}
          >
            <svg className="w-10 h-10 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7"></path>
            </svg>
          </motion.div>
        </div>
      </div>

      {/* About Section */}
      <section className="py-20 bg-slate-900">
        <div className="container mx-auto px-6">
          <motion.div 
            ref={ref1}
            animate={controls1}
            initial="hidden"
            variants={containerVariants}
            className="grid md:grid-cols-2 gap-12 items-center"
          >
            <motion.div variants={itemVariants}>
              <h2 className="text-4xl font-bold mb-6 text-gradient bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-purple-600">About TimeWise</h2>
              <p className="text-lg text-slate-300 mb-6">
                TimeWise is a comprehensive productivity platform designed to help you organize, manage, and optimize your time. We believe that effective time management is the key to achieving your goals and maintaining a balanced life.
              </p>
              <p className="text-lg text-slate-300 mb-6">
                Our mission is to empower individuals and teams with intuitive tools that transform how they work, collaborate, and accomplish their goals.
              </p>
              <div className="flex items-center space-x-4">
                <div className="h-1 w-20 bg-gradient-to-r from-blue-500 to-purple-500"></div>
                <p className="text-slate-400 italic">"Master your minutes to master your life"</p>
              </div>
            </motion.div>
            
            <motion.div variants={itemVariants} className="relative">
              <div className="absolute inset-0 bg-gradient-to-r from-blue-500 to-purple-500 rounded-lg blur-lg opacity-20"></div>
              <div className="relative bg-slate-800 p-8 rounded-lg border border-slate-700">
                <div className="grid grid-cols-2 gap-6">
                  <div className="text-center">
                    <h3 className="text-4xl font-bold text-blue-400 mb-2">93%</h3>
                    <p className="text-slate-400">Users report improved productivity</p>
                  </div>
                  <div className="text-center">
                    <h3 className="text-4xl font-bold text-purple-400 mb-2">4.2h</h3>
                    <p className="text-slate-400">Average weekly time saved</p>
                  </div>
                  <div className="text-center">
                    <h3 className="text-4xl font-bold text-teal-400 mb-2">87%</h3>
                    <p className="text-slate-400">Reduction in missed deadlines</p>
                  </div>
                  <div className="text-center">
                    <h3 className="text-4xl font-bold text-indigo-400 mb-2">2.5x</h3>
                    <p className="text-slate-400">Increase in deep work sessions</p>
                  </div>
                </div>
              </div>
            </motion.div>
          </motion.div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-20 bg-slate-800 relative overflow-hidden">
        <div className="absolute inset-0">
          <div className="absolute top-0 left-0 w-full h-full bg-gradient-to-b from-slate-900 to-transparent opacity-50"></div>
          <div className="absolute bottom-0 left-0 w-full h-full bg-gradient-to-t from-slate-900 to-transparent opacity-50"></div>
        </div>
        
        <div className="container mx-auto px-6 relative z-10">
          <motion.div 
            ref={ref2}
            animate={controls2}
            initial="hidden"
            variants={containerVariants}
            className="text-center mb-16"
          >
            <motion.h2 variants={itemVariants} className="text-4xl font-bold mb-4 bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-purple-600">
              Powerful Features
            </motion.h2>
            <motion.p variants={itemVariants} className="text-xl text-slate-300 max-w-3xl mx-auto">
              TimeWise offers a comprehensive suite of tools designed to revolutionize your productivity and time management.
            </motion.p>
          </motion.div>
          
          <motion.div 
            variants={containerVariants} 
            className="grid md:grid-cols-2 lg:grid-cols-3 gap-8"
          >
            {features.map((feature) => (
              <motion.div
                key={feature.id}
                variants={itemVariants}
                whileHover={{ 
                  scale: 1.03,
                  transition: { duration: 0.2 } 
                }}
                className="bg-slate-800 border border-slate-700 rounded-xl overflow-hidden hover:shadow-lg hover:shadow-blue-500/10 transition-all duration-300"
                onMouseEnter={() => setHoveredFeature(feature.id)}
                onMouseLeave={() => setHoveredFeature(null)}
              >
                <div className={`p-6 ${hoveredFeature === feature.id ? 'bg-gradient-to-r from-slate-800 to-slate-700' : ''} transition-all duration-300`}>
                  <div className={`${feature.color} w-16 h-16 rounded-full flex items-center justify-center mb-6 text-white`}>
                    {feature.icon}
                  </div>
                  <h3 className="text-xl font-semibold mb-3">{feature.title}</h3>
                  <p className="text-slate-400">{feature.description}</p>
                  <Link href={`/features/${feature.id}`} className="inline-block mt-4 text-blue-400 hover:text-blue-300 font-medium">
                    Learn more &rarr;
                  </Link>
                </div>
              </motion.div>
            ))}
          </motion.div>
        </div>
      </section>

      {/* How It Works Section */}
      <section className="py-20 bg-slate-900">
        <div className="container mx-auto px-6">
          <motion.div 
            ref={ref3}
            animate={controls3}
            initial="hidden"
            variants={containerVariants}
            className="text-center mb-16"
          >
            <motion.h2 variants={itemVariants} className="text-4xl font-bold mb-4 bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-purple-600">
              How TimeWise Works
            </motion.h2>
            <motion.p variants={itemVariants} className="text-xl text-slate-300 max-w-3xl mx-auto">
              A seamless journey from planning to achievement
            </motion.p>
          </motion.div>
          
          <motion.div variants={containerVariants} className="relative">
            {/* Timeline line */}
            <div className="absolute left-1/2 transform -translate-x-1/2 h-full w-1 bg-gradient-to-b from-blue-500 to-purple-500 rounded-full"></div>
            
            {/* Timeline steps */}
            <div className="space-y-24 relative">
              <motion.div variants={itemVariants} className="flex flex-col md:flex-row items-center">
                <div className="md:w-1/2 md:pr-12 text-right hidden md:block">
                  <h3 className="text-2xl font-semibold mb-3 text-blue-400">Create Your Account</h3>
                  <p className="text-slate-300">Set up your profile, customize your preferences, and start your productivity journey.</p>
                </div>
                <div className="relative z-10 mx-auto md:mx-0">
                  <div className="w-12 h-12 rounded-full bg-blue-500 flex items-center justify-center text-white font-bold text-xl">1</div>
                </div>
                <div className="md:w-1/2 md:pl-12 md:text-left text-center mt-4 md:mt-0 block md:hidden">
                  <h3 className="text-2xl font-semibold mb-3 text-blue-400">Create Your Account</h3>
                  <p className="text-slate-300">Set up your profile, customize your preferences, and start your productivity journey.</p>
                </div>
              </motion.div>
              
              <motion.div variants={itemVariants} className="flex flex-col md:flex-row items-center">
                <div className="md:w-1/2 md:pr-12 text-right block md:hidden">
                  <h3 className="text-2xl font-semibold mb-3 text-indigo-400">Define Your Tasks</h3>
                  <p className="text-slate-300">Create tasks with clear objectives, deadlines, and priorities to structure your workflow.</p>
                </div>
                <div className="relative z-10 mx-auto md:mx-0">
                  <div className="w-12 h-12 rounded-full bg-indigo-500 flex items-center justify-center text-white font-bold text-xl">2</div>
                </div>
                <div className="md:w-1/2 md:pl-12 text-center md:text-left mt-4 md:mt-0 hidden md:block">
                  <h3 className="text-2xl font-semibold mb-3 text-indigo-400">Define Your Tasks</h3>
                  <p className="text-slate-300">Create tasks with clear objectives, deadlines, and priorities to structure your workflow.</p>
                </div>
              </motion.div>
              
              <motion.div variants={itemVariants} className="flex flex-col md:flex-row items-center">
                <div className="md:w-1/2 md:pr-12 text-right hidden md:block">
                  <h3 className="text-2xl font-semibold mb-3 text-purple-400">Manage Work Sessions</h3>
                  <p className="text-slate-300">Create focused work sessions with timers, goals, and curated environments to enhance productivity.</p>
                </div>
                <div className="relative z-10 mx-auto md:mx-0">
                  <div className="w-12 h-12 rounded-full bg-purple-500 flex items-center justify-center text-white font-bold text-xl">3</div>
                </div>
                <div className="md:w-1/2 md:pl-12 text-center md:text-left mt-4 md:mt-0 block md:hidden">
                  <h3 className="text-2xl font-semibold mb-3 text-purple-400">Manage Work Sessions</h3>
                  <p className="text-slate-300">Create focused work sessions with timers, goals, and curated environments to enhance productivity.</p>
                </div>
              </motion.div>
              
              <motion.div variants={itemVariants} className="flex flex-col md:flex-row items-center">
                <div className="md:w-1/2 md:pr-12 text-right block md:hidden">
                  <h3 className="text-2xl font-semibold mb-3 text-teal-400">Track Progress</h3>
                  <p className="text-slate-300">Monitor your task completion, analyze performance, and identify areas for improvement.</p>
                </div>
                <div className="relative z-10 mx-auto md:mx-0">
                  <div className="w-12 h-12 rounded-full bg-teal-500 flex items-center justify-center text-white font-bold text-xl">4</div>
                </div>
                <div className="md:w-1/2 md:pl-12 text-center md:text-left mt-4 md:mt-0 hidden md:block">
                  <h3 className="text-2xl font-semibold mb-3 text-teal-400">Track Progress</h3>
                  <p className="text-slate-300">Monitor your task completion, analyze performance, and identify areas for improvement.</p>
                </div>
              </motion.div>
              
              <motion.div variants={itemVariants} className="flex flex-col md:flex-row items-center">
                <div className="md:w-1/2 md:pr-12 text-right hidden md:block">
                  <h3 className="text-2xl font-semibold mb-3 text-green-400">Collaborate & Share</h3>
                  <p className="text-slate-300">Form teams, share tasks, and work together to achieve common goals with enhanced efficiency.</p>
                </div>
                <div className="relative z-10 mx-auto md:mx-0">
                  <div className="w-12 h-12 rounded-full bg-green-500 flex items-center justify-center text-white font-bold text-xl">5</div>
                </div>
                <div className="md:w-1/2 md:pl-12 text-center md:text-left mt-4 md:mt-0 block md:hidden">
                  <h3 className="text-2xl font-semibold mb-3 text-green-400">Collaborate & Share</h3>
                  <p className="text-slate-300">Form teams, share tasks, and work together to achieve common goals with enhanced efficiency.</p>
                </div>
              </motion.div>
            </div>
          </motion.div>
        </div>
      </section>

      {/* Testimonials Section */}
      {/* <section className="py-20 bg-slate-800">
        <div className="container mx-auto px-6">
          <div className="text-center mb-16">
            <h2 className="text-4xl font-bold mb-4 bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-purple-600">
              What Our Users Say
            </h2>
            <p className="text-xl text-slate-300 max-w-3xl mx-auto">
              Join thousands of satisfied TimeWise users who have transformed their productivity
            </p>
          </div>
          
          <div className="grid md:grid-cols-3 gap-8">
            {[
              { 
                name: "Alex Johnson", 
                role: "Product Manager", 
                testimonial: "TimeWise has completely transformed how I manage my team's projects. The deep work analytics helped us identify our most productive periods and optimize our schedules accordingly.",
                avatar: "/api/placeholder/50/50"
              },
              { 
                name: "Sarah Chen", 
                role: "Freelance Designer", 
                testimonial: "As someone who juggles multiple client projects, TimeWise has been a game-changer. The session management feature helps me stay focused and track billable hours effortlessly.",
                avatar: "/api/placeholder/50/50" 
              },
              { 
                name: "Michael Rodriguez", 
                role: "Graduate Student", 
                testimonial: "The collaborative features in TimeWise helped our research team coordinate tasks and deadlines seamlessly. I've recommended it to everyone in my department.",
                avatar: "/api/placeholder/50/50" 
              }
            ].map((testimonial, index) => (
              <motion.div
                key={index}
                whileHover={{ y: -5 }}
                className="bg-slate-900 p-8 rounded-xl border border-slate-700 shadow-lg"
              >
                <div className="flex items-center mb-6">
                  <img src={testimonial.avatar} alt={testimonial.name} className="w-12 h-12 rounded-full mr-4" />
                  <div>
                    <h4 className="font-semibold text-lg">{testimonial.name}</h4>
                    <p className="text-slate-400 text-sm">{testimonial.role}</p>
                  </div>
                </div>
                <p className="text-slate-300 italic">"{testimonial.testimonial}"</p>
                <div className="mt-4 flex text-yellow-400">
                  {[...Array(5)].map((_, i) => (
                    <svg key={i} className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
                      <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"></path>
                    </svg>
                  ))}
                </div>
              </motion.div>
            ))}
          </div>
          
          <div className="text-center mt-12">
            <Link href="/testimonials" className="inline-flex items-center text-blue-400 hover:text-blue-300">
              <span>Read more testimonials</span>
              <svg className="w-5 h-5 ml-2" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M14 5l7 7m0 0l-7 7m7-7H3"></path>
              </svg>
            </Link>
          </div>
        </div>
      </section> */}

      {/* CTA Section */}
      <section className="py-20 bg-gradient-to-r from-blue-900 to-indigo-900 relative overflow-hidden">
        <div className="absolute inset-0">
          <div className="absolute top-0 right-0 w-96 h-96 bg-blue-500 rounded-full opacity-10 blur-3xl"></div>
          <div className="absolute bottom-0 left-0 w-96 h-96 bg-indigo-500 rounded-full opacity-10 blur-3xl"></div>
        </div>
        
        <div className="container mx-auto px-6 relative z-10 text-center">
          <h2 className="text-4xl md:text-5xl font-bold mb-6 text-white">Ready to Transform Your Productivity?</h2>
          <p className="text-xl text-blue-100 max-w-3xl mx-auto mb-8">
            Join thousands of individuals and teams who have revolutionized their workflow with TimeWise.
          </p>
          <div className="flex flex-wrap justify-center gap-4">
            <Link href="/register" className="px-8 py-4 bg-white text-blue-900 rounded-lg font-medium hover:bg-blue-50 transition-all duration-300 shadow-lg">
              Get Started for Free
            </Link>
            {/* <Link href="/demo" className="px-8 py-4 bg-transparent text-white border border-white rounded-lg font-medium hover:bg-white/10 transition-all duration-300">
              Schedule a Demo
            </Link> */}
          </div>
        </div>
      </section>

     {/* Footer */}
<footer className="bg-slate-900 text-slate-400 py-12">
  <div className="container mx-auto px-6">
    <div className="grid md:grid-cols-4 gap-8">
      <div>
        <h3 className="text-xl font-semibold mb-4 text-white">TimeWise</h3>
        <p className="mb-4">Master your time, elevate your productivity</p>
        <div className="flex space-x-4">
          <a href="#" className="text-slate-400 hover:text-white">
            <svg className="w-6 h-6" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path d="M22 5.16c-.69.31-1.44.52-2.23.62.8-.48 1.42-1.24 1.7-2.15-.75.44-1.58.77-2.46.94-.7-.75-1.7-1.22-2.81-1.22-2.12 0-3.84 1.72-3.84 3.84 0 .3.03.6.09.88-3.2-.16-6.03-1.69-7.93-4.02-.33.57-.52 1.24-.52 1.95 0 1.33.68 2.5 1.71 3.19-.63-.02-1.22-.19-1.74-.48v.05c0 1.86 1.32 3.41 3.08 3.76-.32.09-.66.13-1.01.13-.25 0-.49-.02-.73-.07.49 1.52 1.9 2.63 3.58 2.66-1.31 1.03-2.96 1.64-4.76 1.64-.31 0-.62-.02-.92-.06 1.7 1.09 3.71 1.72 5.87 1.72 7.04 0 10.9-5.83 10.9-10.9 0-.17 0-.33-.01-.5.75-.54 1.4-1.21 1.91-1.97z"></path>
            </svg>
          </a>
          <a href="#" className="text-slate-400 hover:text-white">
            <svg className="w-6 h-6" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path d="M22.675 0H1.325C.593 0 0 .593 0 1.325v21.351C0 23.407.593 24 1.325 24h21.35c.731 0 1.325-.593 1.325-1.325V1.325C24 .593 23.407 0 22.675 0zm-6.555 9.077h-3.197v1.704h3.197v6.275c.322.051.646.085.973.085h2.756v-6.36h2.345l.233-1.704h-2.578v-1.089c0-.488.258-.954.935-.954h1.693V4.618h-2.379c-2.608 0-3.978 1.578-3.978 4.459z"></path>
            </svg>
          </a>
          <a href="#" className="text-slate-400 hover:text-white">
            <svg className="w-6 h-6" fill="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm5.992 18.057a.19.19 0 01-.17.1h-3.659a.193.193 0 01-.17-.1.19.19 0 01-.024-.197c.5-1.178 1.323-4.478 1.323-5.248 0-.813-.669-1.128-1.386-1.128-.744 0-1.275.332-1.735.787-.144.14-.191.28-.254.427-.107.254-.177.427-.38.427-.207 0-.27-.173-.37-.427-.063-.147-.105-.287-.248-.427-.46-.455-1.002-.787-1.746-.787-.707 0-1.385.315-1.385 1.128 0 .77.832 4.07 1.323 5.248a.19.19 0 01-.195.197H6.31a.19.19 0 01-.169-.1.192.192 0 01-.025-.197c.5-1.178 1.323-4.478 1.323-5.248 0-1.405-1.262-2.320-2.785-2.32-1.496 0-2.784.908-2.784 2.32-.001.77.834 4.07 1.325 5.248a.194.194 0 01-.195.197h-1.5a.194.194 0 01-.193-.153L.006 4.438a.191.191 0 01.093-.214.196.196 0 01.244.055l1.532 2.15c.68-1.408 2.11-2.334 3.795-2.334 1.712 0 3.163.947 3.818 2.384.655-1.437 2.123-2.384 3.836-2.384 1.685 0 3.116.926 3.795 2.334l1.533-2.15a.192.192 0 01.243-.055.193.193 0 01.096.214l-1.003 13.468a.195.195 0 01-.194.153h-1.5a.194.194 0 01-.194-.153.192.192 0 01-.025-.197c.5-1.178 1.323-4.478 1.323-5.248 0-1.405-1.262-2.320-2.785-2.320-1.496 0-2.783.908-2.783 2.32 0 .77.832 4.07 1.322 5.248a.19.19 0 01-.194.197h-1.5a.19.19 0 01-.17-.1.192.192 0 01-.024-.197c.5-1.178 1.323-4.478 1.323-5.248 0-.813-.67-1.128-1.386-1.128-.717 0-1.37.315-1.37 1.128 0 .77.832 4.07 1.323 5.248a.19.19 0 01-.195.197z"></path>
            </svg>
          </a>
        </div>
      </div>
      
      <div>
        <h3 className="text-lg font-semibold mb-4 text-white">Features</h3>
        <ul className="space-y-2">
          <li><a href="#" className="hover:text-white transition-colors">Task Management</a></li>
          <li><a href="#" className="hover:text-white transition-colors">Session Management</a></li>
          <li><a href="#" className="hover:text-white transition-colors">Deep Work Analytics</a></li>
          <li><a href="#" className="hover:text-white transition-colors">Team Collaboration</a></li>
          <li><a href="#" className="hover:text-white transition-colors">TimeWise Lab</a></li>
        </ul>
      </div>
      
      <div>
        <h3 className="text-lg font-semibold mb-4 text-white">Resources</h3>
        <ul className="space-y-2">
          <li><a href="#" className="hover:text-white transition-colors">Help Center</a></li>
          <li><a href="#" className="hover:text-white transition-colors">Blog</a></li>
          <li><a href="#" className="hover:text-white transition-colors">Tutorials</a></li>
          <li><a href="#" className="hover:text-white transition-colors">API Documentation</a></li>
          <li><a href="#" className="hover:text-white transition-colors">Community Forum</a></li>
        </ul>
      </div>
      
      <div>
        <h3 className="text-lg font-semibold mb-4 text-white">Company</h3>
        <ul className="space-y-2">
          <li><a href="#" className="hover:text-white transition-colors">About Us</a></li>
          <li><a href="#" className="hover:text-white transition-colors">Careers</a></li>
          <li><a href="#" className="hover:text-white transition-colors">Contact</a></li>
          <li><a href="#" className="hover:text-white transition-colors">Privacy Policy</a></li>
          <li><a href="#" className="hover:text-white transition-colors">Terms of Service</a></li>
        </ul>
      </div>
    </div>
    
    <div className="border-t border-slate-800 mt-10 pt-6 flex flex-col md:flex-row justify-between items-center">
      <p>&copy; 2025 TimeWise. All rights reserved.</p>
      <div className="mt-4 md:mt-0">
        <a href="#" className="text-blue-400 hover:text-blue-300">Subscribe to our newsletter</a>
      </div>
    </div>
  </div>
</footer>
</div>

  );
}
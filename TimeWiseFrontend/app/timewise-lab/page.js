"use client"
// pages/timewise-lab.js
import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import Link from 'next/link';
import {
  Lightbulb, Sparkles, Bot, Zap, Brain, Wand2, 
  MessageSquare, Calendar, BarChart3, Route, FileText,
  Code, ScrollText, Radar, ArrowRight
} from 'lucide-react';

export default function TimeWiseLabPage() {
  const [activeFeature, setActiveFeature] = useState('roadmap');
  const [isLoading, setIsLoading] = useState(false);
  const [aiResponse, setAiResponse] = useState(null);

  // Simulate AI loading effect
  const simulateAiResponse = () => {
    setIsLoading(true);
    setTimeout(() => {
      setIsLoading(false);
      setAiResponse(true);
    }, 1500);
  };

  // Reset AI response when changing features
  useEffect(() => {
    setAiResponse(null);
  }, [activeFeature]);

  // Animation variants
  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1
      }
    }
  };

  const itemVariants = {
    hidden: { y: 20, opacity: 0 },
    visible: {
      y: 0,
      opacity: 1,
      transition: { duration: 0.5, ease: "easeOut" }
    }
  };

  // AI features data
  const aiFeatures = [
    {
      id: 'roadmap',
      title: 'AI Roadmap Generator',
      description: 'Generate detailed task roadmaps for your projects with intelligent milestone planning.',
      icon: <Route className="h-6 w-6" />,
      color: 'bg-gradient-to-r from-blue-500 to-indigo-600',
      prompt: "Generate a roadmap for learning Spanish in 3 months",
      output: {
        title: "Spanish Language Learning Roadmap",
        tasks: [
          { name: "Master Basic Greetings & Phrases", status: "Week 1", priority: "High" },
          { name: "Learn Present Tense Conjugation", status: "Week 2-3", priority: "High" },
          { name: "Build 500-Word Vocabulary", status: "Week 3-6", priority: "Medium" },
          { name: "Practice Basic Conversations", status: "Week 4-8", priority: "High" },
          { name: "Study Past Tense Structures", status: "Week 6-9", priority: "Medium" },
          { name: "Consume Spanish Media Daily", status: "Ongoing", priority: "Medium" },
          { name: "Complete Grammar Exercises", status: "Weekly", priority: "Medium" },
          { name: "Hold 10-Minute Conversations", status: "Week 10-12", priority: "High" }
        ]
      }
    },
    {
      id: 'autocomplete',
      title: 'Smart Message Autocomplete',
      description: 'Draft professional messages and feedback with AI-powered content suggestions.',
      icon: <MessageSquare className="h-6 w-6" />,
      color: 'bg-gradient-to-r from-green-500 to-emerald-600',
      prompt: "Draft a message to postpone tomorrow's team meeting",
      output: {
        subject: "Rescheduling Tomorrow's Team Meeting",
        message: "Hi team,\n\nI need to reschedule our meeting originally planned for tomorrow due to an unexpected conflict. I propose we move it to Thursday at the same time (2 PM).\n\nPlease let me know if this works for everyone. If not, please suggest alternative times that would work for you.\n\nThe agenda remains the same, and all materials are already shared in our project folder.\n\nThanks for your understanding,\n[Your Name]"
      }
    },
    {
      id: 'session',
      title: 'Session Outline Creator',
      description: 'Get intelligent suggestions for structuring your work sessions based on task priorities.',
      icon: <Calendar className="h-6 w-6" />,
      color: 'bg-gradient-to-r from-purple-500 to-violet-600',
      prompt: "Create a 2-hour deep work session outline for my website project",
      output: {
        title: "Website Development Deep Work Session",
        goals: ["Complete homepage layout", "Fix responsive design issues", "Optimize image loading"],
        schedule: [
          { time: "0:00-0:15", activity: "Review current project status & set specific targets" },
          { time: "0:15-0:55", activity: "Focused coding: Homepage layout completion" },
          { time: "0:55-1:05", activity: "Short break & progress check" },
          { time: "1:05-1:45", activity: "Focused work: Responsive design fixes & image optimization" },
          { time: "1:45-2:00", activity: "Session review & plan next steps" }
        ],
        tips: "Minimize distractions by silencing notifications. Have your design requirements document ready before starting."
      }
    },
    {
      id: 'analysis',
      title: 'Content Analysis Engine',
      description: 'Extract insights from messages, feedback, and productivity data with AI-powered analysis.',
      icon: <FileText className="h-6 w-6" />,
      color: 'bg-gradient-to-r from-orange-500 to-amber-600',
      prompt: "Analyze my productivity stats for patterns and improvement areas",
      output: {
        summary: "Your productivity shows strong morning performance but significant afternoon decline. Peak focus hours: 8-11 AM.",
        insights: [
          "Task completion rate is 37% higher before noon compared to afternoon hours",
          "Meeting-heavy days (Wed/Thu) show 42% lower deep work metrics",
          "Tasks categorized as 'Creative' have the longest completion times",
          "Short breaks (5-10 min) correlate with improved focus in subsequent work blocks"
        ],
        recommendations: [
          "Schedule critical tasks in your 8-11 AM peak performance window",
          "Implement active break strategy in afternoons to combat productivity decline",
          "Consider moving meetings to after 2 PM to preserve morning focus time",
          "Break down 'Creative' category tasks into smaller, more manageable chunks"
        ]
      }
    },
  ];

  // Function to generate demo content for features
  const getDemoContent = () => {
    const feature = aiFeatures.find(f => f.id === activeFeature);
    if (!feature) return null;

    switch (activeFeature) {
      case 'roadmap':
        return (
          <div className="bg-slate-800 rounded-lg p-6 border border-slate-700">
            <h3 className="text-xl font-semibold mb-4">{feature.output.title}</h3>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-slate-700">
                    <th className="text-left py-2 px-4">Task</th>
                    <th className="text-left py-2 px-4">Timeline</th>
                    <th className="text-left py-2 px-4">Priority</th>
                  </tr>
                </thead>
                <tbody>
                  {feature.output.tasks.map((task, index) => (
                    <tr key={index} className="border-b border-slate-700">
                      <td className="py-3 px-4">{task.name}</td>
                      <td className="py-3 px-4">{task.status}</td>
                      <td className="py-3 px-4">
                        <span className={`px-2 py-1 rounded text-xs font-medium ${
                          task.priority === 'High' ? 'bg-red-500/20 text-red-400' : 
                          task.priority === 'Medium' ? 'bg-yellow-500/20 text-yellow-400' :
                          'bg-blue-500/20 text-blue-400'
                        }`}>
                          {task.priority}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <div className="flex justify-end mt-4 space-x-2">
              <button className="bg-slate-700 py-2 px-4 rounded hover:bg-slate-600 transition-colors">
                Edit
              </button>
              <button className="bg-blue-600 py-2 px-4 rounded hover:bg-blue-700 transition-colors">
                Save Roadmap
              </button>
            </div>
          </div>
        );
      
      case 'autocomplete':
        return (
          <div className="bg-slate-800 rounded-lg p-6 border border-slate-700">
            <div className="mb-4">
              <label className="block text-sm font-medium mb-1">Subject</label>
              <input 
                type="text" 
                className="w-full bg-slate-700 border border-slate-600 rounded p-2"
                value={feature.output.subject} 
              />
            </div>
            <div className="mb-4">
              <label className="block text-sm font-medium mb-1">Message</label>
              <textarea 
                className="w-full bg-slate-700 border border-slate-600 rounded p-2 h-40"
                value={feature.output.message}
              ></textarea>
            </div>
            <div className="flex justify-end space-x-2">
              <button className="bg-slate-700 py-2 px-4 rounded hover:bg-slate-600 transition-colors">
                Regenerate
              </button>
              <button className="bg-green-600 py-2 px-4 rounded hover:bg-green-700 transition-colors">
                Use Message
              </button>
            </div>
          </div>
        );
      
      case 'session':
        return (
          <div className="bg-slate-800 rounded-lg p-6 border border-slate-700">
            <h3 className="text-xl font-semibold mb-4">{feature.output.title}</h3>
            
            <div className="mb-6">
              <h4 className="text-md font-medium mb-2 text-purple-400">Session Goals</h4>
              <ul className="list-disc pl-5 space-y-1">
                {feature.output.goals.map((goal, index) => (
                  <li key={index}>{goal}</li>
                ))}
              </ul>
            </div>
            
            <div className="mb-6">
              <h4 className="text-md font-medium mb-2 text-purple-400">Timeline</h4>
              <div className="space-y-3">
                {feature.output.schedule.map((item, index) => (
                  <div key={index} className="flex border-l-2 border-purple-500 pl-4">
                    <div className="w-24 font-mono text-sm pt-1">{item.time}</div>
                    <div>{item.activity}</div>
                  </div>
                ))}
              </div>
            </div>
            
            <div className="mb-4 bg-slate-700 p-4 rounded-md">
              <h4 className="text-md font-medium mb-2 flex items-center text-yellow-400">
                <Lightbulb className="h-4 w-4 mr-2" />
                Pro Tips
              </h4>
              <p className="text-slate-300 text-sm">{feature.output.tips}</p>
            </div>
            
            <div className="flex justify-end space-x-2">
              <button className="bg-slate-700 py-2 px-4 rounded hover:bg-slate-600 transition-colors">
                Customize
              </button>
              <button className="bg-purple-600 py-2 px-4 rounded hover:bg-purple-700 transition-colors">
                Start Session
              </button>
            </div>
          </div>
        );
      
      case 'analysis':
        return (
          <div className="bg-slate-800 rounded-lg p-6 border border-slate-700">
            <div className="mb-6">
              <h3 className="text-xl font-semibold mb-2">Productivity Analysis</h3>
              <p className="text-slate-400">{feature.output.summary}</p>
            </div>
            
            <div className="grid md:grid-cols-2 gap-6 mb-6">
              <div>
                <h4 className="text-md font-medium mb-3 text-orange-400 flex items-center">
                  <Zap className="h-4 w-4 mr-2" />
                  Key Insights
                </h4>
                <ul className="space-y-2">
                  {feature.output.insights.map((insight, index) => (
                    <li key={index} className="flex items-start">
                      <span className="bg-orange-500/20 text-orange-400 rounded-full w-5 h-5 flex items-center justify-center text-xs mr-2 mt-0.5">
                        {index + 1}
                      </span>
                      <span>{insight}</span>
                    </li>
                  ))}
                </ul>
              </div>
              
              <div>
                <h4 className="text-md font-medium mb-3 text-blue-400 flex items-center">
                  <Sparkles className="h-4 w-4 mr-2" />
                  Recommendations
                </h4>
                <ul className="space-y-2">
                  {feature.output.recommendations.map((rec, index) => (
                    <li key={index} className="flex items-start">
                      <span className="bg-blue-500/20 text-blue-400 rounded-full w-5 h-5 flex items-center justify-center text-xs mr-2 mt-0.5">
                        {index + 1}
                      </span>
                      <span>{rec}</span>
                    </li>
                  ))}
                </ul>
              </div>
            </div>
            
            <div className="flex justify-end space-x-2">
              <button className="bg-slate-700 py-2 px-4 rounded hover:bg-slate-600 transition-colors">
                Full Report
              </button>
              <button className="bg-orange-600 py-2 px-4 rounded hover:bg-orange-700 transition-colors">
                Apply Recommendations
              </button>
            </div>
          </div>
        );
      
      default:
        return null;
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-slate-900 via-slate-800 to-slate-900 text-white">
      {/* Hero Section */}
      <div className="relative pt-24 pb-16 overflow-hidden">
        <div className="absolute inset-0 z-0">
          <div className="absolute top-20 left-20 w-64 h-64 rounded-full bg-blue-500 opacity-10 blur-3xl"></div>
          <div className="absolute bottom-20 right-20 w-96 h-96 rounded-full bg-indigo-500 opacity-10 blur-3xl"></div>
          <div className="absolute top-1/2 left-1/3 w-80 h-80 rounded-full bg-purple-500 opacity-10 blur-3xl"></div>
        </div>
        
        <div className="container mx-auto px-6 relative z-10">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8 }}
            className="max-w-4xl mx-auto text-center"
          >
            <div className="flex items-center justify-center mb-6">
              <div className="relative">
                <Wand2 className="h-12 w-12 text-blue-400" />
                <motion.div 
                  className="absolute inset-0"
                  animate={{ 
                    boxShadow: ['0 0 0 0 rgba(96, 165, 250, 0)', '0 0 0 10px rgba(96, 165, 250, 0)'],
                  }}
                  transition={{ 
                    duration: 2, 
                    repeat: Infinity,
                    repeatType: "loop"
                  }}
                />
              </div>
            </div>
            
            <h1 className="text-5xl md:text-6xl font-bold mb-6 bg-clip-text text-transparent bg-gradient-to-r from-blue-400 via-purple-400 to-indigo-400">
              Introducing TimeWise Lab
            </h1>
            
            <p className="text-xl md:text-2xl text-slate-300 mb-8">
              Our latest extension to TimeWise, enhancing productivity through AI in dynamic and vibrant ways
            </p>
            
            <div className="inline-block relative">
              <div className="absolute -inset-1 bg-gradient-to-r from-blue-600 via-purple-600 to-indigo-600 rounded-lg blur"></div>
              <button 
                className="relative bg-slate-900 hover:bg-slate-800 text-white px-8 py-4 rounded-lg font-medium transition-all duration-300 flex items-center"
                onClick={() => document.getElementById('features').scrollIntoView({ behavior: 'smooth' })}
              >
                <span>Explore AI Features</span>
                <ArrowRight className="ml-2 h-5 w-5" />
              </button>
            </div>
          </motion.div>
        </div>
      </div>

      {/* Key Benefits */}
      <section className="py-16 bg-slate-800/50">
        <div className="container mx-auto px-6">
          <motion.div 
            initial="hidden"
            whileInView="visible"
            viewport={{ once: true, margin: "-100px" }}
            variants={containerVariants}
            className="grid md:grid-cols-3 gap-8"
          >
            <motion.div variants={itemVariants} className="bg-gradient-to-b from-slate-800 to-slate-900 p-8 rounded-xl border border-slate-700">
              <div className="bg-blue-500/20 text-blue-400 w-16 h-16 rounded-xl flex items-center justify-center mb-6">
                <Brain className="h-8 w-8" />
              </div>
              <h3 className="text-xl font-semibold mb-3">Intelligent Automation</h3>
              <p className="text-slate-400">AI-powered tools that handle repetitive tasks and generate valuable content, freeing up your time for deep work.</p>
            </motion.div>

            <motion.div variants={itemVariants} className="bg-gradient-to-b from-slate-800 to-slate-900 p-8 rounded-xl border border-slate-700">
              <div className="bg-purple-500/20 text-purple-400 w-16 h-16 rounded-xl flex items-center justify-center mb-6">
                <Sparkles className="h-8 w-8" />
              </div>
              <h3 className="text-xl font-semibold mb-3">Personalized Insights</h3>
              <p className="text-slate-400">Custom analysis that adapts to your work patterns and delivers actionable recommendations for productivity improvement.</p>
            </motion.div>

            <motion.div variants={itemVariants} className="bg-gradient-to-b from-slate-800 to-slate-900 p-8 rounded-xl border border-slate-700">
              <div className="bg-green-500/20 text-green-400 w-16 h-16 rounded-xl flex items-center justify-center mb-6">
                <Zap className="h-8 w-8" />
              </div>
              <h3 className="text-xl font-semibold mb-3">Seamless Integration</h3>
              <p className="text-slate-400">AI features work harmoniously with existing TimeWise tools, enhancing your workflow without disrupting it.</p>
            </motion.div>
          </motion.div>
        </div>
      </section>

            {/* Features Section */}
            <section id="features" className="py-16">
        <div className="container mx-auto px-6">
          <motion.div 
            initial="hidden"
            whileInView="visible"
            viewport={{ once: true, margin: "-50px" }}
            variants={containerVariants}
            className="max-w-4xl mx-auto"
          >
            <motion.div variants={itemVariants} className="text-center mb-12">
              <h2 className="text-3xl md:text-4xl font-bold mb-4 bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-indigo-500">
                AI-Powered Productivity Tools
              </h2>
              <p className="text-slate-400 text-lg">
                TimeWise Lab leverages AI to automate manual tasks, provide insights, and generate content to enhance productivity in task management and related activities.
              </p>
            </motion.div>

            {/* Feature Navigation */}
            <motion.div variants={itemVariants} className="mb-8">
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                {aiFeatures.map((feature) => (
                  <button
                    key={feature.id}
                    onClick={() => setActiveFeature(feature.id)}
                    className={`flex flex-col items-center justify-center p-4 rounded-lg transition-all duration-300 ${
                      activeFeature === feature.id
                        ? `${feature.color} text-white`
                        : 'bg-slate-800 hover:bg-slate-700 text-slate-400'
                    }`}
                  >
                    <div className="mb-2">{feature.icon}</div>
                    <span className="text-sm font-medium">{feature.title}</span>
                  </button>
                ))}
              </div>
            </motion.div>

            {/* Feature Demo Area */}
            <motion.div variants={itemVariants} className="mt-8">
              <div className="bg-slate-900 rounded-xl p-6">
                <div className="flex items-center justify-between mb-6">
                  <h3 className="text-xl font-semibold">
                    {aiFeatures.find((f) => f.id === activeFeature)?.title}
                  </h3>
                  <button
                    onClick={simulateAiResponse}
                    className="flex items-center bg-slate-800 hover:bg-slate-700 text-slate-400 px-4 py-2 rounded-lg transition-colors"
                  >
                    {isLoading ? (
                      <>
                        <span className="animate-spin mr-2">🌀</span>
                        Generating...
                      </>
                    ) : (
                      <>
                        <Sparkles className="h-4 w-4 mr-2" />
                        Generate Demo
                      </>
                    )}
                  </button>
                </div>

                {/* Demo Content */}
                {aiResponse && (
                  <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.5 }}
                  >
                    {getDemoContent()}
                  </motion.div>
                )}
              </div>
            </motion.div>
          </motion.div>
        </div>
      </section>

      {/* Call to Action */}
      <section className="py-16 bg-gradient-to-b from-slate-900 to-slate-800">
        <div className="container mx-auto px-6 text-center">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8 }}
            viewport={{ once: true, margin: "-100px" }}
          >
            <h2 className="text-3xl md:text-4xl font-bold mb-6 bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-indigo-500">
              Ready to Supercharge Your Productivity?
            </h2>
            <p className="text-slate-400 text-lg mb-8">
              Join TimeWise Lab today and experience the future of task management powered by AI.
            </p>
            <Link href="/register" className="inline-block bg-blue-600 hover:bg-blue-700 text-white px-8 py-4 rounded-lg font-medium transition-colors duration-300">
  Get Started Now
</Link>
          </motion.div>
        </div>
      </section>
    </div>
  );
}
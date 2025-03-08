"use client"
import React, { useState, useEffect } from "react";
import { motion, AnimatePresence } from "framer-motion";
import Link from "next/link";
import Image from "next/image";
import { useTheme } from "next-themes";
import {
  NavigationMenu,
  NavigationMenuContent,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
  NavigationMenuTrigger,
} from "@/components/ui/navigation-menu";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import { Separator } from "@/components/ui/separator";
import { Badge } from "@/components/ui/badge";
import { ScrollArea } from "@/components/ui/scroll-area";
import { ModeToggle } from "@/components/ModeToggle";
import {
  Moon,
  Sun,
  Menu,
  X,
  User,
  Mail,
  Info,
  Phone,
  Clock,
  CheckCircle,
  BarChart,
  Users,
  FileText,
  Bell,
  Calendar,
  MessageSquare,
  Route,
  Target,
  Activity,
} from "lucide-react";

export default function WelcomePage() {
  const [mounted, setMounted] = useState(false);
  const { theme, setTheme } = useTheme();
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [activeFeature, setActiveFeature] = useState(null);

  useEffect(() => {
    setMounted(true);
  }, []);

  const features = [
    {
      title: "AI-Powered Task & Roadmap Generator",
      description: "Automate task creation and roadmap planning with AI-driven insights and suggestions.",
      icon: Route,
      details: [
        "Set goals and let AI generate detailed task roadmaps",
        "AI suggests task dependencies, priorities, and deadlines",
        "Customize parameters like task count and milestones",
        "Modify and save AI-generated roadmaps for future use",
        "Collaborate on tasks with team members and track progress",
      ],
    },
    {
      title: "Smart Communication & Feedback Assistant",
      description: "Draft professional messages and feedback with AI-powered content generation.",
      icon: MessageSquare,
      details: [
        "AI generates concise and insightful messages and feedback",
        "Summarize feedback with AI-driven analysis and scoring",
        "Revise and customize AI-generated content before sending",
        "Email notifications for messages and feedback",
        "AI-powered inbox analysis for quick insights",
      ],
    },
    {
      title: "AI-Driven Session & Productivity Manager",
      description: "Optimize your work sessions with AI-generated outlines and deep work analytics.",
      icon: Clock,
      details: [
        "AI creates session goals and task outlines based on priorities",
        "Track session progress with a timer and AI-driven insights",
        "Evaluate session efficiency with AI-generated productivity scores",
        "Resume sessions from where you left off",
        "Analyze deep work patterns with time-based productivity metrics",
      ],
    },
    {
      title: "Advanced Progress & Performance Tracker",
      description: "Monitor your productivity with AI-driven reports and actionable insights.",
      icon: BarChart,
      details: [
        "AI generates daily progress reports for unfinished tasks",
        "Weekly performance reports with statistical data and trends",
        "Identify productivity patterns and improvement areas",
        "Historical progress tracking for better goal setting",
        "Customizable time periods for detailed analysis",
      ],
    },
    {
      title: "Collaborative Team & Task Management",
      description: "Work seamlessly with teams using AI-powered task sharing and collaboration tools.",
      icon: Users,
      details: [
        "AI suggests team members based on task compatibility",
        "Share tasks and collaborate with real-time updates",
        "Team chat for communication and feedback",
        "AI tracks team progress and performance metrics",
        "Create and manage teams with role-based access control",
      ],
    },
    {
      title: "Personalized Insights & Analytics Dashboard",
      description: "Visualize your productivity and performance with AI-driven statistics and insights.",
      icon: Target,
      details: [
        "AI generates bar graphs, pie charts, and trend forecasts",
        "Filter statistics by time period for personalized insights",
        "AI identifies performance trends and improvement areas",
        "Multiple visualization options for better understanding",
        "Deep work analytics for optimizing productivity",
      ],
    },
  ];
  const mainContentVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: {
      opacity: 1,
      y: 0,
      transition: {
        duration: 0.6,
        staggerChildren: 0.2,
      },
    },
  };

  const featureCardVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: {
      opacity: 1,
      y: 0,
      transition: { duration: 0.5 },
    },
    hover: {
      scale: 1.02,
      transition: { duration: 0.2 },
    },
  };
  const handleFeatureClick = (id) => {
    setIsMenuOpen(false); // Close the menu
    const element = document.getElementById(id);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  };

  if (!mounted) return null;

  return (
    <div className={`min-h-screen ${theme === 'dark' ? 'bg-gray-950' : 'bg-gray-50'}`}>
      {/* Header */}
      <header className="sticky top-0 z-50 backdrop-blur-lg bg-background/80 border-b">
        <div className="container mx-auto px-4">
          <div className="flex items-center justify-between h-16">
            <div className="flex items-center space-x-4">
              <Image
                src="/images/timewise_logo.png"
                alt="TimeWise Logo"
                width={40}
                height={40}
                className="rounded-lg"
              />
              <span className="text-2xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-purple-600">
                TimeWise
              </span>
            </div>

            {/* Desktop Navigation */}
            <div className="hidden md:flex items-center space-x-4">
              

              <Button variant="ghost" size="sm" asChild>
                <Link href="/about">About</Link>
              </Button>
              <Button variant="ghost" size="sm" asChild>
                <Link href="/contact">Contact</Link>
              </Button>
              <Button variant="default" size="sm" asChild>
                <Link href="/login">Login</Link>
              </Button>
              <Button variant="outline" size="sm" asChild>
                <Link href="/register">Register</Link>
              </Button>
               {/* Theme Toggle */}
                        <ModeToggle />
              
            </div>

            {/* Mobile Menu Button */}
            <Sheet open={isMenuOpen} onOpenChange={setIsMenuOpen}>
              <SheetTrigger asChild className="md:hidden">
                <Button variant="ghost" size="icon">
                  <Menu className="h-6 w-6" />
                </Button>
              </SheetTrigger>
              <SheetContent>
                <SheetHeader>
                  <SheetTitle>Menu</SheetTitle>
                  <SheetDescription>
                    Navigate through TimeWise
                  </SheetDescription>
                </SheetHeader>
                <ScrollArea className="h-[calc(100vh-8rem)] py-4">
                  <div className="flex flex-col space-y-2">
                  {features.map((feature, index) => (
                  <Button
                    key={feature.title}
                    variant="ghost"
                    className="justify-start"
                    onClick={() => handleFeatureClick(`feature-${index}`)} // Add onClick
                  >
                    <feature.icon className="mr-2 h-4 w-4" />
                    {feature.title}
                  </Button>
                ))}
                    <Separator className="my-2" />
                    <Button variant="ghost" asChild>
                      <Link href="/login">Login</Link>
                    </Button>
                    <Button variant="ghost" asChild>
                      <Link href="/register">Register</Link>
                    </Button>
                    <Button variant="ghost" asChild>
                      <Link href="/about">About</Link>
                    </Button>
                    <Button variant="ghost" asChild>
                      <Link href="/contact">Contact</Link>
                    </Button>
                  </div>
                </ScrollArea>
              </SheetContent>
            </Sheet>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="container mx-auto px-4 py-8">
        <motion.div
          variants={mainContentVariants}
          initial="hidden"
          animate="visible"
          className="space-y-12"
        >
          {/* Hero Section */}
          <section className="text-center space-y-6 py-6">
            <motion.h1
              className="text-4xl md:text-6xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-blue-400 to-purple-600"
              initial={{ opacity: 0, y: -20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6 }}
            >
              Welcome to TimeWise
            </motion.h1>
            <motion.p
              className="text-lg md:text-xl text-muted-foreground max-w-2xl mx-auto"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ delay: 0.2 }}
            >
             TimeWise empowers you to manage tasks effortlessly with AI-driven roadmaps, smart session planning, deep work analytics, team collaboration tools, and real-time progress tracking—all designed to boost productivity and streamline your workflow.
            </motion.p>
            <motion.div
              className="flex justify-center space-x-4"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ delay: 0.4 }}
            >
              <Button size="lg" asChild>
                <Link href="/register">Get Started</Link>
              </Button>
              <Button size="lg" variant="outline" asChild>
                <Link href="/about">Learn More</Link>
              </Button>
            </motion.div>
          </section>

          {/* Features Grid */}
          <section className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
  <AnimatePresence>
    {features.map((feature, index) => (
      <motion.div
        key={feature.title}
        variants={featureCardVariants}
        id={`feature-${index}`}
        initial="hidden"
        animate="visible"
        whileHover="hover"
        layout
      >
        <Card className="h-full">
          <CardHeader>
            <div className="flex items-center space-x-2">
              <feature.icon className="h-6 w-6 text-primary" />
              <CardTitle>{feature.title}</CardTitle>
            </div>
            <CardDescription>{feature.description}</CardDescription>
          </CardHeader>
          <CardContent>
            <ul className="space-y-2">
              {feature.details.map((detail, idx) => (
                <li key={idx} className="flex items-center space-x-2">
                  <CheckCircle className="h-4 w-4 text-primary" />
                  <span>{detail}</span>
                </li>
              ))}
            </ul>
          </CardContent>
        </Card>
      </motion.div>
    ))}
  </AnimatePresence>
</section>
        </motion.div>
      </main>
    </div>
  );
}
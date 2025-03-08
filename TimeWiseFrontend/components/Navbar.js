"use client";

import * as React from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import Image from "next/image";
import { useTheme } from "next-themes";
import { Menu, Search, Bell, MessageSquare, Settings,BarChart3, User } from "lucide-react";
import {
  NavigationMenu,
  NavigationMenuContent,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
  NavigationMenuTrigger,
  navigationMenuTriggerStyle,
} from "@/components/ui/navigation-menu";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { ModeToggle } from "@/components/ModeToggle";
import { Bar } from "recharts";

const mainNavItems = [
  {
    title: "TimeWise Labs",
    href: "/timewise-lab",
  },
  {
    title: "About",
    href: "/about",
  },
  {
    title: "Contact",
    href: "/contact",
  },
  {
    title: "Search",
    href: "/home/search",
  },
 
];


const mainNavItemsForMobile = [
  {
    title: "TimeWise Labs",
    href: "/timewise-lab",
  },
  {
    title: "Search User",
    href: "/home/search",
  },
  {
    title: "Create Task",
    href: "/home/task/create",
  },
  {
    title: "Generate Roadmap",
    href: "/home/roadmap/generate",
  },
  {
    title: "My Progress",
    href: "/home/progress-report",
  },{
    title: "Performance Report",
    href: "/home/performance-report",
  },
  {
    title: "Create Session",
    href: "/home/session",
  },
  {
    title: "My Teams",
    href: "/home/team",
  },
  {
    title: "Feedback",
    href: "/home/feedback",
  },
  {
    title: "Deep Work Analytics",
    href: "/home/deep-work-analytics",
  },
  {
    title: "Notification",
    href: "/home/notification",
  },
  {
    title: "Message",
    href: "/home/message",
  },
  {
    title: "About",
    href: "/about",
  },
  {
    title: "Contact",
    href: "/contact",
  }
 
];

const quickActions = [
  { icon: Bell, label: "Notifications", href: "/home/notification" },
  { icon: MessageSquare, label: "Messages", href: "/home/message" },
  { icon: BarChart3, label: "Statistics", href: "/home/statistics/account" },
];

export function Navbar() {
  const [isSearchOpen, setIsSearchOpen] = React.useState(false);
  const router = useRouter();
  return (
    <header className="sticky top-0 z-5 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/120">
      <div className="container flex items-center px-4">
        <div className="flex items-center space-x-4">
          {/* Mobile Menu */}
          <Sheet>
            <SheetTrigger asChild>
              <Button variant="ghost" size="icon" className="md:hidden">
                <Menu className="h-5 w-5" />
              </Button>
            </SheetTrigger>
            <SheetContent side="left" className="w-[300px] sm:w-[400px]">
              <SheetHeader className="pb-2"> {/* Reduced padding bottom */}
                <SheetTitle>Menu</SheetTitle>
                <SheetDescription>
                  Navigate through your workspace
                </SheetDescription>
              </SheetHeader>
              <div className="flex flex-col space-y-2 mt-2 overflow-y-auto max-h-[80vh]"> {/* Reduced space-y and mt */}
                {mainNavItemsForMobile.map((item) => (
                  <Link
                    key={item.href}
                    href={item.href}
                    className="flex items-center p-1 hover:bg-accent rounded-lg"
                  >
                    {item.title}
                  </Link>
                ))}
              </div>
            </SheetContent>
          </Sheet>
  
          {/* Logo */}
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
          <NavigationMenu className="hidden md:flex">
            <NavigationMenuList>
              {mainNavItems.map((item) => (
                <NavigationMenuItem key={item.href}>
                  <Link href={item.href} legacyBehavior passHref>
                    <NavigationMenuLink className={navigationMenuTriggerStyle()}>
                      {item.title}
                    </NavigationMenuLink>
                  </Link>
                </NavigationMenuItem>
              ))}
            </NavigationMenuList>
          </NavigationMenu>
        </div>
  
        {/* Right side items */}
        <div className="flex items-center ml-auto space-x-4">
          {/* Quick Actions */}
          <div className="flex items-center space-x-2">
            {quickActions.map(({ icon: Icon, label, href }) => (
              <Button
                key={href}
                variant="ghost"
                size="icon"
                className="hidden sm:inline-flex"
                onClick={() => router.push(href)}
              >
                <Icon className="h-5 w-5" />
                <span className="sr-only">{label}</span>
              </Button>
            ))}
          </div>
  
          {/* Theme Toggle */}
          <ModeToggle />
  
          {/* User Menu */}
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" size="icon">
                <User className="h-5 w-5" />
                <span className="sr-only">User menu</span>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuLabel>My Account</DropdownMenuLabel>
              <DropdownMenuSeparator />
              <DropdownMenuItem onClick={() => {
                localStorage.removeItem('UserAccountName');
                window.location.href = '/home/account';
              }}>Profile</DropdownMenuItem>
              <DropdownMenuItem onClick={() => {
                localStorage.removeItem('timewise-auth-token');
                localStorage.removeItem('TimeWiseTask');
                localStorage.removeItem('TimeWiseTeam');
                localStorage.removeItem('TimeWiseUserData');
                localStorage.removeItem('TimeWiseCurrentSession');
                window.location.href = '/welcome';
              }}>Log out</DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </div>
    </header>
  );
}

export default Navbar;
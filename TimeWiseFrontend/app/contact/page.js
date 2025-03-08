"use client";
import React from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import {
  Mail,
  MapPin,
  MessageCircle,
  Send,
  Facebook,
  Twitter,
  Linkedin,
  Github,
  Instagram,
  Globe,
} from "lucide-react";
import { useToast } from "@/components/ui/use-toast";

export default function ContactUsPage() {
  const { toast } = useToast();

  const handleSubmit = (e) => {
    e.preventDefault();
    toast({
      title: "Thank you!",
      description: "Your feedback has been submitted successfully.",
    });
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-gray-50 to-gray-100 dark:from-gray-900 dark:to-gray-800 py-12">
      <div className="container mx-auto px-4">
        {/* Page Title */}
        <h1 className="text-4xl md:text-5xl font-bold text-center bg-clip-text text-transparent bg-gradient-to-r from-blue-600 to-purple-600 mb-8">
          Contact Us
        </h1>

        {/* Contact Information */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 mb-12">
          {/* Email Section */}
          <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-lg">
            <div className="flex items-center space-x-4 mb-4">
              <Mail className="h-8 w-8 text-blue-600" />
              <div>
                <h2 className="text-xl font-semibold">Email Us</h2>
                <p className="text-gray-600 dark:text-gray-400">
                  Reach out to us via email for any inquiries.
                </p>
              </div>
            </div>
            <a
              href="mailto:timeWise.webapp@gmail.com"
              className="text-blue-600 hover:underline"
            >
              timeWise.webapp@gmail.com
            </a>
          </div>

          {/* Location Section */}
          <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-lg">
            <div className="flex items-center space-x-4 mb-4">
              <MapPin className="h-8 w-8 text-purple-600" />
              <div>
                <h2 className="text-xl font-semibold">Our Location</h2>
                <p className="text-gray-600 dark:text-gray-400">
                  Find us on the map (link not working correctly).
                </p>
              </div>
            </div>
            <Button
              variant="outline"
              className="text-purple-600 hover:text-purple-700"
              disabled
            >
              <MapPin className="mr-2 h-4 w-4" />
              View on Map
            </Button>
          </div>

          {/* Social Media Links */}
          <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-lg">
            <div className="flex items-center space-x-4 mb-4">
              <Globe className="h-8 w-8 text-green-600" />
              <div>
                <h2 className="text-xl font-semibold">Follow Us</h2>
                <p className="text-gray-600 dark:text-gray-400">
                  Connect with us on social media.
                </p>
              </div>
            </div>
            <div className="flex space-x-4">
              <a
                href="https://facebook.com/timewise"
                target="_blank"
                rel="noopener noreferrer"
              >
                <Button variant="outline" size="icon">
                  <Facebook className="h-4 w-4 text-blue-600" />
                </Button>
              </a>
              <a
                href="https://twitter.com/timewise"
                target="_blank"
                rel="noopener noreferrer"
              >
                <Button variant="outline" size="icon">
                  <Twitter className="h-4 w-4 text-blue-400" />
                </Button>
              </a>
              <a
                href="https://linkedin.com/company/timewise"
                target="_blank"
                rel="noopener noreferrer"
              >
                <Button variant="outline" size="icon">
                  <Linkedin className="h-4 w-4 text-blue-700" />
                </Button>
              </a>
              <a
                href="https://github.com/timewise"
                target="_blank"
                rel="noopener noreferrer"
              >
                <Button variant="outline" size="icon">
                  <Github className="h-4 w-4 text-gray-800 dark:text-gray-200" />
                </Button>
              </a>
              <a
                href="https://instagram.com/timewise"
                target="_blank"
                rel="noopener noreferrer"
              >
                <Button variant="outline" size="icon">
                  <Instagram className="h-4 w-4 text-pink-600" />
                </Button>
              </a>
            </div>
          </div>
        </div>

        {/* Feedback Form */}
        <div className="max-w-2xl mx-auto bg-white dark:bg-gray-800 p-8 rounded-lg shadow-lg">
          <h2 className="text-2xl font-semibold mb-6 flex items-center">
            <MessageCircle className="h-6 w-6 mr-2 text-blue-600" />
            Share Your Feedback
          </h2>
          <form onSubmit={handleSubmit}>
            <div className="space-y-6">
              {/* Anonymous Feedback Textarea */}
              <Textarea
                placeholder="Your feedback or suggestions (anonymous)"
                className="min-h-[150px]"
                required
              />

              {/* Submit Button */}
              <Button type="submit" className="w-full">
                <Send className="mr-2 h-4 w-4" />
                Submit Feedback
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
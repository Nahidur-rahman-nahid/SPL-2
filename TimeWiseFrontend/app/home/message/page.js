"use client";
import React, { useState, useEffect, useMemo } from 'react';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Search, Send, Trash2, Users, Sparkles, FileText, MessageSquare, User } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { format } from 'date-fns';

const MessageDashboard = () => {
  const router = useRouter();
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [showSummary, setShowSummary] = useState(false);
  const [summaryText, setSummaryText] = useState('');
  const [summarizing, setSummarizing] = useState(false);
  const [suggestions, setSuggestions] = useState([]);
  
  const [isInputFocused, setIsInputFocused] = useState(false);

  
  // New message form state
  const [newMessage, setNewMessage] = useState({
    recipient: '',
   
    messageDescription: '',
    isTeamMessage: false,
  });
  
  // AI prompt state
  const [autoCompletingPrompt, setAutocompletingPrompt] = useState('');
  const [showPromptDialog, setShowPromptDialog] = useState(false);
  const [autoCompletingMessage, setAutoCompletingMessage] = useState(false);

  useEffect(() => {
    fetchMessages();
  }, []);

  const fetchMessages = async () => {
    try {
      setLoading(true);
      const response = await fetch(`/api/messages/all`);
      const messages = await response.json();
      setMessages(messages);
    } catch (error) {
      console.error('Error fetching messages:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmitMessage = async () => {
    try {
      const endpoint = newMessage.isTeamMessage ? '/api/messages/team/send' : '/api/messages/user/send';
      const subject = newMessage.isTeamMessage ? 'TEAM_MESSAGE':'USER_MESSAGE';
      const response = await fetch(endpoint, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          recipient: newMessage.recipient,
          messageSubject: subject,
          messageDescription: newMessage.messageDescription,
        }),
      });
      
      if (response.ok) {
        // Reset form and refresh messages
        setNewMessage({
          recipient: '',
         
          messageDescription: '',
          isTeamMessage: false,
        });
        fetchMessages();
      } else {
        const errorData = await response.json();
        console.error('Error sending message:', errorData);
      }
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const handleRemoveMessage = async (messageId) => {
    try {
      const response = await fetch(`/api/messages/remove?messageId=${messageId}`, {
        method: 'DELETE',
      });
      
      if (response.ok) {
        fetchMessages();
      } else {
        const errorData = await response.json();
        console.error('Error removing message:', errorData);
      }
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const handleAutoCompleteMessageFromPrompt = async () => {
    if (!autoCompletingPrompt.trim()) return;
    
    try {
      setAutoCompletingMessage(true);
      const response = await fetch('/api/messages/autocomplete', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ prompt: autoCompletingPrompt }),
      });
      
      if (response.ok) {
        const data = await response.json();
        setNewMessage(prev => ({
          ...prev,
          messageDescription: data.generatedMessage || data.text || data.content || '',
        }));
        setShowPromptDialog(false);
      } else {
        console.error('Error generating message');
      }
    } catch (error) {
      console.error('Error:', error);
    } finally {
      setAutoCompletingMessage(false);
    }
  };

  const handleSummarizeMessages = async () => {
    try {
      setSummarizing(true);
      const response = await fetch('/api/messages/summarize', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ limit: 50 }), // Optional limit parameter
      });
      
      if (response.ok) {
        const data = await response.json();
        setSummaryText(data.summary || data.text || 'No summary available.');
        setShowSummary(true);
      } else {
        console.error('Error summarizing messages');
      }
    } catch (error) {
      console.error('Error:', error);
    } finally {
      setSummarizing(false);
    }
  };

  const sentMessages = useMemo(() => {
    return messages.filter(message => message.sender === UserCredentials.getCurrentUsername());
  }, [messages]);
  
  const receivedMessages = useMemo(() => {
    return messages.filter(message => 
      message.recipients && 
      message.recipients.includes(UserCredentials.getCurrentUsername())
    );
  }, [messages]);

  const filteredMessages = useMemo(() => {
    if (!searchTerm) return messages;
    
    const term = searchTerm.toLowerCase();
    return messages.filter(message => 
      (message.sender && message.sender.toLowerCase().includes(term)) ||
      (message.recipients && message.recipients.some(recipient => recipient.toLowerCase().includes(term))) ||
     
      (message.messageDescription && message.messageDescription.toLowerCase().includes(term))
    );
  }, [messages, searchTerm]);

  const getInitials = (name) => {
    if (!name) return '?';
    return name.split(' ').map((n) => n[0]).join('').toUpperCase();
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'READ': return 'bg-green-500';
      case 'DELIVERED': return 'bg-blue-500';
      case 'SENT': return 'bg-yellow-500';
      case 'PENDING': return 'bg-orange-500';
      default: return 'bg-gray-500';
    }
  };

  const renderMessageItem = (message) => {
    const isSender = message.sender === UserCredentials.getCurrentUsername();
    const recipientText = message.recipients && message.recipients.length > 1 
      ? `${message.recipients.length} recipients` 
      : message.recipients && message.recipients[0];
    
    return (
      <Card key={message.messageId} className="mb-4">
        <CardHeader className="pb-2">
          <div className="flex justify-between items-start">
            <div className="flex items-center gap-3">
              <Avatar>
                <AvatarFallback className={isSender ? 'bg-blue-100' : 'bg-green-100'}>
                  {getInitials(isSender ? recipientText : message.sender)}
                </AvatarFallback>
              </Avatar>
              <div>
                <CardTitle className="text-base">
                  {isSender ? 'To: ' + (recipientText || 'Unknown') : 'From: ' + message.sender}
                </CardTitle>
                <CardDescription className="text-sm">
                  Subject: {message.messageSubject}
                </CardDescription>
              </div>
            </div>
            <div className="flex items-center gap-2">
              {message.messageStatus && (
                <Badge className={`${getStatusColor(message.messageStatus)} text-white`}>
                  {message.messageStatus}
                </Badge>
              )}
              <Button 
                variant="ghost" 
                size="icon"
                onClick={() => handleRemoveMessage(message.messageId)}
              >
                <Trash2 className="h-4 w-4" />
              </Button>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <p className="text-sm whitespace-pre-line">{message.messageDescription}</p>
        </CardContent>
        <CardFooter className="pt-0">
          <p className="text-xs text-gray-500">
            {message.timeStamp ? format(new Date(message.timeStamp), 'PPp') : 'Date unavailable'}
          </p>
        </CardFooter>
      </Card>
    );
  };


  useEffect(() => {
    try {
      const userData = JSON.parse(localStorage.getItem('TimeWiseUserData'));
      if (userData) {
        if (newMessage.isTeamMessage && userData.userTeams) {
          // Extract team names from userTeams array
          setSuggestions(userData.userTeams.map(team => team.teamName));
        } else if (!newMessage.isTeamMessage && userData.usersFollowing) {
          // Use usersFollowing directly
          setSuggestions(userData.usersFollowing);
        } else {
          setSuggestions([]);
        }
      } else {
        setSuggestions([]);
      }
    } catch (error) {
      console.error('Error parsing userData from localStorage:', error);
      setSuggestions([]);
    }
  }, [newMessage.isTeamMessage]);
  
  
  const handleInputChange = (e) => {
    setNewMessage({ ...newMessage, recipient: e.target.value });
  };
  
  const handleSuggestionClick = (suggestion) => {
    setNewMessage({ ...newMessage, recipient: suggestion });
    setIsInputFocused(false);
  };
  


  return (
    <div className="container mx-auto py-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Messages Dashboard</h1>
        <div className="flex gap-2">
          <Button onClick={handleSummarizeMessages} disabled={summarizing}>
            <FileText className="mr-2 h-4 w-4" />
            {summarizing ? 'Summarizing...' : 'Summarize Messages'}
          </Button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Left Column - Create New Message */}
        <div className="md:col-span-1">
          <Card>
            <CardHeader>
              <CardTitle>New Message</CardTitle>
              <CardDescription>
                Send a message to a user or team
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div>
                  <label htmlFor="isTeamMessage" className="block text-sm font-medium mb-1">
                    Message Type
                  </label>
                  <div className="flex gap-4">
                    <Button
                      variant={newMessage.isTeamMessage ? "outline" : "default"}
                      className="flex-1"
                      onClick={() => setNewMessage({...newMessage, isTeamMessage: false})}
                    >
                      <User className="mr-2 h-4 w-4" />
                      User
                    </Button>
                    <Button
                      variant={newMessage.isTeamMessage ? "default" : "outline"}
                      className="flex-1"
                      onClick={() => setNewMessage({...newMessage, isTeamMessage: true})}
                    >
                      <Users className="mr-2 h-4 w-4" />
                      Team
                    </Button>
                  </div>
                </div>
                
                <div>
  <label htmlFor="recipient" className="block text-sm font-medium mb-1">
    {newMessage.isTeamMessage ? 'Team' : 'Recipient'}
  </label>
  <div className="relative">
    <Input
      id="recipient"
      placeholder={newMessage.isTeamMessage ? 'Team name' : 'Username of recipient'}
      value={newMessage.recipient}
      onChange={handleInputChange}
      onFocus={() => setIsInputFocused(true)}
      onBlur={() => setTimeout(() => setIsInputFocused(false), 200)}
    />
    
    {isInputFocused && suggestions.length > 0 && (
      <div className="absolute z-10 mt-1 w-full rounded-md bg-black shadow-lg border border-gray-900 max-h-60 overflow-auto">
        <ul className="py-1 text-sm">
          {suggestions
            .filter(suggestion => 
              suggestion.toLowerCase().includes(newMessage.recipient.toLowerCase())
            )
            .map((suggestion) => (
              <li
                key={suggestion}
                className="cursor-pointer select-none py-2 px-4 hover:bg-gray-700"
                onMouseDown={(e) => {
                  e.preventDefault();
                  handleSuggestionClick(suggestion);
                }}
              >
                {suggestion}
              </li>
            ))}
        </ul>
      </div>
    )}
  </div>
</div>
                
                
                
                <div>
                  <div className="flex justify-between items-center mb-1">
                    <label htmlFor="description" className="block text-sm font-medium">
                      Message
                    </label>
                    <Button 
                      variant="ghost" 
                      size="sm" 
                      onClick={() => setShowPromptDialog(true)}
                    >
                      <Sparkles className="h-4 w-4 mr-1" />
                      Auto Complete
                    </Button>
                  </div>
                  <Textarea 
                    id="description"
                    placeholder="Your message"
                    rows={6}
                    value={newMessage.messageDescription}
                    onChange={(e) => setNewMessage({...newMessage, messageDescription: e.target.value})}
                  />
                </div>
              </div>
            </CardContent>
            <CardFooter>
              <Button 
                className="w-full" 
                onClick={handleSubmitMessage}
                disabled={!newMessage.recipient  || !newMessage.messageDescription}
              >
                <Send className="mr-2 h-4 w-4" />
                Send Message
              </Button>
            </CardFooter>
          </Card>
        </div>

        {/* Right Column - Message List */}
        <div className="md:col-span-2">
          <Card>
            <CardHeader className="pb-2">
              <div className="flex flex-col md:flex-row md:justify-between md:items-center gap-4">
                <CardTitle>Messages</CardTitle>
                <div className="relative w-full md:w-64">
                  <Search className="absolute left-2 top-2.5 h-4 w-4 text-gray-500" />
                  <Input 
                    placeholder="Search messages..." 
                    className="pl-8"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                  />
                </div>
              </div>
            </CardHeader>
            <CardContent>
              <Tabs defaultValue="all">
                <TabsList className="mb-4">
                  <TabsTrigger value="all">All ({messages.length})</TabsTrigger>
                  <TabsTrigger value="sent">Sent ({sentMessages.length})</TabsTrigger>
                  <TabsTrigger value="received">Received ({receivedMessages.length})</TabsTrigger>
                </TabsList>
                
                <TabsContent value="all">
                  {loading ? (
                    <div className="text-center py-8">Loading messages...</div>
                  ) : filteredMessages.length === 0 ? (
                    <div className="text-center py-8">No messages found</div>
                  ) : (
                    <div className="space-y-4">
                      {filteredMessages.map(renderMessageItem)}
                    </div>
                  )}
                </TabsContent>
                
                <TabsContent value="sent">
                  {loading ? (
                    <div className="text-center py-8">Loading messages...</div>
                  ) : sentMessages.length === 0 ? (
                    <div className="text-center py-8">No sent messages</div>
                  ) : (
                    <div className="space-y-4">
                      {sentMessages
                        .filter(message => {
                          if (!searchTerm) return true;
                          const term = searchTerm.toLowerCase();
                          return (
                            (message.recipients && message.recipients.some(recipient => recipient.toLowerCase().includes(term))) ||
                           
                            (message.messageDescription && message.messageDescription.toLowerCase().includes(term))
                          );
                        })
                        .map(renderMessageItem)}
                    </div>
                  )}
                </TabsContent>
                
                <TabsContent value="received">
                  {loading ? (
                    <div className="text-center py-8">Loading messages...</div>
                  ) : receivedMessages.length === 0 ? (
                    <div className="text-center py-8">No received messages</div>
                  ) : (
                    <div className="space-y-4">
                      {receivedMessages
                        .filter(message => {
                          if (!searchTerm) return true;
                          const term = searchTerm.toLowerCase();
                          return (
                            (message.sender && message.sender.toLowerCase().includes(term)) ||
                           
                            (message.messageDescription && message.messageDescription.toLowerCase().includes(term))
                          );
                        })
                        .map(renderMessageItem)}
                    </div>
                  )}
                </TabsContent>
              </Tabs>
            </CardContent>
          </Card>
        </div>
      </div>

      {/* Prompt Dialog */}
      <Dialog open={showPromptDialog} onOpenChange={setShowPromptDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Autocomplete Message</DialogTitle>
            <DialogDescription>
              Describe what you want to say, and we will help craft your message.
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <Textarea
              placeholder="Example: Write a request for the design team to review my project proposal, mentioning the deadline is next Friday."
              rows={5}
              value={autoCompletingPrompt}
              onChange={(e) => setAutocompletingPrompt(e.target.value)}
            />
          </div>
          <DialogFooter>
            <Button 
              variant="outline" 
              onClick={() => setShowPromptDialog(false)}
            >
              Cancel
            </Button>
            <Button 
              onClick={handleAutoCompleteMessageFromPrompt}
              disabled={!autoCompletingPrompt.trim() || autoCompletingMessage}
            >
              <Sparkles className="mr-2 h-4 w-4" />
              {autoCompletingMessage ? 'Processing...' : 'Generate Message'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Message Summary Dialog */}
      <Dialog open={showSummary} onOpenChange={setShowSummary}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>Message Summary</DialogTitle>
            <DialogDescription>
              Summary of recent message patterns and insights.
            </DialogDescription>
          </DialogHeader>
          <div className="max-h-96 overflow-y-auto whitespace-pre-line">
            {summaryText}
          </div>
          <DialogFooter>
            <Button onClick={() => setShowSummary(false)}>
              Close
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default MessageDashboard;
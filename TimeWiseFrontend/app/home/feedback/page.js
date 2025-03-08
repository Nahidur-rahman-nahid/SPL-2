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
import { Search, Send, Trash2, BarChart2, Sparkles, FileText, MessageSquare } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { format } from 'date-fns';
import AnalyzeDataButton from '@/components/AnalyzeDataButton';

const FeedbackDashboard = () => {
  const router = useRouter();
  const [feedbacks, setFeedbacks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [showSummary, setShowSummary] = useState(false);
  const [summaryText, setSummaryText] = useState('');
  const [summarizing, setSummarizing] = useState(false);
  const [isInputFocused, setIsInputFocused] = useState(false);
  const [suggestions, setSuggestions] = useState([]);
   const [selectedTask, setSelectedTask] = useState('');
      const [availableTasks, setAvailableTasks] = useState([]);
  
  // New feedback form state
  const [newFeedback, setNewFeedback] = useState({
    feedbackRecipient: '',
    feedbackTaskName: '',
    feedbackScore: 50,
    feedbackMessage: '',
  });
  
  // AI prompt state
  const [autoCompletingPrompt, setAutocompletingPrompt] = useState('');
  const [showPromptDialog, setShowPromptDialog] = useState(false);
  const [autoCompletingFeedback, setautoCompletingFeedback] = useState(false);

  useEffect(() => {
    fetchFeedbacks();
  }, []);

  const fetchFeedbacks = async () => {
    try {
      setLoading(true);
      const response = await fetch(`/api/feedbacks`);
      const feedbacks = await response.json();
      const sortedFeedbacks = feedbacks.sort((a, b) => {
        return new Date(b.timeStamp) - new Date(a.timeStamp);
      });
      setFeedbacks(sortedFeedbacks);
    } catch (error) {
      console.error('Error fetching feedbacks:', error);
    } finally {
      setLoading(false);
    }
  };

   useEffect(() => {
        const userData = JSON.parse(localStorage.getItem('TimeWiseUserData'));
        const currentUser = userData?.userName; 
      
        if (userData?.userTasks && currentUser) {
          const filteredTasks = userData.userTasks
            .filter((task) => task.taskOwner === currentUser);
          setAvailableTasks(filteredTasks);
        } else {
          setAvailableTasks([]);
        }
      }, []);

  const handleSubmitFeedback = async () => {
    try {
      const response = await fetch('/api/feedbacks/send', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(newFeedback),
      });
      
      if (response.ok) {
        // Reset form and refresh feedbacks
        setNewFeedback({
          feedbackRecipient: '',
          feedbackTaskName: '',
          feedbackScore: 50,
          feedbackMessage: '',
        });
        fetchFeedbacks();
      } else {
        const errorData = await response.json();
        console.error('Error sending feedback:', errorData);
      }
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const handleRemoveFeedback = async (feedback) => {
    try {
      console.log("Feedback object to remove:", feedback);
        const response = await fetch(`/api/feedbacks/remove`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(feedback), 
        });
      
      if (response.ok) {
        fetchFeedbacks();
      } else {
        const errorData = await response.json();
        console.error('Error removing feedback:', errorData);
      }
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const handleAutoCompleteFeedbackFromPrompt = async () => {
    if (!autoCompletingPrompt.trim()) return;
    
    try {
      setautoCompletingFeedback(true);
      const response = await fetch(`/api/feedbacks/autocomplete?feedbackSubject=${encodeURIComponent(autoCompletingPrompt)}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
        
      });
      
      if (response.ok) {
        const data = await response.text();
        setNewFeedback(prev => ({
          ...prev,
          feedbackMessage: data || '',
        }));
        setShowPromptDialog(false);
      } else {
        console.error('Error generating feedback');
      }
    } catch (error) {
      console.error('Error:', error);
    } finally {
      setautoCompletingFeedback(false);
    }
  };
  const handleSuggestionClick = (suggestion) => {
    setNewFeedback({ ...newFeedback, feedbackRecipient: suggestion });
    setIsInputFocused(false);
  };
  const handleSummarizeFeedbacks = async () => {
    try {
      setSummarizing(true);
      const response = await fetch('/api/feedback/summarize', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ limit: 50 }), 
      });
      
      if (response.ok) {
        const data = await response.json();
        setSummaryText(data.summary || data.text || 'No summary available.');
        setShowSummary(true);
      } else {
        console.error('Error summarizing feedbacks');
      }
    } catch (error) {
      console.error('Error:', error);
    } finally {
      setSummarizing(false);
    }
  };
  const useCurrentUser = () => {
    if (typeof window !== 'undefined') {
      const userData = JSON.parse(localStorage.getItem('TimeWiseUserData'));
      return userData?.userName;
    }
    return null; 
  };

  const sentFeedbacks = useMemo(() => {
    return feedbacks.filter(feedback => feedback.feedbackSender === useCurrentUser());
  }, [feedbacks]);
  
  const receivedFeedbacks = useMemo(() => {
    return feedbacks.filter(feedback => feedback.feedbackRecipient === useCurrentUser());
  }, [feedbacks]);

  const filteredFeedbacks = useMemo(() => {
    if (!searchTerm) return feedbacks;
    
    const term = searchTerm.toLowerCase();
    return feedbacks.filter(feedback => 
      (feedback.feedbackSender && feedback.feedbackSender.toLowerCase().includes(term)) ||
      (feedback.feedbackRecipient && feedback.feedbackRecipient.toLowerCase().includes(term)) ||
      (feedback.feedbackTaskName && feedback.feedbackTaskName.toLowerCase().includes(term)) ||
      (feedback.feedbackMessage && feedback.feedbackMessage.toLowerCase().includes(term))
    );
  }, [feedbacks, searchTerm]);

  const getInitials = (name) => {
    if (!name) return '?';
    return name.split(' ').map((n) => n[0]).join('').toUpperCase();
  };

  const getScoreColor = (score) => {
    if (score >= 80) return 'bg-green-500';
    if (score >= 60) return 'bg-blue-500';
    if (score >= 40) return 'bg-yellow-500';
    if (score >= 20) return 'bg-orange-500';
    return 'bg-red-500';
  };

  const renderFeedbackItem = (feedback) => {
    const isSender = feedback.feedbackSender === useCurrentUser();
    return (
      <Card key={`${feedback.feedbackId}-${feedback.timeStamp}`} className="mb-4">
        <CardHeader className="pb-2">
          <div className="flex justify-between items-start">
            <div className="flex items-center gap-3">
              <Avatar>
                <AvatarFallback className={isSender ? 'bg-blue-100' : 'bg-green-100'}>
                  {getInitials(isSender ? feedback.feedbackRecipient : feedback.feedbackSender)}
                </AvatarFallback>
              </Avatar>
              <div>
                <CardTitle className="text-base">
                  {isSender ? 'To: ' + feedback.feedbackRecipient : 'From: ' + feedback.feedbackSender}
                </CardTitle>
                <CardDescription className="text-sm">
                  Task: {feedback.feedbackTaskName}
                </CardDescription>
              </div>
            </div>
            <div className="flex items-center gap-2">
              <Badge className={`${getScoreColor(feedback.feedbackScore)} text-white`}>
                Score: {feedback.feedbackScore}
              </Badge>
              <Button 
                variant="ghost" 
                size="icon"
                onClick={() => handleRemoveFeedback(feedback.feedbackId)}
              >
                <Trash2 className="h-4 w-4" />
              </Button>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <p className="text-sm whitespace-pre-line">{feedback.feedbackMessage}</p>
        </CardContent>
        <CardFooter className="pt-0">
          <p className="text-xs text-gray-500">
            {feedback.timeStamp ? format(new Date(feedback.timeStamp), 'PPp') : 'Date unavailable'}
          </p>
        </CardFooter>
      </Card>
    );
  };
    useEffect(() => {
      try {
        const userData = JSON.parse(localStorage.getItem('TimeWiseUserData'));
        if (userData) {
           if ( userData.usersFollowing) {
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
    }, []);
     useEffect(() => {
          const userData = JSON.parse(localStorage.getItem('TimeWiseUserData'));
          const currentUser = userData?.userName; 
        
          if (userData?.userTasks && currentUser) {
            const filteredTasks = userData.userTasks
              .filter((task) => task.taskOwner === currentUser) 
              .map((task) => task.taskName)
              ;
            setAvailableTasks(filteredTasks);
          } else {
            setAvailableTasks([]);
          }
        }, []);
        const filteredTasks = availableTasks.filter((task) =>
          task.toLowerCase().includes(searchTerm.toLowerCase())
        );

  return (
    <div className="container mx-auto py-1">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Feedback Dashboard</h1>
        <div className="flex gap-2">
          <Button 
            variant="outline" 
            onClick={() => router.push('/home/statistics/feedback')}
          >
            <BarChart2 className="mr-2 h-4 w-4" />
            Feedback Statistics
          </Button>
           <AnalyzeDataButton
                                    data={feedbacks}
                                    buttonText="Analyze Feedbacks"
                                  />
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Left Column - Create New Feedback */}
        <div className="md:col-span-1">
          <Card>
            <CardHeader>
              <CardTitle>Create New Feedback</CardTitle>
              <CardDescription>
                Provide feedback to your teammates
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div>
                  <label htmlFor="recipient" className="block text-sm font-medium mb-1">
                    Recipient
                  </label>
                  <Input 
                    id="recipient"
                    placeholder="Username of recipient"
                    value={newFeedback.feedbackRecipient}
                    onFocus={() => setIsInputFocused(true)}
                    onChange={(e) => setNewFeedback({...newFeedback, feedbackRecipient: e.target.value})}
                    onBlur={() => setTimeout(() => setIsInputFocused(false), 200)}
                  />
                  {isInputFocused && suggestions.length > 0 && (
  <div className="absolute z-10 mt-1 w-full max-w-[300px] rounded-md bg-black shadow-lg border border-gray-900 max-h-60 overflow-auto">
    <ul className="py-1 text-sm">
      {suggestions
        .filter(suggestion => 
          suggestion.toLowerCase().includes(newFeedback.feedbackRecipient.toLowerCase())
        )
        .map((suggestion) => (
          <li
            key={suggestion}
            className="cursor-pointer select-none py-2 px-4 hover:bg-gray-700 whitespace-nowrap overflow-hidden text-ellipsis"
            onMouseDown={(e) => {
              e.preventDefault();
              handleSuggestionClick(suggestion);
              setIsInputFocused(false);
            }}
          >
            {suggestion}
          </li>
        ))}
    </ul>
  </div>
)}

                </div>
                
                <div>
                  <label htmlFor="task" className="block text-sm font-medium mb-1">
                    Task Name
                  </label>
                  <Select
  value={newFeedback.feedbackTaskName} 
  onValueChange={(value) => setNewFeedback({ ...newFeedback, feedbackTaskName: value })}
>
  <SelectTrigger className="w-full">
    <SelectValue placeholder="Select a task" />
  </SelectTrigger>

  <SelectContent className="max-h-[200px]">
    {filteredTasks.length > 0 ? (
      filteredTasks.map((task) => (
        <SelectItem key={task} value={task}>
          {task}
        </SelectItem>
      ))
    ) : (
      <div className="p-2 text-center text-muted-foreground">
        {searchTerm ? 'No matching tasks found' : 'No available tasks'}
      </div>
    )}
  </SelectContent>
</Select>

                </div>
                
                <div>
                  <label htmlFor="score" className="block text-sm font-medium mb-1">
                    Score (0-100): {newFeedback.feedbackScore}
                  </label>
                  <Input 
                    id="score"
                    type="range"
                    min="0"
                    max="100"
                    value={newFeedback.feedbackScore}
                    onChange={(e) => setNewFeedback({...newFeedback, feedbackScore: parseInt(e.target.value)})}
                  />
                </div>
                
                <div>
                  <div className="flex justify-between items-center mb-1">
                    <label htmlFor="message" className="block text-sm font-medium">
                      Feedback Message
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
                    id="message"
                    placeholder="Your feedback message"
                    rows={6}
                    value={newFeedback.feedbackMessage}
                    onChange={(e) => setNewFeedback({...newFeedback, feedbackMessage: e.target.value})}
                  />
                </div>
              </div>
            </CardContent>
            <CardFooter>
              <Button 
                className="w-full" 
                onClick={handleSubmitFeedback}
                disabled={!newFeedback.feedbackRecipient || !newFeedback.feedbackTaskName || !newFeedback.feedbackMessage}
              >
                <Send className="mr-2 h-4 w-4" />
                Send Feedback
              </Button>
            </CardFooter>
          </Card>
        </div>

        {/* Right Column - Feedback List */}
        <div className="md:col-span-2">
          <Card>
            <CardHeader className="pb-2">
              <div className="flex flex-col md:flex-row md:justify-between md:items-center gap-4">
                <CardTitle>Feedback Records</CardTitle>
                <div className="relative w-full md:w-64">
                  <Search className="absolute left-2 top-2.5 h-4 w-4 text-gray-500" />
                  <Input 
                    placeholder="Search feedback..." 
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
                  <TabsTrigger value="all">All ({feedbacks.length})</TabsTrigger>
                  <TabsTrigger value="sent">Sent ({sentFeedbacks.length})</TabsTrigger>
                  <TabsTrigger value="received">Received ({receivedFeedbacks.length})</TabsTrigger>
                </TabsList>
                
                <TabsContent value="all">
                  {loading ? (
                    <div className="text-center py-8">Loading feedbacks...</div>
                  ) : filteredFeedbacks.length === 0 ? (
                    <div className="text-center py-8">No feedbacks found</div>
                  ) : (
                    <div className="space-y-4">
                      {filteredFeedbacks.map(renderFeedbackItem)}
                    </div>
                  )}
                </TabsContent>
                
                <TabsContent value="sent">
                  {loading ? (
                    <div className="text-center py-8">Loading feedbacks...</div>
                  ) : sentFeedbacks.length === 0 ? (
                    <div className="text-center py-8">No sent feedbacks</div>
                  ) : (
                    <div className="space-y-4">
                      {sentFeedbacks
                        .filter(feedback => {
                          if (!searchTerm) return true;
                          const term = searchTerm.toLowerCase();
                          return (
                            (feedback.feedbackRecipient && feedback.feedbackRecipient.toLowerCase().includes(term)) ||
                            (feedback.feedbackTaskName && feedback.feedbackTaskName.toLowerCase().includes(term)) ||
                            (feedback.feedbackMessage && feedback.feedbackMessage.toLowerCase().includes(term))
                          );
                        })
                        .map(renderFeedbackItem)}
                    </div>
                  )}
                </TabsContent>
                
                <TabsContent value="received">
                  {loading ? (
                    <div className="text-center py-8">Loading feedbacks...</div>
                  ) : receivedFeedbacks.length === 0 ? (
                    <div className="text-center py-8">No received feedbacks</div>
                  ) : (
                    <div className="space-y-4">
                      {receivedFeedbacks
                        .filter(feedback => {
                          if (!searchTerm) return true;
                          const term = searchTerm.toLowerCase();
                          return (
                            (feedback.feedbackSender && feedback.feedbackSender.toLowerCase().includes(term)) ||
                            (feedback.feedbackTaskName && feedback.feedbackTaskName.toLowerCase().includes(term)) ||
                            (feedback.feedbackMessage && feedback.feedbackMessage.toLowerCase().includes(term))
                          );
                        })
                        .map(renderFeedbackItem)}
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
            <DialogTitle>Autocomplete Feedback</DialogTitle>
            <DialogDescription>
              Describe what you want to say, and we will help craft your feedback.
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <Textarea
              placeholder="Example: Provide positive feedback about their excellent work on the project redesign. Mention their attention to detail and collaboration skills."
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
              onClick={handleAutoCompleteFeedbackFromPrompt}
              disabled={!autoCompletingPrompt.trim() || autoCompletingFeedback}
            >
              <Sparkles className="mr-2 h-4 w-4" />
              {autoCompletingFeedback ? 'Autocompleting...' : 'Autocomplete Feedback'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Feedback Summary Dialog */}
      <Dialog open={showSummary} onOpenChange={setShowSummary}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>Feedback Summary</DialogTitle>
            <DialogDescription>
              Summary of recent feedback patterns and insights.
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

export default FeedbackDashboard;
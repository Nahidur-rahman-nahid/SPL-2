"use client";
import React, { useState, useEffect, useMemo } from 'react';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Search, Trash2, Users, Bell, FileText, Check, X, Clock, Info, AlertCircle } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { format } from 'date-fns';

const NotificationDashboard = () => {
  const router = useRouter();
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [showSummary, setShowSummary] = useState(false);
  const [summaryText, setSummaryText] = useState('');
  const [summarizing, setSummarizing] = useState(false);

  useEffect(() => {
    fetchNotifications();
  }, []);

  const fetchNotifications = async () => {
    try {
      setLoading(true);
      const response = await fetch(`/api/notifications`);
      const notifications = await response.json();
      setNotifications(notifications);
    } catch (error) {
      console.error('Error fetching notifications:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleRemoveNotification = async (notificationId) => {
    try {
      const response = await fetch(`/api/notifications/remove?notificationId=${notificationId}`, {
        method: 'DELETE',
      });
      
      if (response.ok) {
        fetchNotifications();
      } else {
        const errorData = await response.json();
        console.error('Error removing notification:', errorData);
      }
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const handleRespondToInvitation = async (notificationId, action, type) => {
    try {
      let endpoint = '';
      
      switch (type) {
        case 'TEAM JOINING INVITATION':
        case 'TEAM JOINING INVITATION_RESPONSE':
          endpoint = '/api/teams/respond-invitation';
          break;
        case 'TASK_INVITATION':
        case 'TASK_INVITATION_RESPONSE':
          endpoint = '/api/tasks/respond-invitation';
          break;
        case 'TEAM_JOINING_REQUEST':
        case 'TEAM_JOINING_REQUEST_RESPONSE':
          endpoint = '/api/teams/respond-join-request';
          break;
        default:
          console.error('Unknown notification type:', type);
          return;
      }
      
      const response = await fetch(endpoint, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          notificationId,
          action, // 'accept' or 'decline'
          entityName: notifications.find(n => n.notificationId === notificationId)?.entityNameRelatedToNotification
        }),
      });
      
      if (response.ok) {
        fetchNotifications();
      } else {
        const errorData = await response.json();
        console.error('Error responding to invitation:', errorData);
      }
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const handleSummarizeNotifications = async () => {
    try {
      setSummarizing(true);
      const response = await fetch('/api/notifications/summarize', {
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
        console.error('Error summarizing notifications');
      }
    } catch (error) {
      console.error('Error:', error);
    } finally {
      setSummarizing(false);
    }
  };

  const markNotificationAsSeen = async (notificationId) => {
    try {
      const response = await fetch(`/api/notifications/mark-seen`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ notificationId }),
      });
      
      if (response.ok) {
        fetchNotifications();
      }
    } catch (error) {
      console.error('Error marking notification as seen:', error);
    }
  };
  const unseenNotifications = useMemo(() => {
    return Array.isArray(notifications)
      ? notifications.filter(notification => notification.notificationStatus === 'Unseen')
      : []; 
  }, [notifications]);

  const seenNotifications = useMemo(() => {
    return Array.isArray(notifications)
      ? notifications.filter(notification => notification.notificationStatus === '')
      : []; 
  }, [notifications]);

  const filteredNotifications = useMemo(() => {
    if (!Array.isArray(notifications)) {
      return []; // Return an empty array if notifications is not an array
    }
  
    if (!searchTerm) {
      return notifications;
    }
  
    const term = searchTerm.toLowerCase();
    return notifications.filter(notification =>
      (notification.sender && notification.sender.toLowerCase().includes(term)) ||
      (notification.recipients && notification.recipients.some(recipient => recipient.toLowerCase().includes(term))) ||
      (notification.notificationSubject && notification.notificationSubject.toLowerCase().includes(term)) ||
      (notification.notificationMessage && notification.notificationMessage.toLowerCase().includes(term)) ||
      (notification.entityNameRelatedToNotification && notification.entityNameRelatedToNotification.toLowerCase().includes(term))
    );
  }, [notifications, searchTerm]);
  const getInitials = (name) => {
    if (!name) return '?';
    return name.split(' ').map((n) => n[0]).join('').toUpperCase();
  };

  const getNotificationIcon = (type) => {
    switch (type) {
      case 'TEAM JOINING INVITATION':
      case 'TEAM_JOINING_REQUEST':
      case 'TEAM JOINING INVITATION_RESPONSE':
      case 'TEAM_JOINING_REQUEST_RESPONSE':
        return <Users className="h-5 w-5" />;
      case 'TASK_INVITATION':
      case 'TASK_INVITATION_RESPONSE':
        return <FileText className="h-5 w-5" />;
      case 'TASK_UPDATED':
      case 'TASK_DELETED':
        return <AlertCircle className="h-5 w-5" />;
      default:
        return <Info className="h-5 w-5" />;
    }
  };

  const getNotificationColor = (type) => {
    if (type.includes('INVITATION')) return 'bg-blue-100 text-blue-800';
    if (type.includes('REQUEST')) return 'bg-purple-100 text-purple-800';
    if (type.includes('RESPONSE')) return 'bg-green-100 text-green-800';
    if (type.includes('DELETED')) return 'bg-red-100 text-red-800';
    if (type.includes('UPDATED')) return 'bg-yellow-100 text-yellow-800';
    return 'bg-gray-100 text-gray-800';
  };

  const isActionableNotification = (type) => {
    return [
      'TEAM JOINING INVITATION',
      'TASK_INVITATION',
      'TEAM_JOINING_REQUEST'
    ].includes(type);
  };

  const renderNotificationItem = (notification) => {
    const isActionable = isActionableNotification(notification.notificationSubject);
    
    return (
      <Card 
        key={`${notification.notificationId}-${notification.timeStamp}`} 
        className={`mb-4 ${notification.notificationStatus === 'UNSEEN' ? 'border-l-4 border-l-blue-500' : ''}`}
      >
        <CardHeader className="pb-2">
          <div className="flex justify-between items-start">
            <div className="flex items-center gap-3">
              <Avatar className={getNotificationColor(notification.notificationSubject)}>
                <AvatarFallback>
                  {getNotificationIcon(notification.notificationSubject)}
                </AvatarFallback>
              </Avatar>
              <div>
                <CardTitle className="text-base">
                  {notification.sender} 
                  {notification.entityNameRelatedToNotification ? 
                    ` • ${notification.entityNameRelatedToNotification}` : ''}
                </CardTitle>
                <CardDescription className="text-sm">
                  {notification.notificationSubject.replace(/_/g, ' ')}
                </CardDescription>
              </div>
            </div>
            <div className="flex items-center gap-2">
              {notification.notificationStatus === 'UNSEEN' && (
                <Badge className="bg-blue-500 text-white">
                  New
                </Badge>
              )}
              <Button 
                variant="ghost" 
                size="icon"
                onClick={() => handleRemoveNotification(notification.notificationId)}
              >
                <Trash2 className="h-4 w-4" />
              </Button>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <p className="text-sm whitespace-pre-line mb-3">{notification.notificationMessage}</p>
          
          {isActionable && (
            <div className="flex gap-2 mt-2">
              <Button 
                size="sm"
                className="bg-green-600 hover:bg-green-700"
                onClick={() => handleRespondToInvitation(
                  notification.notificationId, 
                  'accept', 
                  notification.notificationSubject
                )}
              >
                <Check className="mr-1 h-4 w-4" />
                Accept
              </Button>
              <Button 
                size="sm"
                variant="outline"
                className="text-red-600 border-red-600 hover:bg-red-50"
                onClick={() => handleRespondToInvitation(
                  notification.notificationId, 
                  'decline', 
                  notification.notificationSubject
                )}
              >
                <X className="mr-1 h-4 w-4" />
                Decline
              </Button>
            </div>
          )}
        </CardContent>
        <CardFooter className="pt-0">
          <p className="text-xs text-gray-500">
            {notification.timeStamp ? format(new Date(notification.timeStamp), 'PPp') : 'Date unavailable'}
          </p>
        </CardFooter>
      </Card>
    );
  };

  return (
    <div className="container mx-auto py-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Notifications</h1>
        <div className="flex gap-2">
          <Button onClick={handleSummarizeNotifications} disabled={summarizing}>
            <FileText className="mr-2 h-4 w-4" />
            {summarizing ? 'Summarizing...' : 'Summarize Notifications'}
          </Button>
        </div>
      </div>

      <Card>
        <CardHeader className="pb-2">
          <div className="flex flex-col md:flex-row md:justify-between md:items-center gap-4">
            <CardTitle>
              <div className="flex items-center">
                <Bell className="mr-2 h-5 w-5" />
                Your Notifications
                {unseenNotifications.length > 0 && (
                  <Badge className="ml-2 bg-blue-500">{unseenNotifications.length} new</Badge>
                )}
              </div>
            </CardTitle>
            <div className="relative w-full md:w-64">
              <Search className="absolute left-2 top-2.5 h-4 w-4 text-gray-500" />
              <Input 
                placeholder="Search notifications..." 
                className="pl-8"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <div className="mb-4 flex flex-wrap gap-2">
            <Badge className="bg-blue-100 text-blue-800 hover:bg-blue-200 cursor-pointer" onClick={() => setSearchTerm('TEAM JOINING INVITATION')}>
              <Users className="mr-1 h-3 w-3" /> Team Invitations
            </Badge>
            <Badge className="bg-purple-100 text-purple-800 hover:bg-purple-200 cursor-pointer" onClick={() => setSearchTerm('JOINING_REQUEST')}>
              <Users className="mr-1 h-3 w-3" /> Join Requests
            </Badge>
            <Badge className="bg-green-100 text-green-800 hover:bg-green-200 cursor-pointer" onClick={() => setSearchTerm('TASK_INVITATION')}>
              <FileText className="mr-1 h-3 w-3" /> Task Invitations
            </Badge>
            <Badge className="bg-red-100 text-red-800 hover:bg-red-200 cursor-pointer" onClick={() => setSearchTerm('DELETED')}>
              <AlertCircle className="mr-1 h-3 w-3" /> Deletions
            </Badge>
            <Badge className="bg-yellow-100 text-yellow-800 hover:bg-yellow-200 cursor-pointer" onClick={() => setSearchTerm('UPDATED')}>
              <Info className="mr-1 h-3 w-3" /> Updates
            </Badge>
          </div>
          
          <Tabs defaultValue="all">
            <TabsList className="mb-4">
              <TabsTrigger value="all">All ({notifications.length})</TabsTrigger>
              <TabsTrigger value="unseen">Unseen ({unseenNotifications.length})</TabsTrigger>
              <TabsTrigger value="seen">Seen ({seenNotifications.length})</TabsTrigger>
            </TabsList>
            
            <TabsContent value="all">
  {loading ? (
    <div className="text-center py-8">Loading notifications...</div>
  ) : Array.isArray(filteredNotifications) && filteredNotifications.length === 0 ? (
    <div className="text-center py-8">No notifications found</div>
  ) : Array.isArray(filteredNotifications) ? (
    <div className="space-y-4">
      {filteredNotifications.map(renderNotificationItem)}
    </div>
  ) : (
    <div className="text-center py-8">No notifications found</div>
  )}
</TabsContent>
            <TabsContent value="unseen">
              {loading ? (
                <div className="text-center py-8">Loading notifications...</div>
              ) : unseenNotifications.length === 0 ? (
                <div className="text-center py-8">No unseen notifications</div>
              ) : (
                <div className="space-y-4">
                  {unseenNotifications
                    .filter(notification => {
                      if (!searchTerm) return true;
                      const term = searchTerm.toLowerCase();
                      return (
                        (notification.sender && notification.sender.toLowerCase().includes(term)) ||
                        (notification.notificationSubject && notification.notificationSubject.toLowerCase().includes(term)) ||
                        (notification.notificationMessage && notification.notificationMessage.toLowerCase().includes(term)) ||
                        (notification.entityNameRelatedToNotification && notification.entityNameRelatedToNotification.toLowerCase().includes(term))
                      );
                    })
                    .map(renderNotificationItem)}
                </div>
              )}
            </TabsContent>
            
            <TabsContent value="seen">
              {loading ? (
                <div className="text-center py-8">Loading notifications...</div>
              ) : seenNotifications.length === 0 ? (
                <div className="text-center py-8">No seen notifications</div>
              ) : (
                <div className="space-y-4">
                  {seenNotifications
                    .filter(notification => {
                      if (!searchTerm) return true;
                      const term = searchTerm.toLowerCase();
                      return (
                        (notification.sender && notification.sender.toLowerCase().includes(term)) ||
                        (notification.notificationSubject && notification.notificationSubject.toLowerCase().includes(term)) ||
                        (notification.notificationMessage && notification.notificationMessage.toLowerCase().includes(term)) ||
                        (notification.entityNameRelatedToNotification && notification.entityNameRelatedToNotification.toLowerCase().includes(term))
                      );
                    })
                    .map(renderNotificationItem)}
                </div>
              )}
            </TabsContent>
          </Tabs>
        </CardContent>
      </Card>

      {/* Notification Summary Dialog */}
      <Dialog open={showSummary} onOpenChange={setShowSummary}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>Notification Summary</DialogTitle>
            <DialogDescription>
              Summary of recent notification patterns and insights.
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

export default NotificationDashboard;
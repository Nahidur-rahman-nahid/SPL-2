"use client";

import React, { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { toast } from 'react-toastify';
import { format } from "date-fns";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Textarea } from "@/components/ui/textarea";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
  Calendar,
  Check,
  Clock,
  Circle,
  FileText,
  Folder,
  MessageCircle,
  MoreVertical,
  PlusCircle,
  Tag,
  Trash2,
  Users,
  X,
  Edit,
  Save,
  RefreshCcw,
  ListTodo,
  History,
} from "lucide-react";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import { Progress } from "@/components/ui/progress";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";

const TaskPage = ({ taskId }) => {
  const [task, setTask] = useState([]); // changed from 'task' to 'tasks'
  const [loading, setLoading] = useState(true);
  const [editMode, setEditMode] = useState(false);
  const [updatingTodo, setUpdatingTodo] = useState(false);
  const [newComment, setNewComment] = useState("");
  const [newTodo, setNewTodo] = useState("");
  const [newNote, setNewNote] = useState("");
  const router = useRouter();
  const [error, setError] = useState(null); 

  // Form state
  const [editedTask, setEditedTask] = useState({
    taskName: "",
    taskDescription: "",
    taskCategory: "",
    taskPriority: "",
    taskDeadline: "",
    taskGoal: "",
    taskCurrentProgress: 0,
  });

  useEffect(() => {
    const fetchTask = async () => {
      try {
        const taskName = localStorage.getItem("TimeWiseSelectedTaskName");
        const taskOwner = localStorage.getItem("TimeWiseSelectedTaskOwner");

        if (!taskName || !taskOwner) {
          console.error("No task details found in local storage.");
          setLoading(false);
          return;
        }

        const response = await fetch(
          `/api/tasks/details?taskName=${encodeURIComponent(
            taskName
          )}&taskOwner=${encodeURIComponent(taskOwner)}`
        );
        if (!response.ok) throw new Error("Failed to fetch task details");

        const data = await response.json();
        setTask(data);
      } catch (error) {
        console.error("Error fetching task details:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchTask();
  }, []);

  const handleSaveTask = async () => {
    try {
      const response = await fetch(`/api/tasks/${taskId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(editedTask),
      });

      if (!response.ok) throw new Error("Failed to update task");

      const updatedTask = await response.json();
      setTask(updatedTask);
      setEditMode(false);
    } catch (error) {
      console.error("Error updating task:", error);
    }
  };

  const handleAddComment = async () => {
    if (!newComment.trim()) return;

    try {
      const taskName = task.taskName;
      const taskOwner = task.taskOwner;
      const response = await fetch(`/api/tasks/comment/add`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          taskName: taskName,
          taskOwner: taskOwner,
          taskComment: newComment,
        }),
      });

      if (!response.ok) throw new Error("Failed to add comment");

      const updatedTask = await response.json();
      setTask(updatedTask);
      setNewComment("");
    } catch (error) {
      console.error("Error adding comment:", error);
    }
  };
  const handleAddNote = async () => {
    if (!newNote.trim()) return;

    try {
      const taskName = task.taskName;
      const taskOwner = task.taskOwner;

      const response = await fetch(`/api/tasks/note/add`, {
        // Correct API endpoint
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          taskName: taskName,
          taskOwner: taskOwner,
          taskNote: newNote, // Send 'noteText' to match backend
        }),
      });

      if (!response.ok) {
        const errorData = await response.json(); // Get error details from JSON
        throw new Error(errorData.error || "Failed to add note");
      }

      const updatedTask = await response.json();
      setTask(updatedTask);
      setNewNote("");
    } catch (error) {
      console.error("Error adding note:", error);
      // Handle error
    }
  };

  const handleAddTodo = async () => {
  if (!newTodo.trim()) return;

  try {
    
    const todoData = {
      taskName: task.taskName,
      taskOwner: task.taskOwner,
      taskTodo: newTodo
    };

    const response = await fetch('/api/tasks/todo/add', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(todoData),
    });
  
    if (!response.ok) {
      const errorData = await response.json();
      const errorMessage = errorData.error || errorData.message || 'Failed to add todo item';
      throw new Error(errorMessage);
    }
    
    const updatedTask = await response.json();
    setTask(updatedTask);
    setNewTodo("");
    
  } catch (error) {
    console.error("Error adding todo:", error);
    setError({
      message: error.message || 'An unexpected error occurred while adding your todo item.',
      actionRequired: 'Please try again or contact support if the problem persists.',
    });
    
    toast.error(`Failed to add todo: ${error.message}`, {
      position: "top-right",
      autoClose: 5000,
      hideProgressBar: false,
      closeOnClick: true,
      pauseOnHover: true,
    });
    
    // Log to monitoring service (if available)
    if (typeof errorLoggingService !== 'undefined') {
      errorLoggingService.logError('todo_creation_failure', error);
    }
  }
};
  const handleUpdateTaskTodoStatus = async (todo) => {
    if (updatingTodo) return;
    setUpdatingTodo(true);

    try {
      const response = await fetch("/api/tasks/todo/status", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          taskName: task.taskName,
          taskOwner: task.taskOwner,
          updatedTaskTodoStatus: {
            description: todo.description,
            status: todo.status === "Complete" ? "Incomplete" : "Complete",
          },
        }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || "Failed to update todo status");
      }

      const updatedTask = await response.json();
      setTask(updatedTask);
    } catch (error) {
      console.error("Error updating todo status:", error);
    } finally {
      setUpdatingTodo(false);
    }
  };
  const getPriorityColor = (priority) => {
    switch (priority?.toLowerCase()) {
      case "high":
        return "text-red-500 bg-red-100 dark:bg-red-900/30";
      case "medium":
        return "text-yellow-500 bg-yellow-100 dark:bg-yellow-900/30";
      case "low":
        return "text-green-500 bg-green-100 dark:bg-green-900/30";
      default:
        return "text-gray-500 bg-gray-100 dark:bg-gray-900/30";
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-32 w-32 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  if (!task) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen">
        <h1 className="text-2xl font-bold mb-4">No tasks found</h1>
        <Button onClick={() => router.push("/tasks")}>Return to Tasks</Button>
      </div>
    );
  }

  return (
    <>
      <div className="container mx-auto p-6 space-y-6 dark:bg-gray-800 min-h-screen">
        <div className="flex justify-between items-center ">
          <div className="space-y-1">
            <h1 className="text-3xl font-bold text-blue-500 dark:text-white">
              {task.taskName}
            </h1>
            <p className="text-gray-500 dark:text-gray-400">
              Created by {task.taskOwner} on{" "}
              {format(new Date(task.taskCreationDate), "PPP")}
            </p>
          </div>
          <div className="flex space-x-2 dark:bg-gray-800">
            <TooltipProvider>
              <Tooltip>
                <TooltipTrigger asChild>
                  <Button
                    variant="outline"
                    size="icon"
                    onClick={() => setEditMode(!editMode)}
                  >
                    {editMode ? <X /> : <Edit />}
                  </Button>
                </TooltipTrigger>
                <TooltipContent>
                  {editMode ? "Cancel Editing" : "Edit Task"}
                </TooltipContent>
              </Tooltip>
            </TooltipProvider>
            {editMode && (
              <TooltipProvider>
                <Tooltip>
                  <TooltipTrigger asChild>
                    <Button
                      variant="default"
                      size="icon"
                      onClick={handleSaveTask}
                    >
                      <Save />
                    </Button>
                  </TooltipTrigger>
                  <TooltipContent>Save Changes</TooltipContent>
                </Tooltip>
              </TooltipProvider>
            )}
          </div>
        </div>

        <div className="grid grid-cols-3 gap-4">
          <div className="col-span-2 space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Task Details</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {editMode ? (
                    <>
                      <div className="space-y-2">
                        <Label htmlFor="taskName">Task Name</Label>
                        <Input
                          id="taskName"
                          value={editedTask.taskName}
                          onChange={(e) =>
                            setEditedTask({
                              ...editedTask,
                              taskName: e.target.value,
                            })
                          }
                        />
                      </div>
                      <div className="space-y-2 dark:bg-gray-600">
                        <Label htmlFor="taskDescription">Description</Label>
                        <Textarea
                          id="taskDescription"
                          value={editedTask.taskDescription}
                          onChange={(e) =>
                            setEditedTask({
                              ...editedTask,
                              taskDescription: e.target.value,
                            })
                          }
                        />
                      </div>
                      <div className="grid grid-cols-2 gap-4">
                        <div className="space-y-2">
                          <Label htmlFor="taskCategory">Category</Label>
                          <Select
                            value={editedTask.taskCategory}
                            onValueChange={(value) =>
                              setEditedTask({
                                ...editedTask,
                                taskCategory: value,
                              })
                            }
                          >
                            <SelectTrigger>
                              <SelectValue placeholder="Select category" />
                            </SelectTrigger>
                            <SelectContent>
                              <SelectItem value="Development">
                                Development
                              </SelectItem>
                              <SelectItem value="Design">Design</SelectItem>
                              <SelectItem value="Marketing">
                                Marketing
                              </SelectItem>
                              <SelectItem value="Research">Research</SelectItem>
                            </SelectContent>
                          </Select>
                        </div>
                        <div className="space-y-2">
                          <Label htmlFor="taskPriority">Priority</Label>
                          <Select
                            value={editedTask.taskPriority}
                            onValueChange={(value) =>
                              setEditedTask({
                                ...editedTask,
                                taskPriority: value,
                              })
                            }
                          >
                            <SelectTrigger>
                              <SelectValue placeholder="Select priority" />
                            </SelectTrigger>
                            <SelectContent>
                              <SelectItem value="High">High</SelectItem>
                              <SelectItem value="Medium">Medium</SelectItem>
                              <SelectItem value="Low">Low</SelectItem>
                            </SelectContent>
                          </Select>
                        </div>
                      </div>
                    </>
                  ) : (
                    <>
                      <div className="flex items-center space-x-2">
                        <Badge className={getPriorityColor(task.taskPriority)}>
                          {task.taskPriority} Priority
                        </Badge>
                        <Badge variant="outline">
                          <Folder className="w-4 h-4 mr-1" />
                          {task.taskCategory}
                        </Badge>
                      </div>
                      <p className="text-gray-600 dark:text-gray-300">
                        {task.taskDescription}
                      </p>
                    </>
                  )}
                </div>
              </CardContent>
            </Card>

            <Tabs defaultValue="comments" className="w-full">
              <TabsList className="grid w-full grid-cols-4">
                <TabsTrigger value="comments">
                  <MessageCircle className="w-4 h-4 mr-2" />
                  Comments
                </TabsTrigger>
                <TabsTrigger value="todos">
                  <ListTodo className="w-4 h-4 mr-2" />
                  Todos
                </TabsTrigger>
                <TabsTrigger value="notes">
                  <FileText className="w-4 h-4 mr-2" />
                  Notes
                </TabsTrigger>
                <TabsTrigger value="history">
                  <History className="w-4 h-4 mr-2" />
                  History
                </TabsTrigger>
              </TabsList>

              <TabsContent value="comments">
                <Card>
                  <CardHeader>
                    <CardTitle>Comments</CardTitle>
                    <CardDescription>
                      Discussion about this task
                    </CardDescription>
                  </CardHeader>
                  <CardContent>
                    <div className="space-y-4">
                      <div className="flex space-x-2">
                        <Input
                          placeholder="Add a comment..."
                          value={newComment}
                          onChange={(e) => setNewComment(e.target.value)}
                        />
                        <Button onClick={handleAddComment}>Post</Button>
                      </div>
                      <ScrollArea className="h-[400px] pr-4">
                        {task.taskComments?.map((comment, index) => (
                          <div
                            key={index}
                            className="flex space-x-3 mb-4 p-3 rounded-lg bg-teal-200 dark:bg-black"
                          >
                            <Avatar>
                              <AvatarFallback>
                                {comment.userName.substring(0, 2).toUpperCase()}
                              </AvatarFallback>
                            </Avatar>
                            <div className="flex-1">
                              <div className="flex justify-between items-center mb-1">
                                <span className="font-semibold dark:text-white">
                                  {comment.userName}
                                </span>
                                <span className="text-sm text-gray-500">
                                  {format(new Date(comment.timestamp), "PPp")}
                                </span>
                              </div>
                              <p className="text-gray-600 dark:text-gray-300">
                                {comment.commentText}
                              </p>
                            </div>
                          </div>
                        ))}
                      </ScrollArea>
                    </div>
                  </CardContent>
                </Card>
              </TabsContent>

              <TabsContent value="todos">
                <Card>
                  <CardHeader>
                    <CardTitle>Todo List</CardTitle>
                    <CardDescription>Track subtasks and todos</CardDescription>
                  </CardHeader>
                  <CardContent>
                    <div className="space-y-4">
                      <div className="flex space-x-2">
                        <Input
                          placeholder="Add a todo..."
                          value={newTodo}
                          onChange={(e) => setNewTodo(e.target.value)}
                        />
                        <Button onClick={handleAddTodo}>Add</Button>
                      </div>
                      <ScrollArea className="h-[400px] pr-4">
                        {task.taskTodos &&
                          task.taskTodos.map((todo) => (
                            <div
                              key={todo.description}
                              className="flex items-center justify-between py-2"
                            >
                              {" "}
                              {/* Added justify-between and padding */}
                              <span className="text-sm">
                                {" "}
                                {/* Display the todo description */}
                                {todo.description}
                              </span>
                              <Button
                                variant="ghost"
                                size="icon"
                                onClick={() => handleUpdateTaskTodoStatus(todo)}
                                disabled={updatingTodo}
                              >
                                {todo.status === "Complete" ? (
                                  <Check className="h-4 w-4 text-green-500" /> // Green checkmark
                                ) : (
                                  <Circle className="h-4 w-4 text-gray-400" /> // Gray circle for incomplete
                                  // Or <X className="h-4 w-4 text-red-500" /> for a red cross
                                )}
                              </Button>
                            </div>
                          ))}
                      </ScrollArea>
                    </div>
                  </CardContent>
                </Card>
              </TabsContent>

              <TabsContent value="notes">
                <Card>
                  <CardHeader>
                     <CardTitle>Notes</CardTitle>
                    {" "}
                    <CardDescription>
                   Personal notes and reminders
                      {" "}
                    </CardDescription>
                    {" "}
                  </CardHeader>
                  <CardContent>
                    <div className="space-y-4">
                      <div className="flex space-x-2">
                        <Textarea
                          placeholder="Add a note..."
                          value={newNote}
                          onChange={(e) => setNewNote(e.target.value)}
                        />
                        <Button className="self-start" onClick={handleAddNote}>
                          Add Note
                        </Button>
                      </div>
                      <ScrollArea className="h-[400px] pr-4">
                        {task.taskNotes &&
                          Object.entries(task.taskNotes).map(
                            ([userName, notes]) => (
                              <div key={userName} className="mb-6">
                                <h4 className="font-semibold mb-2 dark:text-white">
                                  {userName}'s Notes
                                </h4>
                                {notes.map((note, index) => (
                                  <div
                                    key={index}
                                    className="mb-2 p-3 rounded-lg bg-teal-200 dark:bg-gray-800"
                                  >
                                    <div className="flex justify-between items-center mb-1">
                                      <span className="text-sm text-gray-500">
                                        {format(
                                          new Date(note.timestamp),
                                          "PPp"
                                        )}
                                      </span>
                                    </div>
                                    <p className="text-gray-900 dark:text-gray-300">
                                      {note.noteText}
                                    </p>
                                  </div>
                                ))}
                              </div>
                            )
                          )}
                      </ScrollArea>
                    </div>
                  </CardContent>
                </Card>
              </TabsContent>
              <TabsContent value="history">
                <Card>
                  <CardHeader>
                    <CardTitle>Modification History</CardTitle>
                    <CardDescription>
                      Track all changes made to this task
                    </CardDescription>
                  </CardHeader>
                  <CardContent>
                    <ScrollArea className="h-[400px] pr-4">
                      {task.taskModificationHistory?.map(
                        (modification, index) => (
                          <div
                            key={index}
                            className="mb-4 p-3 rounded-lg bg-teal-200 dark:bg-black"
                          >
                            <div className="flex justify-between items-center mb-2">
                              <span className="font-semibold dark:text-white">
                                {modification.updatedBy}
                              </span>
                              <span className="text-sm text-gray-500">
                                {format(
                                  new Date(modification.timestamp),
                                  "PPp"
                                )}
                              </span>
                            </div>
                            <p className="text-gray-600 dark:text-gray-300">
                              Changed{" "}
                              <span className="font-medium">
                                {modification.fieldName}
                              </span>{" "}
                              from{" "}
                              <span className="font-medium">
                                {String(modification.previousValue)}
                              </span>{" "}
                              to{" "}
                              <span className="font-medium">
                                {String(modification.newValue)}
                              </span>
                            </p>
                          </div>
                        )
                      )}
                    </ScrollArea>
                  </CardContent>
                </Card>
              </TabsContent>
            </Tabs>
          </div>

          <div className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Progress & Timeline</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-6">
                  {editMode ? (
                    <div className="space-y-2">
                      <Label htmlFor="taskProgress">Progress (%)</Label>
                      <Input
                        id="taskProgress"
                        type="number"
                        min="0"
                        max="100"
                        value={editedTask.taskCurrentProgress}
                        onChange={(e) =>
                          setEditedTask({
                            ...editedTask,
                            taskCurrentProgress: parseInt(e.target.value),
                          })
                        }
                      />
                    </div>
                  ) : (
                    <div className="space-y-2">
                      <div className="flex justify-between text-sm">
                        <span>Progress</span>
                        <span>{task.taskCurrentProgress}%</span>
                      </div>
                      <Progress value={task.taskCurrentProgress} />
                    </div>
                  )}

                  <div className="space-y-4">
                    <div className="flex items-center justify-between">
                      <span className="text-sm font-medium">Deadline</span>
                      {editMode ? (
                        <Input
                          type="datetime-local"
                          value={
                            editedTask.taskDeadline
                              ? new Date(editedTask.taskDeadline)
                                  .toISOString()
                                  .slice(0, 16)
                              : ""
                          }
                          onChange={(e) =>
                            setEditedTask({
                              ...editedTask,
                              taskDeadline: new Date(e.target.value),
                            })
                          }
                        />
                      ) : (
                        <span className="text-sm text-gray-500">
                          {task.taskDeadline
                            ? format(new Date(task.taskDeadline), "PPp")
                            : "No deadline set"}
                        </span>
                      )}
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Collaboration</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  <div>
                    <h4 className="text-sm font-medium mb-2">Owner</h4>
                    <div className="flex items-center space-x-2">
                      <Avatar>
                        <AvatarFallback>
                          {task.taskOwner.substring(0, 2).toUpperCase()}
                        </AvatarFallback>
                      </Avatar>
                      <span>{task.taskOwner}</span>
                    </div>
                  </div>

                  <div>
                    <h4 className="text-sm font-medium mb-2">Participants</h4>
                    <div className="flex flex-wrap gap-2">
                      {task.taskParticipants?.map((participant) => (
                        <div
                          key={participant}
                          className="flex items-center space-x-2 bg-gray-100 dark:bg-gray-800 rounded-full px-3 py-1"
                        >
                          <Avatar className="h-6 w-6">
                            <AvatarFallback className="text-xs">
                              {participant.substring(0, 2).toUpperCase()}
                            </AvatarFallback>
                          </Avatar>
                          <span className="text-sm">{participant}</span>
                        </div>
                      ))}
                    </div>
                  </div>

                  {editMode && (
                    <div className="pt-4">
                      <Button className="w-full" variant="outline">
                        <Users className="w-4 h-4 mr-2" />
                        Manage Team
                      </Button>
                    </div>
                  )}
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Settings</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {editMode ? (
                    <>
                      <div className="space-y-2">
                        <Label htmlFor="taskVisibility">Visibility</Label>
                        <Select
                          value={editedTask.taskVisibilityStatus}
                          onValueChange={(value) =>
                            setEditedTask({
                              ...editedTask,
                              taskVisibilityStatus: value,
                            })
                          }
                        >
                          <SelectTrigger>
                            <SelectValue placeholder="Select visibility" />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="Public">Public</SelectItem>
                            <SelectItem value="Private">Private</SelectItem>
                            <SelectItem value="Team">Team Only</SelectItem>
                          </SelectContent>
                        </Select>
                      </div>
                    </>
                  ) : (
                    <div className="flex items-center justify-between">
                      <span className="text-sm font-medium">Visibility</span>
                      <Badge variant="outline">
                        {task.taskVisibilityStatus}
                      </Badge>
                    </div>
                  )}

                  <Dialog>
                    <DialogTrigger asChild>
                      <Button variant="destructive" className="w-full">
                        <Trash2 className="w-4 h-4 mr-2" />
                        Delete Task
                      </Button>
                    </DialogTrigger>
                    <DialogContent>
                      <DialogHeader>
                        <DialogTitle>Delete Task</DialogTitle>
                        <DialogDescription>
                          Are you sure you want to delete this task? This action
                          cannot be undone.
                        </DialogDescription>
                      </DialogHeader>
                      <div className="flex justify-end space-x-2">
                        <Button variant="outline" onClick={() => {}}>
                          Cancel
                        </Button>
                        <Button
                          variant="destructive"
                          onClick={() => {
                            // Handle delete
                            router.push("/tasks");
                          }}
                        >
                          Delete
                        </Button>
                      </div>
                    </DialogContent>
                  </Dialog>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </>
  );
};

export default TaskPage;

"use client"

import { useState } from "react"
import { Bell, CheckCircle, AlertCircle, MessageSquare, Clock, X } from "lucide-react"

export function NotificationCenter() {
  const [notifications, setNotifications] = useState([
    {
      id: 1,
      type: "task-assigned",
      title: "New Task Assigned",
      message: "Foundation Excavation has been assigned to you",
      timestamp: "5 minutes ago",
      read: false,
      icon: "task",
    },
    {
      id: 2,
      type: "task-completed",
      title: "Task Completed",
      message: "John Smith marked Site Preparation as completed",
      timestamp: "1 hour ago",
      read: false,
      icon: "check",
    },
    {
      id: 3,
      type: "resource-low",
      title: "Low Resource Alert",
      message: "Steel Reinforcement stock is running low",
      timestamp: "3 hours ago",
      read: true,
      icon: "alert",
    },
    {
      id: 4,
      type: "project-update",
      title: "Project Status Update",
      message: "Downtown Office Complex status changed to Active",
      timestamp: "1 day ago",
      read: true,
      icon: "message",
    },
  ])

  const getIcon = (type: string) => {
    switch (type) {
      case "check":
        return <CheckCircle size={20} className="text-green-500" />
      case "alert":
        return <AlertCircle size={20} className="text-accent" />
      case "message":
        return <MessageSquare size={20} className="text-blue-500" />
      case "task":
        return <Clock size={20} className="text-primary" />
      default:
        return <Bell size={20} className="text-muted-foreground" />
    }
  }

  const deleteNotification = (id: number) => {
    setNotifications(notifications.filter((n) => n.id !== id))
  }

  return (
    <div className="min-h-screen bg-background p-8">
      <div className="max-w-2xl mx-auto">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-foreground">Notifications</h1>
          <p className="text-muted-foreground mt-2">Stay updated on all system events and activities</p>
        </div>

        {/* Notification List */}
        <div className="space-y-3">
          {notifications.map((notification) => (
            <div
              key={notification.id}
              className={`card-elevated p-4 flex items-start gap-4 transition-all hover:shadow-md ${
                !notification.read ? "border-l-4 border-accent" : ""
              }`}
            >
              <div className="flex-shrink-0 mt-1">{getIcon(notification.icon)}</div>

              <div className="flex-1">
                <h3 className="font-semibold text-foreground">{notification.title}</h3>
                <p className="text-sm text-muted-foreground mt-1">{notification.message}</p>
                <span className="text-xs text-muted-foreground mt-2 block">{notification.timestamp}</span>
              </div>

              <button
                onClick={() => deleteNotification(notification.id)}
                className="flex-shrink-0 p-2 hover:bg-muted rounded transition-colors"
              >
                <X size={18} className="text-muted-foreground hover:text-foreground" />
              </button>
            </div>
          ))}
        </div>

        {/* Empty State */}
        {notifications.length === 0 && (
          <div className="card-elevated p-12 text-center">
            <Bell size={48} className="mx-auto text-muted-foreground mb-4" />
            <h3 className="text-lg font-semibold text-foreground mb-2">All Caught Up</h3>
            <p className="text-muted-foreground">You have no new notifications at this time</p>
          </div>
        )}
      </div>
    </div>
  )
}

"use client"

import type React from "react"

import { useApp } from "@/lib/app-context"
import { useState, useEffect } from "react"
import { MessageSquare, Trash2, Plus, Send, ChevronDown, ChevronUp } from "lucide-react"

interface Comment {
  id: string
  author: string
  content: string
  createdAt: string
}

export function AnnouncementBoard() {
  const { announcements, addAnnouncement, deleteAnnouncement } = useApp()
  const [showForm, setShowForm] = useState(false)
  const [expandedAnnouncement, setExpandedAnnouncement] = useState<string | null>(null)
  const [comments, setComments] = useState<{ [key: string]: Comment[] }>({})
  const [commentForm, setCommentForm] = useState({ author: "", content: "" })
  const [formData, setFormData] = useState({
    title: "",
    message: "",
    priority: "normal" as const,
  })

  const API_BASE = "http://localhost:8080/api"

  // Fetch comments for expanded announcement
  const fetchComments = async (announcementId: string) => {
    try {
      const response = await fetch(`${API_BASE}/announcements/${announcementId}/comments`)
      if (response.ok) {
        const data = await response.json()
        setComments(prev => ({ ...prev, [announcementId]: data }))
      }
    } catch (error) {
      console.error("Failed to fetch comments:", error)
    }
  }

  const handleAddAnnouncement = (e: React.FormEvent) => {
    e.preventDefault()
    const newAnnouncement = {
      title: formData.title,
      content: formData.message,
      priority: formData.priority,
      author: "Current User"
    }
    addAnnouncement(newAnnouncement)
    setFormData({ title: "", message: "", priority: "normal" })
    setShowForm(false)
  }

  const handleToggleExpand = (announcementId: string) => {
    if (expandedAnnouncement === announcementId) {
      setExpandedAnnouncement(null)
    } else {
      setExpandedAnnouncement(announcementId)
      if (!comments[announcementId]) {
        fetchComments(announcementId)
      }
    }
  }

  const handleAddComment = async (e: React.FormEvent, announcementId: string) => {
    e.preventDefault()
    if (!commentForm.author.trim() || !commentForm.content.trim()) return

    try {
      const response = await fetch(`${API_BASE}/announcements/${announcementId}/comments`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          author: commentForm.author,
          content: commentForm.content
        })
      })

      if (response.ok) {
        setCommentForm({ author: "", content: "" })
        fetchComments(announcementId)
      }
    } catch (error) {
      console.error("Failed to add comment:", error)
    }
  }

  const handleDeleteComment = async (commentId: string, announcementId: string) => {
    try {
      const response = await fetch(`${API_BASE}/announcements/comments/${commentId}`, {
        method: "DELETE"
      })

      if (response.ok) {
        fetchComments(announcementId)
      }
    } catch (error) {
      console.error("Failed to delete comment:", error)
    }
  }

  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case "critical":
        return "border-l-4 border-red-500 bg-red-50/50"
      case "high":
        return "border-l-4 border-amber-500 bg-amber-50/50"
      case "normal":
        return "border-l-4 border-slate-400 bg-slate-50/50"
      default:
        return ""
    }
  }

  const getPriorityBadge = (priority: string) => {
    switch (priority) {
      case "critical":
        return "bg-red-100 text-red-800"
      case "high":
        return "bg-amber-100 text-amber-800"
      default:
        return "bg-slate-100 text-slate-800"
    }
  }

  const formatDate = (dateString: string) => {
    const date = new Date(dateString)
    return date.toLocaleString()
  }

  return (
    <div className="min-h-screen bg-background p-8">
      <div className="mb-8">
        <div className="flex justify-between items-start mb-6">
          <div>
            <h1 className="text-3xl font-bold text-foreground">Announcement Board</h1>
            <p className="text-muted-foreground mt-2">Real-time communication with team and job sites</p>
          </div>
          <button
            onClick={() => setShowForm(!showForm)}
            className="px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors font-medium flex items-center gap-2"
          >
            <Plus size={20} /> New Announcement
          </button>
        </div>

        {showForm && (
          <form onSubmit={handleAddAnnouncement} className="bg-card p-6 rounded-lg border border-border mb-6 space-y-4">
            <input
              type="text"
              placeholder="Announcement title"
              value={formData.title}
              onChange={(e) => setFormData({ ...formData, title: e.target.value })}
              required
              className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
            />
            <textarea
              placeholder="Message content"
              value={formData.message}
              onChange={(e) => setFormData({ ...formData, message: e.target.value })}
              required
              rows={3}
              className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
            />
            <select
              value={formData.priority}
              onChange={(e) => setFormData({ ...formData, priority: e.target.value as any })}
              className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
            >
              <option value="normal">Normal</option>
              <option value="high">High</option>
              <option value="critical">Critical</option>
            </select>
            <div className="flex gap-2">
              <button
                type="submit"
                className="px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors font-medium"
              >
                Post Announcement
              </button>
              <button
                type="button"
                onClick={() => setShowForm(false)}
                className="px-4 py-2 border border-slate-300 rounded-lg text-foreground hover:bg-slate-50 transition-colors font-medium"
              >
                Cancel
              </button>
            </div>
          </form>
        )}
      </div>

      <div className="space-y-4">
        {announcements.map((announcement) => {
          const isExpanded = expandedAnnouncement === announcement.id
          const announcementComments = comments[announcement.id] || []

          return (
            <div
              key={announcement.id}
              className={`bg-card rounded-lg shadow-sm ${getPriorityColor(announcement.priority)}`}
            >
              {/* Announcement Header */}
              <div className="p-6">
                <div className="flex justify-between items-start mb-2">
                  <div className="flex-1">
                    <h3 className="text-xl font-semibold text-foreground mb-1">{announcement.title}</h3>
                    <div className="flex items-center gap-2 text-sm text-muted-foreground mb-3">
                      <span>Posted by {announcement.author}</span>
                      <span>•</span>
                      <span>{new Date(announcement.datePosted).toLocaleDateString()}</span>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className={`px-3 py-1 rounded-full text-xs font-semibold ${getPriorityBadge(announcement.priority)}`}>
                      {announcement.priority.toUpperCase()}
                    </span>
                    <button
                      onClick={() => deleteAnnouncement(announcement.id)}
                      className="p-2 hover:bg-red-50 rounded-lg transition-colors"
                      title="Delete announcement"
                    >
                      <Trash2 size={18} className="text-red-600" />
                    </button>
                  </div>
                </div>
                <p className="text-foreground mb-4">{announcement.message || announcement.content}</p>

                {/* Toggle Comments Button */}
                <button
                  onClick={() => handleToggleExpand(announcement.id)}
                  className="flex items-center gap-2 text-sm text-primary hover:text-primary/80 font-medium"
                >
                  <MessageSquare size={16} />
                  {announcementComments.length} {announcementComments.length === 1 ? 'Comment' : 'Comments'}
                  {isExpanded ? <ChevronUp size={16} /> : <ChevronDown size={16} />}
                </button>
              </div>

              {/* Comments Section */}
              {isExpanded && (
                <div className="border-t border-border p-6 bg-muted/20">
                  {/* Comment List */}
                  <div className="space-y-3 mb-4">
                    {announcementComments.length > 0 ? (
                      announcementComments.map((comment) => (
                        <div key={comment.id} className="bg-card p-3 rounded-lg border border-border">
                          <div className="flex justify-between items-start mb-1">
                            <div className="flex items-center gap-2">
                              <span className="font-medium text-sm text-foreground">{comment.author}</span>
                              <span className="text-xs text-muted-foreground">{formatDate(comment.createdAt)}</span>
                            </div>
                            <button
                              onClick={() => handleDeleteComment(comment.id, announcement.id)}
                              className="p-1 hover:bg-red-50 rounded transition-colors"
                              title="Delete comment"
                            >
                              <Trash2 size={14} className="text-red-600" />
                            </button>
                          </div>
                          <p className="text-sm text-foreground">{comment.content}</p>
                        </div>
                      ))
                    ) : (
                      <p className="text-sm text-muted-foreground text-center py-4">No comments yet. Be the first to comment!</p>
                    )}
                  </div>

                  {/* Add Comment Form */}
                  <form onSubmit={(e) => handleAddComment(e, announcement.id)} className="space-y-3">
                    <input
                      type="text"
                      placeholder="Your name"
                      value={commentForm.author}
                      onChange={(e) => setCommentForm({ ...commentForm, author: e.target.value })}
                      required
                      className="w-full px-3 py-2 text-sm border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
                    />
                    <div className="flex gap-2">
                      <input
                        type="text"
                        placeholder="Add a comment..."
                        value={commentForm.content}
                        onChange={(e) => setCommentForm({ ...commentForm, content: e.target.value })}
                        required
                        className="flex-1 px-3 py-2 text-sm border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
                      />
                      <button
                        type="submit"
                        className="px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors flex items-center gap-2"
                      >
                        <Send size={16} />
                        Send
                      </button>
                    </div>
                  </form>
                </div>
              )}
            </div>
          )
        })}
      </div>
    </div>
  )
}

"use client"

import { useApp } from "@/lib/app-context"
import { Clock, AlertCircle, CheckCircle, Plus } from "lucide-react"
import { useState } from "react"

export function TaskManager({
  onNewTask,
  onEditTask,
  onDeleteTask,
}: {
  onNewTask: () => void
  onEditTask: (taskId: string) => void
  onDeleteTask: (taskId: string) => void
}) {
  const { tasks, projects, updateTask } = useApp()
  const [filter, setFilter] = useState("all")

  const handleCompleteTask = async (taskId: string) => {
    try {
      await updateTask(taskId, { status: "Completed" })
    } catch (err) {
      console.error("Failed to mark task as completed", err)
    }
  }

  const getStatusIcon = (status: string) => {
    switch (status) {
      case "in progress":
      case "In Progress":
        return <Clock size={16} className="text-blue-500" />
      case "pending":
      case "Pending":
        return <AlertCircle size={16} className="text-yellow-500" />
      case "completed":
      case "Completed":
        return <CheckCircle size={16} className="text-green-500" />
      default:
        return null
    }
  }

  const getPriorityColor = (priority: string) => {
    switch (priority.toLowerCase()) {
      case "critical":
        return "bg-red-600 text-white"
      case "high":
        return "bg-red-100 text-red-800"
      case "medium":
        return "bg-blue-100 text-blue-800"
      case "low":
        return "bg-green-100 text-green-800"
      default:
        return "bg-slate-100 text-slate-800"
    }
  }

  const getProjectName = (projectId: string) => {
    return projects.find((p) => p.id === projectId)?.name || "Unknown Project"
  }

  const filteredTasks = tasks.filter((task) => {
    if (filter === "all") return true
    return task.status === filter
  })

  return (
    <div className="min-h-screen bg-background p-8">
      <div className="mb-8">
        <div className="flex justify-between items-start mb-6">
          <div>
            <h1 className="text-3xl font-bold text-foreground">Task Management</h1>
            <p className="text-muted-foreground mt-2">Track and manage all project tasks</p>
          </div>
          <button
            onClick={onNewTask}
            className="px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors font-medium flex items-center gap-2"
          >
            <Plus size={20} /> New Task
          </button>
        </div>

        <div className="flex gap-2 flex-wrap">
          {["All", "Pending", "In Progress", "Completed"].map((f) => (
            <button
              key={f}
              onClick={() => setFilter(f === "All" ? "all" : f)} // Corrected filter logic
              className={`px-4 py-2 rounded-lg border transition-colors text-sm font-medium ${(f === "All" && filter === "all") || filter === f
                ? "bg-primary text-primary-foreground border-primary"
                : "border-border text-foreground hover:bg-muted"
                }`}
            >
              {f}
            </button>
          ))}
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {filteredTasks.map((task) => (
          <div key={task.id} className="bg-card p-6 hover:shadow-lg transition-shadow rounded-lg border border-border">
            <div className="flex justify-between items-start mb-4">
              <div className="flex-1">
                <div className="flex items-start gap-3">
                  {getStatusIcon(task.status)}
                  <div>
                    <h3 className="font-semibold text-foreground text-lg">{task.name}</h3>
                    <p className="text-sm text-muted-foreground">{getProjectName(task.projectId)}</p>
                  </div>
                </div>
              </div>
              <span className={`px-2 py-1 rounded text-xs font-medium ${getPriorityColor(task.priority)}`}>
                {task.priority ? task.priority.charAt(0).toUpperCase() + task.priority.slice(1) : "Normal"}
              </span>
            </div>

            <div className="space-y-3 mb-4">
              <div className="flex items-center gap-2 text-sm">
                <span className="text-muted-foreground">Assigned to:</span>
                <span className="font-medium text-foreground">{task.assignee}</span>
              </div>
              <div className="flex items-center gap-2 text-sm">
                <span className="text-muted-foreground">Due:</span>
                <span className="font-medium text-foreground">{task.dueDate}</span>
              </div>
              {task.description && <p className="text-sm text-muted-foreground">{task.description}</p>}
            </div>

            <div className="pt-4 border-t border-border flex gap-2">
              <button
                onClick={() => onEditTask(task.id)}
                className="flex-1 px-3 py-2 text-sm font-medium rounded-lg border border-border text-foreground hover:bg-muted transition-colors"
              >
                Edit
              </button>
              {task.status !== "Completed" && (
                <button
                  onClick={() => handleCompleteTask(task.id)}
                  className="flex-1 px-3 py-2 text-sm font-medium rounded-lg border border-green-200 text-green-700 hover:bg-green-50 transition-colors"
                >
                  Mark Complete
                </button>
              )}
              <button
                onClick={() => onDeleteTask(task.id)}
                className="flex-1 px-3 py-2 text-sm font-medium rounded-lg border border-red-200 text-red-700 hover:bg-red-50 transition-colors"
              >
                Delete
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

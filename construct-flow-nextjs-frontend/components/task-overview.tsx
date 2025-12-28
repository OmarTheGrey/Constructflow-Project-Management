"use client"

import { useApp } from "@/lib/app-context"

export function TaskOverview() {
  const { tasks, projects } = useApp()

  const getProjectName = (projectId: string) => {
    return projects.find((p) => p.id === projectId)?.name || "Unknown"
  }

  const getStatusBadge = (status: string) => {
    switch (status.toLowerCase()) {
      case "in progress":
        return "bg-blue-100 text-blue-800"
      case "pending":
        return "bg-yellow-100 text-yellow-800"
      case "completed":
        return "bg-green-100 text-green-800"
      default:
        return "bg-slate-100 text-slate-800"
    }
  }

  const formatDate = (dateString: string) => {
    if (!dateString) return "N/A"
    const date = new Date(dateString)
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
  }

  // Filter tasks by Critical priority
  const criticalTasks = tasks
    .filter((t) => t.priority?.toLowerCase() === "critical")
    .slice(0, 6)

  return (
    <div className="bg-card p-6 rounded-lg border border-border">
      <h3 className="text-lg font-bold text-foreground mb-6">Critical Tasks</h3>
      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-border">
              <th className="text-left py-3 px-4 font-semibold text-muted-foreground text-xs uppercase">Task</th>
              <th className="text-left py-3 px-4 font-semibold text-muted-foreground text-xs uppercase">Project</th>
              <th className="text-left py-3 px-4 font-semibold text-muted-foreground text-xs uppercase">Created</th>
              <th className="text-left py-3 px-4 font-semibold text-muted-foreground text-xs uppercase">Due Date</th>
              <th className="text-left py-3 px-4 font-semibold text-muted-foreground text-xs uppercase">Status</th>
            </tr>
          </thead>
          <tbody>
            {criticalTasks.length > 0 ? (
              criticalTasks.map((task) => (
                <tr key={task.id} className="border-b border-border hover:bg-muted/50 transition-colors">
                  <td className="py-3 px-4 font-medium text-foreground">{task.name}</td>
                  <td className="py-3 px-4 text-muted-foreground text-sm">{getProjectName(task.projectId)}</td>
                  <td className="py-3 px-4 text-muted-foreground text-xs">{formatDate(task.createdAt || '')}</td>
                  <td className="py-3 px-4 text-muted-foreground text-xs">{formatDate(task.dueDate)}</td>
                  <td className="py-3 px-4">
                    <span className={`px-3 py-1 rounded-full text-xs font-medium ${getStatusBadge(task.status)}`}>
                      {task.status.charAt(0).toUpperCase() + task.status.slice(1)}
                    </span>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={5} className="py-8 px-4 text-center text-muted-foreground">
                  No critical tasks found
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}

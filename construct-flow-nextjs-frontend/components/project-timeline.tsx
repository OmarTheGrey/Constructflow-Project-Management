"use client"

import { useApp } from "@/lib/app-context"

export function ProjectTimeline() {
  const { projects } = useApp()

  const getStatusColor = (progress: number, status: string) => {
    if (status === "On Hold") return "bg-slate-100 text-slate-800"
    if (progress >= 80) return "bg-green-100 text-green-800"
    if (progress >= 50) return "bg-blue-100 text-blue-800"
    return "bg-amber-100 text-amber-800"
  }

  return (
    <div className="bg-card p-6 rounded-lg border border-border">
      <h3 className="text-lg font-bold text-foreground mb-6">Active Projects</h3>
      <div className="space-y-4">
        {projects.map((project) => (
          <div key={project.id} className="pb-4 border-b border-border last:border-0 last:pb-0">
            <div className="flex justify-between items-start mb-2">
              <div>
                <h4 className="font-semibold text-foreground">{project.name}</h4>
                <p className="text-sm text-muted-foreground">{project.client}</p>
              </div>
              <span
                className={`px-2 py-1 rounded text-xs font-medium ${getStatusColor(project.progress, project.status)}`}
              >
                {project.status}
              </span>
            </div>
            <div className="space-y-2">
              <div className="w-full bg-muted rounded-full h-2">
                <div className="bg-primary h-2 rounded-full transition-all" style={{ width: `${project.progress}%` }} />
              </div>
              <div className="flex justify-between items-center">
                <span className="text-xs font-medium text-muted-foreground">{project.progress}% Complete</span>
                <span className="text-xs text-muted-foreground">
                  End: {new Date(project.endDate).toLocaleDateString()}
                </span>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

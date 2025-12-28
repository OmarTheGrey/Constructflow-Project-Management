"use client"

import { useApp } from "@/lib/app-context"
import { Card } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"

interface ProjectsSectionProps {
  onNewProject: () => void
  onEditProject: (id: string) => void
}

export function ProjectsSection({ onNewProject, onEditProject }: ProjectsSectionProps) {
  const { projects, deleteProject } = useApp()

  const getStatusColor = (status: string) => {
    switch (status) {
      case "Active":
        return "bg-green-100 text-green-800"
      case "Draft":
        return "bg-slate-100 text-slate-800"
      case "Completed":
        return "bg-blue-100 text-blue-800"
      case "On Hold":
        return "bg-amber-100 text-amber-800"
      default:
        return "bg-slate-100 text-slate-800"
    }
  }

  return (
    <div className="p-8">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold text-foreground">Projects</h1>
        <button
          onClick={onNewProject}
          className="px-6 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 font-medium"
        >
          New Project
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {projects.map((project) => (
          <Card key={project.id} className="p-6 hover:shadow-lg transition-shadow">
            <div className="flex justify-between items-start mb-4">
              <div className="flex-1">
                <h3 className="text-xl font-bold text-foreground">{project.name}</h3>
                <p className="text-sm text-muted-foreground">{project.client}</p>
              </div>
              <Badge className={getStatusColor(project.status)}>{project.status}</Badge>
            </div>

            <div className="space-y-3 mb-4">
              <div className="flex justify-between text-sm">
                <span className="text-muted-foreground">Location:</span>
                <span className="font-medium">{project.location}</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-muted-foreground">Budget:</span>
                <span className="font-medium">${(project.budget / 1000000).toFixed(1)}M</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-muted-foreground">Timeline:</span>
                <span className="font-medium">
                  {new Date(project.startDate).toLocaleDateString()} - {new Date(project.endDate).toLocaleDateString()}
                </span>
              </div>
            </div>

            <div className="mb-4">
              <div className="flex justify-between text-sm mb-2">
                <span className="text-muted-foreground">Progress</span>
                <span className="font-medium">{project.progress}%</span>
              </div>
              <div className="w-full bg-slate-200 rounded-full h-2">
                <div className="bg-primary h-2 rounded-full transition-all" style={{ width: `${project.progress}%` }} />
              </div>
            </div>

            <div className="flex gap-2">
              <button
                onClick={() => onEditProject(project.id)}
                className="flex-1 px-4 py-2 bg-slate-100 text-foreground rounded hover:bg-slate-200 text-sm font-medium"
              >
                Edit
              </button>
              <button
                onClick={() => deleteProject(project.id)}
                className="flex-1 px-4 py-2 bg-red-100 text-red-700 rounded hover:bg-red-200 text-sm font-medium"
              >
                Delete
              </button>
            </div>
          </Card>
        ))}
      </div>
    </div>
  )
}

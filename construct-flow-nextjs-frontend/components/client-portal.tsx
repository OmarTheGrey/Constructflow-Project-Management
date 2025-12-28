"use client"

import { useApp } from "@/lib/app-context"
import { ChevronRight, FileText, ImageIcon, TrendingUp } from "lucide-react"

export function ClientPortal() {
  const { projects, getProjectDocuments } = useApp()

  return (
    <div className="min-h-screen bg-background p-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-foreground">My Projects</h1>
        <p className="text-muted-foreground mt-2">Track your project progress and access documentation</p>
      </div>

      <div className="space-y-6">
        {projects.map((project) => {
          const projectDocs = getProjectDocuments(project.id)
          return (
            <div key={project.id} className="bg-card p-8 rounded-lg border border-border">
              <div className="flex justify-between items-start mb-6 pb-6 border-b border-border">
                <div>
                  <h2 className="text-2xl font-bold text-foreground">{project.name}</h2>
                  <p className="text-muted-foreground mt-1">{project.client}</p>
                </div>
                <div className="text-right">
                  <p className="text-sm font-semibold text-primary uppercase tracking-wide">Status</p>
                  <p className="text-lg font-bold text-foreground">{project.status}</p>
                </div>
              </div>

              <div className="mb-8">
                <div className="flex justify-between items-center mb-2">
                  <span className="text-sm font-semibold text-foreground flex items-center gap-2">
                    <TrendingUp size={16} className="text-primary" />
                    Overall Progress
                  </span>
                  <span className="text-2xl font-bold text-primary">{project.progress}%</span>
                </div>
                <div className="w-full bg-muted rounded-full h-3">
                  <div
                    className="bg-primary h-3 rounded-full transition-all"
                    style={{ width: `${project.progress}%` }}
                  />
                </div>
              </div>

              <div className="grid grid-cols-3 gap-6 mb-8 p-6 bg-muted/50 rounded-lg">
                <div>
                  <p className="text-sm text-muted-foreground uppercase font-semibold tracking-wide">
                    Project Timeline
                  </p>
                  <p className="text-sm font-semibold text-foreground mt-1">
                    {new Date(project.startDate).toLocaleDateString()} -{" "}
                    {new Date(project.endDate).toLocaleDateString()}
                  </p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground uppercase font-semibold tracking-wide">Budget</p>
                  <p className="text-lg font-semibold text-foreground mt-1">
                    ${(project.budget / 1000000).toFixed(1)}M
                  </p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground uppercase font-semibold tracking-wide">Documents</p>
                  <p className="text-3xl font-bold text-foreground mt-1">{projectDocs.length}</p>
                </div>
              </div>

              <div className="grid grid-cols-3 gap-4">
                <button className="p-4 border border-border rounded-lg hover:bg-muted transition-colors text-left group">
                  <div className="flex items-center justify-between mb-2">
                    <ImageIcon size={20} className="text-primary" />
                    <ChevronRight size={16} className="text-muted-foreground group-hover:text-foreground" />
                  </div>
                  <p className="font-semibold text-foreground">Progress Photos</p>
                  <p className="text-sm text-muted-foreground mt-1">View gallery</p>
                </button>

                <button className="p-4 border border-border rounded-lg hover:bg-muted transition-colors text-left group">
                  <div className="flex items-center justify-between mb-2">
                    <FileText size={20} className="text-primary" />
                    <ChevronRight size={16} className="text-muted-foreground group-hover:text-foreground" />
                  </div>
                  <p className="font-semibold text-foreground">Documents</p>
                  <p className="text-sm text-muted-foreground mt-1">{projectDocs.length} files</p>
                </button>

                <button className="p-4 border border-border rounded-lg hover:bg-muted transition-colors text-left group">
                  <div className="flex items-center justify-between mb-2">
                    <TrendingUp size={20} className="text-green-500" />
                    <ChevronRight size={16} className="text-muted-foreground group-hover:text-foreground" />
                  </div>
                  <p className="font-semibold text-foreground">Schedule</p>
                  <p className="text-sm text-muted-foreground mt-1">View timeline</p>
                </button>
              </div>
            </div>
          )
        })}
      </div>
    </div>
  )
}

"use client"

import { useApp } from "@/lib/app-context"
import { FileText, AlertCircle } from "lucide-react"
import { useState } from "react"
import { ReportModal } from "./report-modal"
import { ProjectTimeline } from "./project-timeline"
import { TaskOverview } from "./task-overview"
import { ResourceStatus } from "./resource-status"

export function Dashboard({ onNewProject }: { onNewProject: () => void }) {
  const { projects, tasks, resources } = useApp()
  const [showReport, setShowReport] = useState(false)

  const activeProjects = projects.filter((p) => p.status === "Active").length
  const onScheduleCount = projects.filter((p) => p.progress >= 70).length
  const activeTasks = tasks.filter((t) => t.status === "In Progress").length
  const overdueTasks = tasks.filter((t) => new Date(t.dueDate) < new Date()).length
  const totalBudget = projects.reduce((sum, p) => sum + p.budget, 0)
  const budgetHealth = 94

  return (
    <div className="p-8 space-y-8">
      {showReport && <ReportModal onClose={() => setShowReport(false)} />}
      <div className="flex justify-between items-start">
        <div>
          <h1 className="text-3xl font-bold text-foreground">Projects Dashboard</h1>
          <p className="text-muted-foreground mt-2">Real-time project management & tracking</p>
        </div>
        <div className="flex gap-3">
          <button
            onClick={() => setShowReport(true)}
            className="px-4 py-2 bg-slate-100 text-foreground border border-slate-200 rounded-lg hover:bg-slate-200 transition-colors font-medium flex items-center gap-2"
          >
            <FileText size={18} /> Generate Report
          </button>
          <button
            onClick={onNewProject}
            className="px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors font-medium"
          >
            + New Project
          </button>
        </div>
      </div>

      {/* Key Metrics */}
      <div className="grid grid-cols-4 gap-4">
        <div className="bg-card p-6 rounded-lg border border-border">
          <div className="stat-metric">
            <div className="text-primary text-sm font-semibold uppercase tracking-wide">Active Projects</div>
            <div className="text-3xl font-bold text-foreground mt-2">{activeProjects}</div>
            <div className="text-xs text-muted-foreground mt-2">of {projects.length} total</div>
          </div>
        </div>

        <div className="bg-card p-6 rounded-lg border border-border">
          <div className="stat-metric">
            <div className="text-primary text-sm font-semibold uppercase tracking-wide">On Schedule</div>
            <div className="text-3xl font-bold text-foreground mt-2">
              {projects.length > 0 ? Math.round((onScheduleCount / projects.length) * 100) : 0}%
            </div>
            <div className="text-xs text-muted-foreground mt-2">
              {onScheduleCount} of {projects.length} projects
            </div>
          </div>
        </div>

        <div className="bg-card p-6 rounded-lg border border-border">
          <div className="stat-metric">
            <div className="text-primary text-sm font-semibold uppercase tracking-wide">Active Tasks</div>
            <div className="text-3xl font-bold text-foreground mt-2">{activeTasks}</div>
            <div className="text-xs text-muted-foreground mt-2">{overdueTasks} overdue</div>
          </div>
        </div>

        <div className="bg-card p-6 rounded-lg border border-border">
          <div className="stat-metric">
            <div className="text-primary text-sm font-semibold uppercase tracking-wide">Total Budget</div>
            <div className="text-3xl font-bold text-foreground mt-2">${(totalBudget / 1000000).toFixed(1)}M</div>
            <div className="text-xs text-muted-foreground mt-2">{budgetHealth}% health</div>
          </div>
        </div>
      </div>

      {/* Main Content Grid */}
      <div className="grid grid-cols-3 gap-6">
        {/* Left: Project Timeline & Tasks */}
        <div className="col-span-2 space-y-6">
          <ProjectTimeline />
          <TaskOverview />
        </div>

        {/* Right: Resources & Alerts */}
        <div className="space-y-6">
          <ResourceStatus />

          {/* Alerts */}
          <div className="bg-card p-6 rounded-lg border border-border">
            <h3 className="text-lg font-bold text-foreground mb-4 flex items-center gap-2">
              <AlertCircle size={20} className="text-red-500" />
              Alerts ({overdueTasks})
            </h3>
            <div className="space-y-3">
              {overdueTasks > 0 && (
                <div className="p-3 bg-red-50 border border-red-200 rounded-md">
                  <p className="text-sm font-medium text-foreground">Overdue tasks</p>
                  <p className="text-xs text-muted-foreground mt-1">{overdueTasks} tasks past due date</p>
                </div>
              )}
              {resources.some((r) => (r.allocated / r.quantity) * 100 > 80) && (
                <div className="p-3 bg-amber-50 border border-amber-200 rounded-md">
                  <p className="text-sm font-medium text-foreground">Resource pressure</p>
                  <p className="text-xs text-muted-foreground mt-1">Some resources over 80% utilization</p>
                </div>
              )}
              {projects.some((p) => p.status === "On Hold") && (
                <div className="p-3 bg-slate-50 border border-slate-200 rounded-md">
                  <p className="text-sm font-medium text-foreground">Projects on hold</p>
                  <p className="text-xs text-muted-foreground mt-1">
                    {projects.filter((p) => p.status === "On Hold").length} project(s) paused
                  </p>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

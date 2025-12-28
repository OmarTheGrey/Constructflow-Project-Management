"use client"

import type React from "react"

import { useApp } from "@/lib/app-context"
import { useState, useMemo } from "react"
import { Calendar, MapPin, AlertCircle, Activity, Package, Briefcase } from "lucide-react"

export function DailyReport() {
  const { projects, tasks, resources, addDailyReport } = useApp()
  const [reportForm, setReportForm] = useState({
    date: "",
    projectId: projects[0]?.id || "",
    taskId: "",
    activities: "",
    issues: "",
    completionPercentage: 50,
  })

  // Filter changes by selected date
  const dailyChanges = useMemo(() => {
    if (!reportForm.date) return { tasks: [], resources: [], projects: [] }

    const selectedDate = new Date(reportForm.date).toDateString()

    return {
      tasks: tasks.filter(t => {
        const createdDate = t.createdAt ? new Date(t.createdAt).toDateString() : null
        const modifiedDate = t.lastModifiedAt ? new Date(t.lastModifiedAt).toDateString() : null
        return createdDate === selectedDate || modifiedDate === selectedDate
      }),
      resources: resources.filter(r => {
        const createdDate = r.createdAt ? new Date(r.createdAt).toDateString() : null
        const modifiedDate = r.lastModifiedAt ? new Date(r.lastModifiedAt).toDateString() : null
        return createdDate === selectedDate || modifiedDate === selectedDate
      }),
      projects: projects.filter(p => {
        const createdDate = p.createdAt ? new Date(p.createdAt).toDateString() : null
        const modifiedDate = p.lastModifiedAt ? new Date(p.lastModifiedAt).toDateString() : null
        return createdDate === selectedDate || modifiedDate === selectedDate
      })
    }
  }, [reportForm.date, tasks, resources, projects])

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()

    const newReport = {
      id: `dr-${Date.now()}`,
      projectId: reportForm.projectId,
      taskId: reportForm.taskId,
      date: reportForm.date,
      activities: reportForm.activities,
      issues: reportForm.issues,
      completionPercentage: reportForm.completionPercentage,
      photos: [],
      submittedBy: "Current User",
      changes: dailyChanges, // Include tracked changes
    }

    addDailyReport(newReport)
    setReportForm({
      date: "",
      projectId: projects[0]?.id || "",
      taskId: "",
      activities: "",
      issues: "",
      completionPercentage: 50,
    })
  }

  return (
    <div className="min-h-screen bg-background p-8">
      <div className="max-w-5xl mx-auto">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-foreground">Daily Progress Report</h1>
          <p className="text-muted-foreground mt-2">Document daily site activities and system changes</p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Report Form */}
          <div className="lg:col-span-2 bg-card p-8 rounded-lg border border-border">
            <form onSubmit={handleSubmit} className="space-y-6">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-semibold text-foreground mb-2 flex items-center gap-2">
                    <Calendar size={16} /> Report Date
                  </label>
                  <input
                    type="date"
                    value={reportForm.date}
                    onChange={(e) => setReportForm({ ...reportForm, date: e.target.value })}
                    required
                    className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
                  />
                </div>
                <div>
                  <label className="block text-sm font-semibold text-foreground mb-2 flex items-center gap-2">
                    <MapPin size={16} /> Project
                  </label>
                  <select
                    value={reportForm.projectId}
                    onChange={(e) => setReportForm({ ...reportForm, projectId: e.target.value })}
                    className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
                  >
                    {projects.map((p) => (
                      <option key={p.id} value={p.id}>
                        {p.name}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-sm font-semibold text-foreground mb-2">Activities Completed</label>
                <textarea
                  placeholder="Summarize the day's activities and progress..."
                  rows={3}
                  value={reportForm.activities}
                  onChange={(e) => setReportForm({ ...reportForm, activities: e.target.value })}
                  className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent resize-none"
                />
              </div>

              <div>
                <label className="block text-sm font-semibold text-foreground mb-2">Completion Percentage</label>
                <div className="flex items-center gap-4">
                  <input
                    type="range"
                    min="0"
                    max="100"
                    value={reportForm.completionPercentage}
                    onChange={(e) => setReportForm({ ...reportForm, completionPercentage: Number(e.target.value) })}
                    className="flex-1"
                  />
                  <span className="text-2xl font-bold text-primary w-12 text-right">
                    {reportForm.completionPercentage}%
                  </span>
                </div>
              </div>

              <div>
                <label className="block text-sm font-semibold text-foreground mb-2 flex items-center gap-2">
                  <AlertCircle size={16} className="text-amber-500" /> Issues & Concerns
                </label>
                <textarea
                  placeholder="Document any issues, delays, or concerns..."
                  rows={3}
                  value={reportForm.issues}
                  onChange={(e) => setReportForm({ ...reportForm, issues: e.target.value })}
                  className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent resize-none"
                />
              </div>

              <div className="flex gap-4 pt-4 border-t border-slate-200">
                <button
                  type="submit"
                  className="px-6 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors font-medium"
                >
                  Submit Report
                </button>
                <button
                  type="button"
                  className="px-6 py-2 border border-slate-300 rounded-lg text-foreground hover:bg-slate-50 transition-colors font-medium"
                >
                  Save Draft
                </button>
              </div>
            </form>
          </div>

          {/* Automatic Change Tracking */}
          <div className="space-y-4">
            <div className="bg-card p-4 rounded-lg border border-border">
              <h3 className="font-semibold text-foreground mb-3 flex items-center gap-2">
                <Activity size={18} className="text-primary" />
                System Changes Today
              </h3>
              {!reportForm.date ? (
                <p className="text-sm text-muted-foreground">Select a date to view changes</p>
              ) : (
                <div className="space-y-4">
                  {/* Projects */}
                  <div>
                    <div className="flex items-center gap-2 mb-2">
                      <Briefcase size={14} className="text-blue-500" />
                      <span className="text-sm font-medium">Projects ({dailyChanges.projects.length})</span>
                    </div>
                    {dailyChanges.projects.length > 0 ? (
                      <ul className="space-y-1">
                        {dailyChanges.projects.map(p => (
                          <li key={p.id} className="text-xs text-muted-foreground pl-5">
                            • {p.name}
                          </li>
                        ))}
                      </ul>
                    ) : (
                      <p className="text-xs text-muted-foreground pl-5">No changes</p>
                    )}
                  </div>

                  {/* Tasks */}
                  <div>
                    <div className="flex items-center gap-2 mb-2">
                      <Activity size={14} className="text-green-500" />
                      <span className="text-sm font-medium">Tasks ({dailyChanges.tasks.length})</span>
                    </div>
                    {dailyChanges.tasks.length > 0 ? (
                      <ul className="space-y-1">
                        {dailyChanges.tasks.map(t => (
                          <li key={t.id} className="text-xs text-muted-foreground pl-5">
                            • {t.name}
                          </li>
                        ))}
                      </ul>
                    ) : (
                      <p className="text-xs text-muted-foreground pl-5">No changes</p>
                    )}
                  </div>

                  {/* Resources */}
                  <div>
                    <div className="flex items-center gap-2 mb-2">
                      <Package size={14} className="text-purple-500" />
                      <span className="text-sm font-medium">Resources ({dailyChanges.resources.length})</span>
                    </div>
                    {dailyChanges.resources.length > 0 ? (
                      <ul className="space-y-1">
                        {dailyChanges.resources.map(r => (
                          <li key={r.id} className="text-xs text-muted-foreground pl-5">
                            • {r.name}
                          </li>
                        ))}
                      </ul>
                    ) : (
                      <p className="text-xs text-muted-foreground pl-5">No changes</p>
                    )}
                  </div>

                  <div className="pt-3 border-t border-border">
                    <p className="text-xs text-muted-foreground">
                      Total changes: <span className="font-semibold text-foreground">
                        {dailyChanges.projects.length + dailyChanges.tasks.length + dailyChanges.resources.length}
                      </span>
                    </p>
                  </div>
                </div>
              )}
            </div>

            <div className="bg-blue-50 dark:bg-blue-950/20 p-4 rounded-lg border border-blue-200 dark:border-blue-900">
              <p className="text-xs text-blue-700 dark:text-blue-300">
                <strong>Auto-tracked:</strong> Changes are automatically detected based on creation and modification timestamps.
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

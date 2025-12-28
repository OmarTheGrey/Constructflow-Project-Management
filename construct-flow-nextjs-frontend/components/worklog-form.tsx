"use client"

import type React from "react"

import { useState } from "react"
import { Clock, AlertCircle } from "lucide-react"

export function WorklogForm() {
  const [workLogs, setWorkLogs] = useState([{ id: 1, task: "Foundation Excavation", hours: 0, notes: "" }])

  const assignedTasks = ["Foundation Excavation", "Site Preparation", "Material Delivery Coordination"]

  const addWorkLog = () => {
    setWorkLogs([...workLogs, { id: Date.now(), task: "", hours: 0, notes: "" }])
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    const totalHours = workLogs.reduce((sum, log) => sum + log.hours, 0)
    if (totalHours > 12) {
      alert("Total hours cannot exceed 12 per day")
      return
    }
    console.log("Work logs submitted:", workLogs)
  }

  return (
    <div className="min-h-screen bg-background p-8">
      <div className="max-w-3xl mx-auto">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-foreground">Log Work Hours</h1>
          <p className="text-muted-foreground mt-2">Record your daily work hours by task</p>
        </div>

        <div className="card-elevated p-8 mb-6">
          <div className="flex items-center gap-3 p-4 bg-accent/10 border border-accent/20 rounded-lg mb-6">
            <AlertCircle size={20} className="text-accent" />
            <p className="text-sm text-foreground">Maximum 12 hours per day allowed</p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Work Log Entries */}
            <div className="space-y-4">
              {workLogs.map((log, idx) => (
                <div key={log.id} className="p-4 border border-border rounded-lg space-y-3">
                  <div className="grid grid-cols-3 gap-4">
                    <div>
                      <label className="block text-sm font-semibold text-foreground mb-2">Task</label>
                      <select className="w-full px-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary">
                        <option>Select task...</option>
                        {assignedTasks.map((task) => (
                          <option key={task} value={task}>
                            {task}
                          </option>
                        ))}
                      </select>
                    </div>
                    <div>
                      <label className="block text-sm font-semibold text-foreground mb-2 flex items-center gap-2">
                        <Clock size={16} /> Hours
                      </label>
                      <input
                        type="number"
                        min="0"
                        max="12"
                        step="0.5"
                        placeholder="0.0"
                        className="w-full px-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                        onChange={(e) => {
                          const newLogs = [...workLogs]
                          newLogs[idx].hours = Number.parseFloat(e.target.value) || 0
                          setWorkLogs(newLogs)
                        }}
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-semibold text-foreground mb-2">Date</label>
                      <input
                        type="date"
                        className="w-full px-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                      />
                    </div>
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-foreground mb-2">Notes</label>
                    <textarea
                      placeholder="What was accomplished?"
                      rows={2}
                      className="w-full px-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary resize-none"
                      onChange={(e) => {
                        const newLogs = [...workLogs]
                        newLogs[idx].notes = e.target.value
                        setWorkLogs(newLogs)
                      }}
                    />
                  </div>
                </div>
              ))}
            </div>

            {/* Add More Button */}
            <button
              type="button"
              onClick={addWorkLog}
              className="w-full py-2 border border-dashed border-border rounded-lg text-foreground hover:bg-muted transition-colors font-medium text-sm"
            >
              + Add Another Task
            </button>

            {/* Total Hours Display */}
            <div className="p-4 bg-muted rounded-lg flex justify-between items-center">
              <span className="font-semibold text-foreground">Total Hours Today:</span>
              <span className="text-2xl font-bold text-primary">
                {workLogs.reduce((sum, log) => sum + log.hours, 0).toFixed(1)} hrs
              </span>
            </div>

            {/* Actions */}
            <div className="flex gap-4 pt-4 border-t border-border">
              <button
                type="submit"
                className="px-6 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors font-medium"
              >
                Submit Work Log
              </button>
              <button
                type="button"
                className="px-6 py-2 border border-border rounded-lg text-foreground hover:bg-muted transition-colors font-medium"
              >
                Save Draft
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}

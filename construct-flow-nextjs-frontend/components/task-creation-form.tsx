"use client"

import type React from "react"

import { useState } from "react"
import { Calendar, AlertCircle } from "lucide-react"

export function TaskCreationForm({ onClose }: { onClose?: () => void }) {
  const [formData, setFormData] = useState({
    title: "",
    project: "",
    assignee: "",
    startDate: "",
    dueDate: "",
    priority: "medium",
    description: "",
    dependencies: [] as string[],
  })

  const projects = ["Downtown Office Complex", "Residential Tower A", "Shopping Mall Extension"]
  const assignees = ["John Smith", "Sarah Johnson", "Mike Chen", "Emma Davis"]

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    console.log("Task created:", formData)
    if (onClose) onClose()
  }

  return (
    <div className="min-h-screen bg-background p-8">
      <div className="max-w-2xl mx-auto">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-foreground">Create New Task</h1>
          <p className="text-muted-foreground mt-2">Define task details, timeline, and dependencies</p>
        </div>

        <div className="card-elevated p-8">
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Task Title */}
            <div>
              <label className="block text-sm font-semibold text-foreground mb-2">Task Title</label>
              <input
                type="text"
                placeholder="e.g., Foundation Excavation"
                className="w-full px-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                onChange={(e) => setFormData({ ...formData, title: e.target.value })}
              />
            </div>

            {/* Project & Assignee */}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-semibold text-foreground mb-2">Project</label>
                <select className="w-full px-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary">
                  <option>Select project...</option>
                  {projects.map((p) => (
                    <option key={p} value={p}>
                      {p}
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-semibold text-foreground mb-2">Assign To</label>
                <select className="w-full px-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary">
                  <option>Select assignee...</option>
                  {assignees.map((a) => (
                    <option key={a} value={a}>
                      {a}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* Dates */}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-semibold text-foreground mb-2 flex items-center gap-2">
                  <Calendar size={16} /> Start Date
                </label>
                <input
                  type="date"
                  className="w-full px-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                  onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                />
              </div>
              <div>
                <label className="block text-sm font-semibold text-foreground mb-2 flex items-center gap-2">
                  <Calendar size={16} /> Due Date
                </label>
                <input
                  type="date"
                  className="w-full px-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                  onChange={(e) => setFormData({ ...formData, dueDate: e.target.value })}
                />
              </div>
            </div>

            {/* Priority */}
            <div>
              <label className="block text-sm font-semibold text-foreground mb-2 flex items-center gap-2">
                <AlertCircle size={16} /> Priority
              </label>
              <div className="flex gap-4">
                {["low", "medium", "high", "critical"].map((p) => (
                  <label key={p} className="flex items-center gap-2 cursor-pointer">
                    <input
                      type="radio"
                      name="priority"
                      value={p}
                      checked={formData.priority === p}
                      onChange={(e) => setFormData({ ...formData, priority: e.target.value })}
                      className="w-4 h-4"
                    />
                    <span className="text-sm font-medium text-foreground capitalize">{p}</span>
                  </label>
                ))}
              </div>
            </div>

            {/* Description */}
            <div>
              <label className="block text-sm font-semibold text-foreground mb-2">Description</label>
              <textarea
                placeholder="Provide detailed task description..."
                rows={4}
                className="w-full px-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary resize-none"
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              />
            </div>

            {/* Actions */}
            <div className="flex gap-4 pt-4 border-t border-border">
              <button
                type="submit"
                className="px-6 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors font-medium"
              >
                Create Task
              </button>
              <button
                type="button"
                className="px-6 py-2 border border-border rounded-lg text-foreground hover:bg-muted transition-colors font-medium"
                onClick={onClose}
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}

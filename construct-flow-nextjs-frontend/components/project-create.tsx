"use client"

import type React from "react"

import { useState } from "react"
import { Calendar, MapPin, DollarSign } from "lucide-react"

export function ProjectCreate({ onClose }: { onClose?: () => void }) {
  const [formData, setFormData] = useState({
    name: "",
    client: "",
    location: "",
    startDate: "",
    endDate: "",
    budget: "",
    description: "",
  })

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    console.log("Project created:", formData)
    if (onClose) onClose()
  }

  return (
    <div className="min-h-screen bg-background p-8">
      <div className="max-w-2xl mx-auto">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-foreground">Create New Project</h1>
          <p className="text-muted-foreground mt-2">Set up a new construction project with core details</p>
        </div>

        <div className="card-elevated p-8">
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Project Name & Client */}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-semibold text-foreground mb-2">Project Name</label>
                <input
                  type="text"
                  placeholder="e.g., Downtown Office Complex"
                  className="w-full px-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                />
              </div>
              <div>
                <label className="block text-sm font-semibold text-foreground mb-2">Client Name</label>
                <input
                  type="text"
                  placeholder="e.g., TechCorp Inc."
                  className="w-full px-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                  onChange={(e) => setFormData({ ...formData, client: e.target.value })}
                />
              </div>
            </div>

            {/* Location */}
            <div>
              <label className="block text-sm font-semibold text-foreground mb-2 flex items-center gap-2">
                <MapPin size={16} /> Location
              </label>
              <input
                type="text"
                placeholder="Project location"
                className="w-full px-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                onChange={(e) => setFormData({ ...formData, location: e.target.value })}
              />
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
                  <Calendar size={16} /> End Date
                </label>
                <input
                  type="date"
                  className="w-full px-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                  onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                />
              </div>
            </div>

            {/* Budget */}
            <div>
              <label className="block text-sm font-semibold text-foreground mb-2 flex items-center gap-2">
                <DollarSign size={16} /> Initial Budget
              </label>
              <input
                type="number"
                placeholder="0.00"
                className="w-full px-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                onChange={(e) => setFormData({ ...formData, budget: e.target.value })}
              />
            </div>

            {/* Description */}
            <div>
              <label className="block text-sm font-semibold text-foreground mb-2">Project Description</label>
              <textarea
                placeholder="Provide project objectives and key details..."
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
                Create Project
              </button>
              <button
                type="button"
                className="px-6 py-2 border border-border rounded-lg text-foreground hover:bg-muted transition-colors font-medium"
                onClick={onClose}
              >
                Save as Draft
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}

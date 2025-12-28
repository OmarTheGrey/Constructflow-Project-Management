"use client"

import React from "react"

import { useState } from "react"
import { useApp } from "@/lib/app-context"
import type { Project } from "@/lib/types"

interface ProjectCreateModalProps {
  onClose: () => void
  projectId?: string
}

export function ProjectCreateModal({ onClose, projectId }: ProjectCreateModalProps) {
  const { addProject, updateProject, projects } = useApp()
  /* eslint-disable react-hooks/exhaustive-deps */
  const [formData, setFormData] = useState({
    name: "",
    client: "",
    location: "",
    startDate: "",
    endDate: "",
    budget: "",
    objectives: "",
    status: "Draft" as const,
  })

  // Load project data if editing
  React.useEffect(() => {
    if (projectId) {
      const project = projects.find(p => p.id === projectId)
      if (project) {
        setFormData({
          name: project.name || "",
          client: project.client || "",
          location: project.location || "",
          startDate: project.startDate ? project.startDate.split('T')[0] : "",
          endDate: project.endDate ? project.endDate.split('T')[0] : "",
          budget: project.budget ? project.budget.toString() : "",
          objectives: project.objectives || "",
          status: (project.status as any) || "Draft",
        })
      }
    }
  }, [projectId, projects])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    const projectData: Partial<Project> = {
      name: formData.name,
      client: formData.client,
      location: formData.location,
      startDate: formData.startDate,
      endDate: formData.endDate,
      budget: Number.parseFloat(formData.budget),
      objectives: formData.objectives,
      status: formData.status as any,
    }

    try {
      if (projectId) {
        await updateProject(projectId, projectData)
      } else {
        await addProject({
          ...projectData,
          id: `proj-${Date.now()}`,
          progress: 0,
          milestones: [],
        } as Project)
      }
      onClose()
    } catch (error) {
      console.error('Error saving project:', error)
    }
  }

  return (
    <div className="p-6 max-w-2xl">
      <h2 className="text-2xl font-bold mb-6 text-foreground">{projectId ? 'Edit Project' : 'Create New Project'}</h2>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Project Name</label>
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
              placeholder="Enter project name"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Client</label>
            <input
              type="text"
              name="client"
              value={formData.client}
              onChange={handleChange}
              required
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
              placeholder="Client name"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Location</label>
            <input
              type="text"
              name="location"
              value={formData.location}
              onChange={handleChange}
              required
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
              placeholder="Project location"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Budget</label>
            <input
              type="number"
              name="budget"
              value={formData.budget}
              onChange={handleChange}
              required
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
              placeholder="Budget in dollars"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Start Date</label>
            <input
              type="date"
              name="startDate"
              value={formData.startDate}
              onChange={handleChange}
              required
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">End Date</label>
            <input
              type="date"
              name="endDate"
              value={formData.endDate}
              onChange={handleChange}
              required
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
            />
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium text-foreground mb-1">Objectives</label>
          <textarea
            name="objectives"
            value={formData.objectives}
            onChange={handleChange}
            className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
            placeholder="Project objectives"
            rows={3}
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-foreground mb-1">Status</label>
          <select
            name="status"
            value={formData.status}
            onChange={handleChange}
            className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
          >
            <option>Draft</option>
            <option>Active</option>
            <option>On Hold</option>
          </select>
        </div>

        <div className="flex gap-3 pt-4">
          <button
            type="submit"
            className="flex-1 px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 font-medium"
          >
            {projectId ? 'Update Project' : 'Create Project'}
          </button>
          <button
            type="button"
            onClick={onClose}
            className="flex-1 px-4 py-2 bg-slate-100 text-foreground rounded-lg hover:bg-slate-200 font-medium"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  )
}

"use client"

import type React from "react"

import { useState } from "react"
import { useApp } from "@/lib/app-context"

interface DocumentUploadModalProps {
  onClose: () => void
}

export function DocumentUploadModal({ onClose }: DocumentUploadModalProps) {
  const { addDocument, projects } = useApp()
  const [formData, setFormData] = useState({
    projectId: projects[0]?.id || "",
    name: "",
    type: "blueprint" as const,
    folder: "blueprints",
  })

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const [selectedFile, setSelectedFile] = useState<File | null>(null)

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setSelectedFile(e.target.files[0])
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!selectedFile) {
      alert("Please select a file")
      return
    }

    const formDataToSubmit = new FormData()
    formDataToSubmit.append("file", selectedFile)
    formDataToSubmit.append("projectId", formData.projectId)
    formDataToSubmit.append("folder", formData.folder)
    formDataToSubmit.append("type", formData.type)
    // Name is part of file, but we can send custom name if needed. 
    // Backend uses original filename inside service. Let's rely on that or send 'name' too if we updated backend.
    // Backend DocumentService uses dto.getName which is null? 
    // Wait, backend Controller takes MultipartFile AND DTO? Only Multipart and Params.
    // My backend controller takes params: projectId, folder, type. It reads original filename.

    try {
      await addDocument(formDataToSubmit)
      onClose()
    } catch (err) {
      console.error("Failed to upload document", err)
    }
  }

  return (
    <div className="p-6 max-w-2xl">
      <h2 className="text-2xl font-bold mb-6 text-foreground">Upload Document</h2>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-foreground mb-1">Project</label>
          <select
            name="projectId"
            value={formData.projectId}
            onChange={handleChange}
            className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
          >
            {projects.map((p) => (
              <option key={p.id} value={p.id}>
                {p.name}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-foreground mb-1">Document Name</label>
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
            className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
            placeholder="e.g., Building Blueprints v2"
          />
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Document Type</label>
            <select
              name="type"
              value={formData.type}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
            >
              <option value="blueprint">Blueprint</option>
              <option value="contract">Contract</option>
              <option value="permit">Permit</option>
              <option value="specification">Specification</option>
              <option value="report">Report</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Folder</label>
            <select
              name="folder"
              value={formData.folder}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
            >
              <option value="blueprints">Blueprints</option>
              <option value="permits">Permits</option>
              <option value="contracts">Contracts</option>
              <option value="reports">Reports</option>
            </select>
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium text-foreground mb-1">File Upload</label>
          <input
            type="file"
            className="w-full px-3 py-2 border border-slate-300 rounded-lg"
            onChange={handleFileChange}
          />
        </div>

        <div className="flex gap-3 pt-4">
          <button
            type="submit"
            className="flex-1 px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 font-medium"
          >
            Upload Document
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

"use client"

import type React from "react"

import { useState } from "react"
import { useApp } from "@/lib/app-context"

interface ResourceRequestModalProps {
  onClose: () => void
}

export function ResourceRequestModal({ onClose }: ResourceRequestModalProps) {
  const [formData, setFormData] = useState({
    resourceType: "material",
    resourceName: "",
    quantity: "",
    priority: "normal",
    justification: "",
    cost: "", // Added cost state
  })

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const { addResource } = useApp() // Hook

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    try {
      await addResource({
        name: formData.resourceName,
        category: formData.resourceType,
        quantity: Number(formData.quantity),
        unit: "units", // Default unit
        allocated: 0,
        cost: Number(formData.cost) // Use form cost
      })
      onClose()
    } catch (err) {
      console.error("Failed to add resource", err)
    }
  }

  return (
    <div className="p-6 max-w-2xl">
      <h2 className="text-2xl font-bold mb-6 text-foreground">Request Resource</h2>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Resource Type</label>
            <select
              name="resourceType"
              value={formData.resourceType}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
            >
              <option value="material">Material</option>
              <option value="equipment">Equipment</option>
              <option value="labor">Labor</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Resource Name</label>
            <input
              type="text"
              name="resourceName"
              value={formData.resourceName}
              onChange={handleChange}
              required
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
              placeholder="e.g., Concrete Mix"
            />
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Quantity</label>
            <input
              type="number"
              name="quantity"
              value={formData.quantity}
              onChange={handleChange}
              required
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
              placeholder="Amount needed"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Unit Cost ($)</label>
            <input
              type="number"
              name="cost"
              value={formData.cost}
              onChange={handleChange}
              required
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
              placeholder="0.00"
            />
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Priority</label>
            <select
              name="priority"
              value={formData.priority}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
            >
              <option value="low">Low</option>
              <option value="normal">Normal</option>
              <option value="high">High</option>
              <option value="urgent">Urgent</option>
            </select>
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium text-foreground mb-1">Justification</label>
          <textarea
            name="justification"
            value={formData.justification}
            onChange={handleChange}
            className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
            placeholder="Why is this resource needed?"
            rows={3}
          />
        </div>

        <div className="flex gap-3 pt-4">
          <button
            type="submit"
            className="flex-1 px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 font-medium"
          >
            Submit Request
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

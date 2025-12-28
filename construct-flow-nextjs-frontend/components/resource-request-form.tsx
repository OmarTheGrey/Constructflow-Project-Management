"use client"

import type React from "react"

import { useState } from "react"
import { Calendar, Package, AlertCircle } from "lucide-react"

export function ResourceRequestForm({ onClose }: { onClose?: () => void }) {
  const [formData, setFormData] = useState({
    resourceType: "",
    resourceName: "",
    quantity: 0,
    unit: "",
    requestDate: "",
    neededDate: "",
    priority: "normal",
    justification: "",
  })

  const resourceTypes = [
    { type: "material", label: "Materials", units: ["kg", "tons", "m³", "units"] },
    { type: "equipment", label: "Equipment", units: ["units", "days", "weeks"] },
    { type: "labor", label: "Labor", units: ["hours", "days", "weeks"] },
  ]

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    console.log("Resource request submitted:", formData)
    if (onClose) onClose()
  }

  return (
    <div className="min-h-screen bg-background p-8">
      <div className="max-w-2xl mx-auto">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-foreground">Request Resources</h1>
          <p className="text-muted-foreground mt-2">Submit daily resource needs for your project</p>
        </div>

        <div className="card-elevated p-8">
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Resource Type & Name */}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-semibold text-foreground mb-2 flex items-center gap-2">
                  <Package size={16} /> Resource Type
                </label>
                <select className="w-full px-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary">
                  <option>Select type...</option>
                  {resourceTypes.map((rt) => (
                    <option key={rt.type} value={rt.type}>
                      {rt.label}
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-semibold text-foreground mb-2">Resource Name</label>
                <input
                  type="text"
                  placeholder="e.g., Steel Reinforcement"
                  className="w-full px-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                  onChange={(e) => setFormData({ ...formData, resourceName: e.target.value })}
                />
              </div>
            </div>

            {/* Quantity & Unit */}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-semibold text-foreground mb-2">Quantity</label>
                <input
                  type="number"
                  placeholder="0"
                  className="w-full px-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                  onChange={(e) => setFormData({ ...formData, quantity: Number.parseFloat(e.target.value) })}
                />
              </div>
              <div>
                <label className="block text-sm font-semibold text-foreground mb-2">Unit</label>
                <select className="w-full px-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary">
                  <option>Select unit...</option>
                  <option>kg</option>
                  <option>tons</option>
                  <option>m³</option>
                  <option>units</option>
                  <option>hours</option>
                  <option>days</option>
                </select>
              </div>
            </div>

            {/* Dates */}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-semibold text-foreground mb-2 flex items-center gap-2">
                  <Calendar size={16} /> Request Date
                </label>
                <input
                  type="date"
                  className="w-full px-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                  onChange={(e) => setFormData({ ...formData, requestDate: e.target.value })}
                />
              </div>
              <div>
                <label className="block text-sm font-semibold text-foreground mb-2 flex items-center gap-2">
                  <Calendar size={16} /> Date Needed
                </label>
                <input
                  type="date"
                  className="w-full px-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                  onChange={(e) => setFormData({ ...formData, neededDate: e.target.value })}
                />
              </div>
            </div>

            {/* Priority */}
            <div>
              <label className="block text-sm font-semibold text-foreground mb-2 flex items-center gap-2">
                <AlertCircle size={16} /> Priority
              </label>
              <div className="flex gap-4">
                {["low", "normal", "high", "urgent"].map((p) => (
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

            {/* Justification */}
            <div>
              <label className="block text-sm font-semibold text-foreground mb-2">Justification</label>
              <textarea
                placeholder="Explain why this resource is needed..."
                rows={4}
                className="w-full px-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary resize-none"
                onChange={(e) => setFormData({ ...formData, justification: e.target.value })}
              />
            </div>

            {/* Actions */}
            <div className="flex gap-4 pt-4 border-t border-border">
              <button
                type="submit"
                className="px-6 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors font-medium"
              >
                Submit Request
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

"use client"

import { useApp } from "@/lib/app-context"
import { useState } from "react"
import { Package, AlertTriangle, Plus, Search } from "lucide-react"

export function ResourceManagement({
  onRequestResource,
  onUpdateInventory
}: {
  onRequestResource: () => void
  onUpdateInventory: () => void
}) {
  const { resources } = useApp()
  const [searchQuery, setSearchQuery] = useState("")
  const [filterType, setFilterType] = useState("all")

  const getStatusBadge = (utilized: number) => {
    if (utilized > 80) return "bg-red-500/20 text-red-700 border-red-500/30"
    if (utilized > 60) return "bg-amber-500/20 text-amber-700 border-amber-500/30"
    return "bg-green-500/20 text-green-700 border-green-500/30"
  }

  const utilization = (allocated: number, quantity: number) => {
    return Math.round((allocated / quantity) * 100)
  }

  const filteredResources = resources.filter((r) => filterType === "all" || r.category === filterType)

  const lowResources = resources.filter((r) => {
    const util = utilization(r.allocated, r.quantity)
    return util > 75
  })

  return (
    <div className="min-h-screen bg-background p-8">
      <div className="mb-8">
        <div className="flex justify-between items-start mb-6">
          <div>
            <h1 className="text-3xl font-bold text-foreground">Resource Management</h1>
            <p className="text-muted-foreground mt-2">Track materials, equipment, and labor allocation</p>
          </div>
          <div className="flex gap-2">
            <button
              onClick={onUpdateInventory}
              className="px-4 py-2 bg-slate-100 text-foreground rounded-lg hover:bg-slate-200 transition-colors font-medium flex items-center gap-2"
            >
              Daily Inventory
            </button>
            <button
              onClick={onRequestResource}
              className="px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors font-medium flex items-center gap-2"
            >
              <Plus size={20} /> Add Resource
            </button>
          </div>
        </div>

        <div className="flex gap-4 mb-6">
          <div className="flex-1 relative">
            <Search size={20} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground" />
            <input
              type="text"
              placeholder="Search resources..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>
          <select
            value={filterType}
            onChange={(e) => setFilterType(e.target.value)}
            className="px-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
          >
            <option value="all">All Types</option>
            <option value="material">Materials</option>
            <option value="equipment">Equipment</option>
            <option value="labor">Labor</option>
          </select>
        </div>
      </div>

      <div className="bg-card overflow-hidden rounded-lg border border-border">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border bg-muted/50">
                <th className="text-left py-4 px-6 font-semibold text-muted-foreground text-xs uppercase">Resource</th>
                <th className="text-left py-4 px-6 font-semibold text-muted-foreground text-xs uppercase">Type</th>
                <th className="text-left py-4 px-6 font-semibold text-muted-foreground text-xs uppercase">Available</th>
                <th className="text-left py-4 px-6 font-semibold text-muted-foreground text-xs uppercase">Allocated</th>
                <th className="text-left py-4 px-6 font-semibold text-muted-foreground text-xs uppercase">
                  Utilization
                </th>
                <th className="text-left py-4 px-6 font-semibold text-muted-foreground text-xs uppercase">Cost</th>
              </tr>
            </thead>
            <tbody>
              {filteredResources.map((resource) => {
                const util = utilization(resource.allocated, resource.quantity)
                return (
                  <tr key={resource.id} className="border-b border-border hover:bg-muted/50 transition-colors">
                    <td className="py-4 px-6">
                      <div className="flex items-center gap-2">
                        <Package size={18} className="text-muted-foreground" />
                        <span className="font-medium text-foreground">{resource.name}</span>
                      </div>
                    </td>
                    <td className="py-4 px-6">
                      <span className="text-foreground capitalize">{resource.category}</span>
                    </td>
                    <td className="py-4 px-6">
                      <span className="font-medium text-foreground">
                        {resource.quantity} {resource.unit}
                      </span>
                    </td>
                    <td className="py-4 px-6">
                      <span className="text-foreground">
                        {resource.allocated} {resource.unit}
                      </span>
                    </td>
                    <td className="py-4 px-6">
                      <div className="space-y-1">
                        <div className="w-24 bg-muted rounded-full h-2">
                          <div
                            className={`h-2 rounded-full ${util > 80 ? "bg-destructive" : "bg-primary"}`}
                            style={{ width: `${util}%` }}
                          />
                        </div>
                        <span className="text-xs font-medium text-muted-foreground">{util}%</span>
                      </div>
                    </td>
                    <td className="py-4 px-6 text-foreground font-medium">${(resource.cost || 0).toLocaleString()}</td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>
      </div>

      {lowResources.length > 0 && (
        <div className="mt-8">
          <h3 className="text-lg font-bold text-foreground mb-4 flex items-center gap-2">
            <AlertTriangle size={20} className="text-amber-500" />
            Resource Alerts ({lowResources.length})
          </h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {lowResources.map((resource) => (
              <div
                key={resource.id}
                className="bg-card p-6 rounded-lg border-l-4 border-amber-500 border border-border"
              >
                <div className="flex justify-between items-start mb-2">
                  <h4 className="font-semibold text-foreground">{resource.name}</h4>
                  <span className="px-2 py-1 bg-amber-500/20 text-amber-700 border border-amber-500/30 rounded text-xs font-medium">
                    Alert
                  </span>
                </div>
                <p className="text-sm text-muted-foreground mb-3">
                  Utilization at {utilization(resource.allocated, resource.quantity)}% capacity
                </p>
                <button
                  onClick={onRequestResource}
                  className="px-3 py-2 text-sm bg-amber-500/10 text-amber-700 hover:bg-amber-500/20 transition-colors rounded font-medium"
                >
                  Request More
                </button>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}

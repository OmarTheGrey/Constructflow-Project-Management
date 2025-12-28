"use client"

import { useApp } from "@/lib/app-context"

export function ResourceStatus() {
  const { resources } = useApp()

  const utilization = (allocated: number, quantity: number) => {
    return Math.round((allocated / quantity) * 100)
  }

  return (
    <div className="bg-card p-6 rounded-lg border border-border">
      <h3 className="text-lg font-bold text-foreground mb-6">Resource Allocation</h3>
      <div className="space-y-4">
        {resources.slice(0, 4).map((resource) => {
          const util = utilization(resource.allocated, resource.quantity)
          return (
            <div key={resource.id} className="space-y-2">
              <div className="flex justify-between items-center">
                <span className="text-sm font-medium text-foreground">{resource.name}</span>
                <span className="text-sm font-bold text-primary">{util}%</span>
              </div>
              <div className="w-full bg-muted rounded-full h-2 overflow-hidden">
                <div
                  className={`h-2 rounded-full transition-all ${util > 80 ? "bg-red-500" : util > 60 ? "bg-amber-500" : "bg-primary"}`}
                  style={{ width: `${util}%` }}
                />
              </div>
            </div>
          )
        })}
      </div>
    </div>
  )
}

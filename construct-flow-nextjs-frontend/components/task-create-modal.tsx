"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { useApp } from "@/lib/app-context"
import type { Task } from "@/lib/types"

interface TaskCreateModalProps {
  onClose: () => void
  taskId?: string
}

export function TaskCreateModal({ onClose, taskId }: TaskCreateModalProps) {
  /* eslint-disable react-hooks/exhaustive-deps */
  const { addTask, updateTask, projects, tasks, resources, allocateResource } = useApp()
  // Status and priority values use exact backend casing - no transforms on submit.
  const [formData, setFormData] = useState({
    projectId: projects[0]?.id || "",
    title: "",
    description: "",
    assignee: "",
    dueDate: "",
    priority: "Medium" as "Low" | "Medium" | "High" | "Critical",
    status: "Pending" as "Pending" | "In Progress" | "Completed",
  })

  // Allocation State
  const [allocations, setAllocations] = useState<{ resourceId: string; quantity: number }[]>([])
  const [newAllocation, setNewAllocation] = useState({ resourceId: "", quantity: "" })

  const handleAddAllocation = () => {
    if (newAllocation.resourceId && newAllocation.quantity) {
      setAllocations([...allocations, { resourceId: newAllocation.resourceId, quantity: parseFloat(newAllocation.quantity) }])
      setNewAllocation({ resourceId: "", quantity: "" })
    }
  }

  const removeAllocation = (index: number) => {
    const newAlloc = [...allocations]
    newAlloc.splice(index, 1)
    setAllocations(newAlloc)
  }

  // Load existing task data if editing. Backend stores status/priority in
  // title case ("Pending", "In Progress", "Low", ...); use them as-is.
  useEffect(() => {
    if (taskId) {
      const task = tasks.find((t) => t.id === taskId)
      if (task) {
        setFormData({
          projectId: task.projectId,
          title: task.name,
          description: task.description,
          assignee: task.assignee,
          dueDate: task.dueDate,
          priority: (task.priority as any) || "Medium",
          status: (task.status as any) || "Pending",
        })
      }
    }
  }, [taskId, tasks])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    const taskData: Partial<Task> = {
      projectId: formData.projectId,
      name: formData.title,
      description: formData.description,
      assignee: formData.assignee,
      dueDate: formData.dueDate,
      priority: formData.priority as any,
      status: formData.status as any,
    }

    try {
      let activeTaskId: string | undefined = taskId

      if (taskId) {
        await updateTask(taskId, taskData)
      } else {
        // addTask now returns the backend-assigned UUID — use it to allocate
        // resources on the freshly-created task without a follow-up edit.
        const created = await addTask({
          ...taskData,
          dependencies: [],
          actualCost: 0,
        })
        activeTaskId = created.id
      }

      if (activeTaskId && allocations.length > 0) {
        for (const alloc of allocations) {
          await allocateResource(activeTaskId, alloc.resourceId, alloc.quantity)
        }
      }

      onClose()
    } catch (err: any) {
      const msg = err?.response?.data?.message ?? "Failed to save task"
      alert(msg)
      console.error("Failed to save task:", err)
    }
  }

  return (
    <div className="p-6 max-w-2xl">
      <h2 className="text-2xl font-bold mb-6 text-foreground">
        {taskId ? "Edit Task" : "Create New Task"}
      </h2>

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
          <label className="block text-sm font-medium text-foreground mb-1">Task Title</label>
          <input
            type="text"
            name="title"
            value={formData.title}
            onChange={handleChange}
            required
            className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
            placeholder="Task title"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-foreground mb-1">Description</label>
          <textarea
            name="description"
            value={formData.description}
            onChange={handleChange}
            className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
            placeholder="Task description"
            rows={3}
          />
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Assignee</label>
            <input
              type="text"
              name="assignee"
              value={formData.assignee}
              onChange={handleChange}
              required
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
              placeholder="Assigned to"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Due Date</label>
            <input
              type="date"
              name="dueDate"
              value={formData.dueDate}
              onChange={handleChange}
              required
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
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
              <option value="Low">Low</option>
              <option value="Medium">Medium</option>
              <option value="High">High</option>
              <option value="Critical">Critical</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-foreground mb-1">Status</label>
            <select
              name="status"
              value={formData.status}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"
            >
              <option value="Pending">Pending</option>
              <option value="In Progress">In Progress</option>
              <option value="Completed">Completed</option>
            </select>
          </div>
        </div>

        <div className="border-t pt-4 mt-4">
          <h3 className="text-sm font-bold mb-2">Resource Allocation</h3>
          <div className="flex gap-2 mb-2">
            <select
              className="flex-1 px-3 py-2 border border-slate-300 rounded-lg text-sm"
              value={newAllocation.resourceId}
              onChange={e => setNewAllocation({ ...newAllocation, resourceId: e.target.value })}
            >
              <option value="">Select Resource</option>
              {resources.map(r => (
                <option key={r.id} value={r.id}>{r.name} ({r.quantity} {r.unit})</option>
              ))}
            </select>
            <input
              type="number"
              placeholder="Qty"
              className="w-20 px-3 py-2 border border-slate-300 rounded-lg text-sm"
              value={newAllocation.quantity}
              onChange={e => setNewAllocation({ ...newAllocation, quantity: e.target.value })}
            />
            <button
              type="button"
              onClick={handleAddAllocation}
              className="px-3 py-2 bg-slate-200 rounded-lg text-sm hover:bg-slate-300"
            >Add</button>
          </div>

          {/* List of allocations */}
          {allocations.length > 0 && (
            <ul className="space-y-1 text-sm bg-slate-50 p-2 rounded">
              {allocations.map((alloc, idx) => {
                const res = resources.find(r => r.id === alloc.resourceId);
                return (
                  <li key={idx} className="flex justify-between items-center">
                    <span>{res?.name || 'Unknown'} - {alloc.quantity} {res?.unit}</span>
                    <button type="button" onClick={() => removeAllocation(idx)} className="text-red-500 text-xs hover:underline">Remove</button>
                  </li>
                )
              })}
            </ul>
          )}
        </div>

        <div className="flex gap-3 pt-4">
          <button
            type="submit"
            className="flex-1 px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 font-medium"
          >
            {taskId ? "Save Changes" : "Create Task"}
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

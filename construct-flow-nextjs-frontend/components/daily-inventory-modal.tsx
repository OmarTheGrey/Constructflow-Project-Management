"use client"

import type React from "react"
import { useState } from "react"
import { useApp } from "@/lib/app-context"
import { Resource } from "@/lib/types"

interface DailyInventoryModalProps {
    onClose: () => void
}

export function DailyInventoryModal({ onClose }: DailyInventoryModalProps) {
    const { resources, updateResource, refreshData, updateInventory } = useApp()

    const [selectedResourceId, setSelectedResourceId] = useState<string>("")
    const [quantityChange, setQuantityChange] = useState<string>("")
    const [reason, setReason] = useState<string>("")
    const [action, setAction] = useState<"add" | "remove">("add")

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        if (!selectedResourceId || !quantityChange) return

        const change = Number.parseFloat(quantityChange)
        const finalChange = action === "add" ? change : -change

        try {
            if (updateInventory) {
                await updateInventory(selectedResourceId, finalChange, reason || "Manual adjustment")
                onClose()
            } else {
                console.error("updateInventory not found in context")
            }
        } catch (error) {
            console.error("Failed to update inventory", error)
        }
    }

    return (
        <div className="p-6 max-w-md">
            <h2 className="text-2xl font-bold mb-6 text-foreground">Daily Inventory Update</h2>

            <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                    <label className="block text-sm font-medium text-foreground mb-1">Resource</label>
                    <select
                        value={selectedResourceId}
                        onChange={(e) => setSelectedResourceId(e.target.value)}
                        className="w-full px-3 py-2 border border-slate-300 rounded-lg"
                        required
                    >
                        <option value="">Select Resource</option>
                        {resources.map((r) => (
                            <option key={r.id} value={r.id}>
                                {r.name} (Current: {r.quantity} {r.unit})
                            </option>
                        ))}
                    </select>
                </div>

                <div>
                    <label className="block text-sm font-medium text-foreground mb-1">Action</label>
                    <div className="flex gap-4">
                        <label className="flex items-center">
                            <input
                                type="radio"
                                name="action"
                                value="add"
                                checked={action === "add"}
                                onChange={() => setAction("add")}
                                className="mr-2"
                            />
                            Add Stock
                        </label>
                        <label className="flex items-center">
                            <input
                                type="radio"
                                name="action"
                                value="remove"
                                checked={action === "remove"}
                                onChange={() => setAction("remove")}
                                className="mr-2"
                            />
                            Remove Stock
                        </label>
                    </div>
                </div>

                <div>
                    <label className="block text-sm font-medium text-foreground mb-1">Quantity</label>
                    <input
                        type="number"
                        value={quantityChange}
                        onChange={(e) => setQuantityChange(e.target.value)}
                        className="w-full px-3 py-2 border border-slate-300 rounded-lg"
                        placeholder="Amount"
                        required
                        min="0"
                    />
                </div>

                <div>
                    <label className="block text-sm font-medium text-foreground mb-1">Reason (Optional)</label>
                    <input
                        type="text"
                        value={reason}
                        onChange={(e) => setReason(e.target.value)}
                        className="w-full px-3 py-2 border border-slate-300 rounded-lg"
                        placeholder="e.g., New delivery, Spoilage"
                    />
                </div>

                <div className="flex gap-3 pt-4">
                    <button
                        type="submit"
                        className="flex-1 px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 font-medium"
                    >
                        Update Inventory
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

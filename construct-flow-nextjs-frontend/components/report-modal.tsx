"use client"

import { useEffect, useState } from "react"
import { useApp } from "@/lib/app-context"
import { ReportService } from "@/lib/api-service"
import { X, Printer } from "lucide-react"

interface ExecutiveSummary {
    totalProjects: number
    activeProjects: number
    totalBudget: number
    totalActualCost: number
    completedTasks: number
    pendingTasks: number
    criticalAlerts: number
    recentActivities: string[]
}

interface ReportModalProps {
    onClose: () => void
}

export function ReportModal({ onClose }: ReportModalProps) {
    const [summary, setSummary] = useState<ExecutiveSummary | null>(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        async function fetchReport() {
            try {
                const data = await ReportService.getExecutiveSummary()
                setSummary(data)
            } catch (err) {
                console.error("Failed to fetch report", err)
            } finally {
                setLoading(false)
            }
        }
        fetchReport()
    }, [])

    if (loading) {
        return (
            <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
                <div className="bg-card p-6 rounded-lg text-foreground">Loading Report...</div>
            </div>
        )
    }

    if (!summary) return null

    return (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
            <div className="bg-card w-full max-w-4xl max-h-[90vh] overflow-y-auto rounded-lg shadow-2xl border border-border">
                <div className="p-6 border-b border-border flex justify-between items-center bg-muted/20">
                    <div>
                        <h2 className="text-2xl font-bold text-foreground">Executive Summary Report</h2>
                        <p className="text-muted-foreground">Generated on {new Date().toLocaleDateString()}</p>
                    </div>
                    <div className="flex gap-2">
                        <button onClick={() => window.print()} className="p-2 hover:bg-muted rounded-full">
                            <Printer size={20} className="text-foreground" />
                        </button>
                        <button onClick={onClose} className="p-2 hover:bg-muted rounded-full">
                            <X size={24} className="text-foreground" />
                        </button>
                    </div>
                </div>

                <div className="p-8 space-y-8">
                    {/* High Level Stats */}
                    <div className="grid grid-cols-4 gap-6">
                        <div className="bg-primary/10 p-4 rounded-lg border border-primary/20">
                            <div className="text-sm font-semibold text-primary uppercase">Total Budget</div>
                            <div className="text-2xl font-bold text-foreground mt-1">${summary.totalBudget.toLocaleString()}</div>
                        </div>
                        <div className="bg-green-500/10 p-4 rounded-lg border border-green-500/20">
                            <div className="text-sm font-semibold text-green-600 uppercase">Actual Cost</div>
                            <div className="text-2xl font-bold text-foreground mt-1">${summary.totalActualCost.toLocaleString()}</div>
                        </div>
                        <div className="bg-blue-500/10 p-4 rounded-lg border border-blue-500/20">
                            <div className="text-sm font-semibold text-blue-600 uppercase">Active Projects</div>
                            <div className="text-2xl font-bold text-foreground mt-1">{summary.activeProjects} / {summary.totalProjects}</div>
                        </div>
                        <div className="bg-amber-500/10 p-4 rounded-lg border border-amber-500/20">
                            <div className="text-sm font-semibold text-amber-600 uppercase">Pending Tasks</div>
                            <div className="text-2xl font-bold text-foreground mt-1">{summary.pendingTasks}</div>
                        </div>
                    </div>

                    {/* Financial Health */}
                    <div>
                        <h3 className="text-lg font-bold text-foreground mb-4 border-b border-border pb-2">Financial Health</h3>
                        <div className="bg-muted/30 p-6 rounded-lg">
                            <div className="flex justify-between mb-2">
                                <span className="font-medium text-foreground">Budget Utilization</span>
                                <span className="font-bold text-foreground">{((summary.totalActualCost / summary.totalBudget) * 100).toFixed(1)}%</span>
                            </div>
                            <div className="w-full bg-slate-200 rounded-full h-4">
                                <div
                                    className="bg-primary h-4 rounded-full transition-all duration-1000"
                                    style={{ width: `${Math.min((summary.totalActualCost / summary.totalBudget) * 100, 100)}%` }}
                                />
                            </div>
                            <p className="mt-4 text-sm text-muted-foreground">
                                Total allocated budget across all projects is <strong>${summary.totalBudget.toLocaleString()}</strong> with a current valid spend of <strong>${summary.totalActualCost.toLocaleString()}</strong>.
                            </p>
                        </div>
                    </div>

                    {/* Critical Alerts */}
                    {summary.criticalAlerts > 0 && (
                        <div className="bg-red-50 border border-red-200 p-4 rounded-lg">
                            <h4 className="font-bold text-red-800 flex items-center gap-2">
                                ⚠️ Operational Risks Detected
                            </h4>
                            <p className="text-red-700 mt-1">There are {summary.criticalAlerts} critical items requiring immediate attention (overdue tasks or budget overruns).</p>
                        </div>
                    )}

                </div>

                <div className="p-6 bg-muted/20 border-t border-border text-center text-sm text-muted-foreground">
                    ConstructFlow Project Management System • Confidential
                </div>
            </div>
        </div>
    )
}

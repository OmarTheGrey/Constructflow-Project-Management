"use client"

import { useState } from "react"
import { DailyReport } from "./daily-report"
import { FileText, BarChart3, Calendar } from "lucide-react"

interface ReportsSectionProps {
    onOpenGlobalReport: () => void
}

export function ReportsSection({ onOpenGlobalReport }: ReportsSectionProps) {
    const [activeTab, setActiveTab] = useState<"daily" | "global">("daily")

    return (
        <div className="min-h-screen bg-background p-8">
            <div className="mb-8">
                <h1 className="text-3xl font-bold text-foreground">Reports</h1>
                <p className="text-muted-foreground mt-2">Generate and view project reports</p>
            </div>

            {/* Tab Navigation */}
            <div className="mb-6 border-b border-border">
                <div className="flex gap-4">
                    <button
                        onClick={() => setActiveTab("daily")}
                        className={`px-4 py-3 font-medium transition-colors border-b-2 flex items-center gap-2 ${activeTab === "daily"
                                ? "border-primary text-primary"
                                : "border-transparent text-muted-foreground hover:text-foreground"
                            }`}
                    >
                        <Calendar size={18} />
                        Daily Reports
                    </button>
                    <button
                        onClick={() => setActiveTab("global")}
                        className={`px-4 py-3 font-medium transition-colors border-b-2 flex items-center gap-2 ${activeTab === "global"
                                ? "border-primary text-primary"
                                : "border-transparent text-muted-foreground hover:text-foreground"
                            }`}
                    >
                        <BarChart3 size={18} />
                        Global Report
                    </button>
                </div>
            </div>

            {/* Tab Content */}
            <div>
                {activeTab === "daily" ? (
                    <DailyReport />
                ) : (
                    <div className="bg-card p-6 rounded-lg border border-border">
                        <div className="text-center py-12">
                            <FileText size={64} className="mx-auto text-muted-foreground mb-4" />
                            <h3 className="text-xl font-semibold text-foreground mb-2">Global Project Report</h3>
                            <p className="text-muted-foreground mb-6 max-w-md mx-auto">
                                Generate a comprehensive report covering all projects, tasks, resources, and overall system status.
                            </p>
                            <button
                                onClick={onOpenGlobalReport}
                                className="px-6 py-3 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors font-medium flex items-center gap-2 mx-auto"
                            >
                                <BarChart3 size={20} />
                                Generate Global Report
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    )
}

export function ProjectMetrics() {
  const metrics = [
    { label: "Active Projects", value: 12, change: "+3" },
    { label: "Completed", value: 47, change: "+8" },
    { label: "On Schedule", value: "89%", change: "+5%" },
    { label: "Budget Health", value: "94%", change: "+2%" },
  ]

  return (
    <div className="grid grid-cols-4 gap-4">
      {metrics.map((metric, idx) => (
        <div key={idx} className="card-elevated p-6">
          <div className="text-sm font-semibold uppercase text-accent tracking-wide">{metric.label}</div>
          <div className="text-2xl font-bold mt-2 text-foreground">{metric.value}</div>
          <div className="text-xs text-muted-foreground mt-2">{metric.change} vs last month</div>
        </div>
      ))}
    </div>
  )
}

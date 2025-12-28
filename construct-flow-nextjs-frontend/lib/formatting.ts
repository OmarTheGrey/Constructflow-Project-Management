// Format utility functions for consistent data presentation

export const formatDate = (date: string | Date): string => {
  const d = new Date(date)
  return d.toLocaleDateString("en-US", {
    year: "numeric",
    month: "short",
    day: "numeric",
  })
}

export const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
  }).format(amount)
}

export const formatPercentage = (value: number, decimals = 0): string => {
  return `${value.toFixed(decimals)}%`
}

export const getStatusColor = (status: string): string => {
  const colors: Record<string, string> = {
    active: "bg-green-500/20 text-green-700 border-green-500/30",
    pending: "bg-yellow-500/20 text-yellow-700 border-yellow-500/30",
    "in-progress": "bg-blue-500/20 text-blue-700 border-blue-500/30",
    completed: "bg-green-500/20 text-green-700 border-green-500/30",
    overdue: "bg-red-500/20 text-red-700 border-red-500/30",
    draft: "bg-gray-500/20 text-gray-700 border-gray-500/30",
  }
  return colors[status] || "bg-muted"
}

export const getPriorityColor = (priority: string): string => {
  const colors: Record<string, string> = {
    low: "bg-green-500/20 text-green-700 border-green-500/30",
    medium: "bg-blue-500/20 text-blue-700 border-blue-500/30",
    high: "bg-orange-500/20 text-orange-700 border-orange-500/30",
    critical: "bg-red-500/20 text-red-700 border-red-500/30",
  }
  return colors[priority] || "bg-muted"
}

export const calculateProgress = (completed: number, total: number): number => {
  if (total === 0) return 0
  return Math.round((completed / total) * 100)
}

export const getRelativeTime = (date: string | Date): string => {
  const d = new Date(date)
  const now = new Date()
  const diff = now.getTime() - d.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 60) return `${minutes}m ago`
  if (hours < 24) return `${hours}h ago`
  if (days < 7) return `${days}d ago`

  return formatDate(d)
}

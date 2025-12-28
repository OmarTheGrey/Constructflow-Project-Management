export interface Project {
  id: string // UUID
  name: string
  client: string
  location: string
  startDate: string
  endDate: string
  budget: number
  actualCost: number
  status: "Draft" | "Active" | "Completed" | "On Hold"
  progress: number
  objectives: string
  milestones: string[]
  createdAt?: string
  lastModifiedAt?: string
}

export interface Task {
  id: string // UUID
  projectId: string // UUID
  name: string
  description: string
  assignee: string
  dueDate: string
  status: "Pending" | "In Progress" | "Completed"
  priority: "Low" | "Normal" | "High"
  actualCost: number
  dependencies: string[]
  createdAt?: string
}

export interface Resource {
  id: string // UUID
  name: string
  category: string // Changed from type to match backend DTO
  quantity: number
  unit: string
  allocated: number
  cost: number
  createdAt?: string
}

export interface Document {
  id: string // UUID
  projectId: string // UUID
  name: string
  type: string
  uploadDate: string
  size: string
  folder: string
  uploadedBy: string
}

export interface Announcement {
  id: string
  title: string
  message: string
  content?: string  // Alias for message
  priority: string
  datePosted: string
  author: string
}

export interface DailyReport {
  id: string
  projectId: string
  activities: string
  issues: string
  completionPercentage: number
  photos: string[]
  submittedBy: string
  createdAt: string
}

export interface WorkLog {
  id: string
  taskId: string
  date: string
  hours: number
  notes: string
  submittedBy: string
  createdAt: string
}
export interface TaskAllocation {
  id: string
  taskId: string
  resourceId: string
  quantityAllocated: number
  allocatedAt: string
  notes?: string
}

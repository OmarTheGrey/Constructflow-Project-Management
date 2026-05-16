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
  // Must match backend casing - see TASK_STATUS in constants.ts and the
  // values rendered by TaskController.getCriticalTasks (priority "Critical").
  status: "Pending" | "In Progress" | "Completed"
  priority: "Low" | "Medium" | "High" | "Critical"
  actualCost: number
  dependencies: string[]
  createdAt?: string
}

export interface Resource {
  id: string // UUID
  name: string
  category: string // Material | Equipment | Labor — matches backend ResourceRequestDTO.category
  quantity: number
  unit: string
  // Percentage 0-100. Maps to backend ResourceResponseDTO.allocationPercentage.
  allocationPercentage: number
  cost: number
  projectId?: string
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
  // Matches backend AnnouncementRequestDTO.content / AnnouncementResponseDTO.content.
  content: string
  priority: string
  // Backend ResponseDTO uses createdAt (ISO date-time); kept here under the
  // legacy field name datePosted so existing components don't break.
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

// Navigation routes and menu items
export const MAIN_MENU = [
  { icon: "LayoutGrid", label: "Dashboard", href: "/" },
  { icon: "Briefcase", label: "Projects", href: "/projects" },
  { icon: "CheckSquare", label: "Tasks", href: "/tasks" },
  { icon: "Layers", label: "Resources", href: "/resources" },
  { icon: "FileText", label: "Documents", href: "/documents" },
  { icon: "Bell", label: "Announcements", href: "/announcements" },
]

export const BOTTOM_MENU = [
  { icon: "Settings", label: "Settings", href: "/settings" },
  { icon: "LogOut", label: "Logout", href: "/logout" },
]

// User roles and permissions
export const USER_ROLES = {
  ADMIN: "admin",
  PROJECT_MANAGER: "project_manager",
  SITE_ENGINEER: "site_engineer",
  ARCHITECT: "architect",
  CLIENT: "client",
}

// Project statuses
export const PROJECT_STATUS = {
  DRAFT: "draft",
  ACTIVE: "active",
  IN_PROGRESS: "in-progress",
  COMPLETED: "completed",
  ON_HOLD: "on-hold",
}

// Task statuses
export const TASK_STATUS = {
  PENDING: "pending",
  IN_PROGRESS: "in-progress",
  COMPLETED: "completed",
  OVERDUE: "overdue",
}

// Priority levels
export const PRIORITY_LEVELS = {
  LOW: "low",
  MEDIUM: "medium",
  HIGH: "high",
  CRITICAL: "critical",
}

// Resource types
export const RESOURCE_TYPES = {
  MATERIAL: "material",
  EQUIPMENT: "equipment",
  LABOR: "labor",
}

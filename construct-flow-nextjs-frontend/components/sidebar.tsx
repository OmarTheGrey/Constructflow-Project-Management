"use client"
import { LayoutGrid, Briefcase, CheckSquare, Layers, FileText, Bell, Settings, LogOut, FileBarChart } from "lucide-react"

const menuItems = [
  { id: "dashboard", label: "Dashboard" },
  { id: "projects", label: "Projects" },
  { id: "tasks", label: "Tasks" },
  { id: "resources", label: "Resources" },
  { id: "documents", label: "Documents" },
  { id: "reports", label: "Reports" },
  { id: "announcements", label: "Announcements" },
]

const bottomItems = [
  { icon: Settings, label: "Settings" },
  { icon: LogOut, label: "Logout" },
]

interface SidebarProps {
  currentSection: string
  onNavigate: (section: string) => void
}

export function Sidebar({ currentSection, onNavigate }: SidebarProps) {
  const menuIcons: { [key: string]: typeof LayoutGrid } = {
    dashboard: LayoutGrid,
    projects: Briefcase,
    tasks: CheckSquare,
    resources: Layers,
    documents: FileText,
    reports: FileBarChart,
    announcements: Bell,
  }

  return (
    <aside className="w-64 bg-[#2a2a2a] border-r border-[#3a3a3a] flex flex-col h-screen">
      <div className="p-6 border-b border-[#3a3a3a]">
        <h1 className="text-2xl font-semibold italic text-white font-sans">constructflow</h1>
      </div>

      {/* Main Menu */}
      <nav className="flex-1 p-4">
        <div className="space-y-2">
          {menuItems.map((item) => {
            const IconComponent = menuIcons[item.id]
            return (
              <button
                key={item.id}
                onClick={() => onNavigate(item.id)}
                className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg transition-all ${currentSection === item.id
                  ? "bg-[#4a90e2] text-white"
                  : "text-[#b0b0b0] hover:text-white hover:bg-[#3a3a3a]"
                  }`}
              >
                <IconComponent size={20} />
                <span className="text-sm font-medium">{item.label}</span>
              </button>
            )
          })}
        </div>
      </nav>

      {/* Bottom Menu */}
      <div className="p-4 border-t border-[#3a3a3a] space-y-2">
        {bottomItems.map((item, index) => (
          <button
            key={index}
            className="w-full flex items-center gap-3 px-4 py-3 rounded-lg text-[#b0b0b0] hover:text-white hover:bg-[#3a3a3a] transition-all"
          >
            <item.icon size={20} />
            <span className="text-sm font-medium">{item.label}</span>
          </button>
        ))}
      </div>
    </aside>
  )
}

"use client"

import { Bell, Search, Settings, User, LogOut } from "lucide-react"

export function Header() {
  return (
    <header className="bg-card border-b border-border sticky top-0 z-40">
      <div className="flex items-center justify-between px-8 py-4">
        {/* Search Bar */}
        <div className="flex-1 max-w-md relative">
          <Search size={18} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground" />
          <input
            type="text"
            placeholder="Search projects, tasks..."
            className="w-full pl-10 pr-4 py-2 border border-border rounded-lg bg-muted/50 text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary text-sm"
          />
        </div>

        {/* Right Actions */}
        <div className="flex items-center gap-6 ml-8">
          {/* Notifications */}
          <button className="relative p-2 hover:bg-muted rounded-lg transition-colors">
            <Bell size={20} className="text-muted-foreground hover:text-foreground" />
            <span className="absolute top-1 right-1 w-2 h-2 bg-accent rounded-full"></span>
          </button>

          {/* User Menu */}
          <div className="flex items-center gap-3 pl-6 border-l border-border">
            <div className="text-right">
              <p className="text-sm font-semibold text-foreground">John Smith</p>
              <p className="text-xs text-muted-foreground">Site Engineer</p>
            </div>
            <button className="w-10 h-10 rounded-full bg-primary text-primary-foreground flex items-center justify-center font-bold">
              JS
            </button>

            {/* Dropdown Menu */}
            <div className="absolute top-16 right-8 hidden group-hover:block">
              <div className="bg-card border border-border rounded-lg shadow-lg p-2 w-48">
                <button className="w-full flex items-center gap-3 px-4 py-2 rounded hover:bg-muted transition-colors text-foreground text-sm">
                  <User size={16} /> Profile
                </button>
                <button className="w-full flex items-center gap-3 px-4 py-2 rounded hover:bg-muted transition-colors text-foreground text-sm">
                  <Settings size={16} /> Settings
                </button>
                <hr className="my-2 border-border" />
                <button className="w-full flex items-center gap-3 px-4 py-2 rounded hover:bg-destructive/10 transition-colors text-destructive text-sm">
                  <LogOut size={16} /> Logout
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </header>
  )
}

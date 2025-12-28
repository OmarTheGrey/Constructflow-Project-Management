"use client"

import type React from "react"
import { createContext, useContext, useState, useEffect } from "react"
import type { Project, Task, Resource, Document, Announcement, DailyReport, WorkLog } from "./types"
import { ProjectService, TaskService, ResourceService, DocumentService, AnnouncementService, ReportService } from "./api-service"

interface AppContextType {
  projects: Project[]
  tasks: Task[]
  resources: Resource[]
  documents: Document[]
  announcements: Announcement[]
  dailyReports: DailyReport[]
  workLogs: WorkLog[]
  addProject: (project: Partial<Project>) => Promise<void>
  updateProject: (id: string, project: Partial<Project>) => Promise<void>
  deleteProject: (id: string) => Promise<void>
  addTask: (task: Partial<Task>) => Promise<void>
  updateTask: (id: string, task: Partial<Task>) => Promise<void>
  deleteTask: (id: string) => Promise<void>
  addResource: (resource: Partial<Resource>) => Promise<void>
  updateResource: (id: string, resource: Partial<Resource>) => Promise<void>
  addDocument: (document: any) => Promise<void>
  deleteDocument: (id: string) => Promise<void>
  addAnnouncement: (announcement: Partial<Announcement>) => Promise<void>
  deleteAnnouncement: (id: string) => Promise<void>
  addDailyReport: (report: Partial<DailyReport>) => Promise<void>
  addWorkLog: (log: Partial<WorkLog>) => Promise<void>
  allocateResource: (taskId: string, resourceId: string, quantity: number) => Promise<void>
  updateInventory: (id: string, quantityChange: number, reason: string) => Promise<void>
  refreshData: () => Promise<void>
}

const AppContext = createContext<AppContextType | undefined>(undefined)

export function AppProvider({ children }: { children: React.ReactNode }) {
  const [projects, setProjects] = useState<Project[]>([])
  const [tasks, setTasks] = useState<Task[]>([])
  const [resources, setResources] = useState<Resource[]>([])
  const [documents, setDocuments] = useState<Document[]>([])
  const [announcements, setAnnouncements] = useState<Announcement[]>([])
  const [dailyReports, setDailyReports] = useState<DailyReport[]>([])
  const [workLogs, setWorkLogs] = useState<WorkLog[]>([])

  const refreshData = async () => {
    try {
      console.log("Refreshing data from backend...");
      const [p, t, r, a] = await Promise.all([
        ProjectService.getAllProjects(),
        TaskService.getAllTasks(),
        ResourceService.getAllResources(),
        AnnouncementService.getAllAnnouncements()
      ]);
      setProjects(p);
      setTasks(t);
      setResources(r);
      setAnnouncements(a);
    } catch (error) {
      console.error("Failed to refresh data", error);
    }
  };

  useEffect(() => {
    refreshData();
  }, []);

  const addProject = async (project: Partial<Project>) => {
    await ProjectService.createProject(project);
    refreshData();
  }

  const updateProject = async (id: string, project: Partial<Project>) => {
    await ProjectService.updateProject(id, project);
    refreshData();
  }

  const deleteProject = async (id: string) => {
    await ProjectService.deleteProject(id);
    refreshData();
  }

  const addTask = async (task: Partial<Task>) => {
    await TaskService.createTask(task);
    refreshData();
  }

  const updateTask = async (id: string, task: Partial<Task>) => {
    await TaskService.updateTask(id, task);
    refreshData();
  }

  const deleteTask = async (id: string) => {
    await TaskService.deleteTask(id);
    refreshData();
  }

  const addResource = async (resource: Partial<Resource>) => {
    await ResourceService.createResource(resource);
    refreshData();
  }

  const updateResource = async (id: string, resource: Partial<Resource>) => {
    await ResourceService.updateResource(id, resource);
    refreshData();
  }

  const addDocument = async (formData: FormData) => {
    await DocumentService.uploadDocument(formData);
    // Refresh documents handled via refreshData or local state update if implemented
  }

  const deleteDocument = async (id: string) => {
    await DocumentService.deleteDocument(id);
  }

  const addAnnouncement = async (announcement: Partial<Announcement>) => {
    await AnnouncementService.createAnnouncement(announcement);
    refreshData();
  }

  const deleteAnnouncement = async (id: string) => {
    await AnnouncementService.deleteAnnouncement(id);
    refreshData();
  }

  const addDailyReport = async (report: Partial<DailyReport>) => {
    await ReportService.createDailyReport(report);
  }

  const addWorkLog = async (log: Partial<WorkLog>) => {
    await ReportService.createWorkLog(log);
  }

  const allocateResource = async (taskId: string, resourceId: string, quantity: number) => {
    await ResourceService.allocateResource(taskId, resourceId, quantity);
    refreshData();
  }

  const updateInventory = async (id: string, quantityChange: number, reason: string) => {
    await ResourceService.updateInventory(id, quantityChange, reason);
    refreshData();
  }

  return (
    <AppContext.Provider
      value={{
        projects,
        tasks,
        resources,
        documents,
        announcements,
        dailyReports,
        workLogs,
        addProject,
        updateProject,
        deleteProject,
        addTask,
        updateTask,
        deleteTask,
        addResource,
        updateResource,
        addDocument,
        deleteDocument,
        addAnnouncement,
        deleteAnnouncement,
        addDailyReport,
        addWorkLog,
        allocateResource,
        updateInventory,
        refreshData
      }}
    >
      {children}
    </AppContext.Provider>
  )
}

export function useApp() {
  const context = useContext(AppContext)
  if (context === undefined) {
    throw new Error("useApp must be used within AppProvider")
  }
  return context
}

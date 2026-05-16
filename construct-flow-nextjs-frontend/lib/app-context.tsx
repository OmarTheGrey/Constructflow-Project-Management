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
  // Create operations return the backend-assigned entity (with real UUID)
  // so callers can chain follow-up calls (e.g. allocate-on-create).
  addProject: (project: Partial<Project>) => Promise<Project>
  updateProject: (id: string, project: Partial<Project>) => Promise<Project>
  deleteProject: (id: string) => Promise<void>
  addTask: (task: Partial<Task>) => Promise<Task>
  updateTask: (id: string, task: Partial<Task>) => Promise<Task>
  deleteTask: (id: string) => Promise<void>
  addResource: (resource: Partial<Resource>) => Promise<Resource>
  updateResource: (id: string, resource: Partial<Resource>) => Promise<Resource>
  addDocument: (document: any) => Promise<Document>
  deleteDocument: (id: string) => Promise<void>
  addAnnouncement: (announcement: Partial<Announcement>) => Promise<Announcement>
  deleteAnnouncement: (id: string) => Promise<void>
  addDailyReport: (report: Partial<DailyReport>) => Promise<DailyReport>
  addWorkLog: (log: Partial<WorkLog>) => Promise<WorkLog>
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

  const addProject = async (project: Partial<Project>): Promise<Project> => {
    const created = await ProjectService.createProject(project);
    refreshData();
    return created;
  }

  const updateProject = async (id: string, project: Partial<Project>): Promise<Project> => {
    const updated = await ProjectService.updateProject(id, project);
    refreshData();
    return updated;
  }

  const deleteProject = async (id: string) => {
    await ProjectService.deleteProject(id);
    refreshData();
  }

  const addTask = async (task: Partial<Task>): Promise<Task> => {
    const created = await TaskService.createTask(task);
    refreshData();
    return created;
  }

  const updateTask = async (id: string, task: Partial<Task>): Promise<Task> => {
    const updated = await TaskService.updateTask(id, task);
    refreshData();
    return updated;
  }

  const deleteTask = async (id: string) => {
    await TaskService.deleteTask(id);
    refreshData();
  }

  const addResource = async (resource: Partial<Resource>): Promise<Resource> => {
    const created = await ResourceService.createResource(resource);
    refreshData();
    return created;
  }

  const updateResource = async (id: string, resource: Partial<Resource>): Promise<Resource> => {
    const updated = await ResourceService.updateResource(id, resource);
    refreshData();
    return updated;
  }

  const addDocument = async (formData: FormData): Promise<Document> => {
    const created = await DocumentService.uploadDocument(formData);
    // Stash the freshly-uploaded record locally so the UI can show it without
    // a global refresh (the backend has no global "list all documents" endpoint).
    setDocuments(prev => [...prev, created]);
    return created;
  }

  const deleteDocument = async (id: string) => {
    await DocumentService.deleteDocument(id);
    setDocuments(prev => prev.filter(d => d.id !== id));
  }

  const addAnnouncement = async (announcement: Partial<Announcement>): Promise<Announcement> => {
    const created = await AnnouncementService.createAnnouncement(announcement);
    refreshData();
    return created;
  }

  const deleteAnnouncement = async (id: string) => {
    await AnnouncementService.deleteAnnouncement(id);
    refreshData();
  }

  const addDailyReport = async (report: Partial<DailyReport>): Promise<DailyReport> => {
    return await ReportService.createDailyReport(report);
  }

  const addWorkLog = async (log: Partial<WorkLog>): Promise<WorkLog> => {
    return await ReportService.createWorkLog(log);
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

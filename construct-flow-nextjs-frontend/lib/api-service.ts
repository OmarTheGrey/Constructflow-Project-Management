import axios from 'axios';
import { Project, Task, Resource, Document, DailyReport, WorkLog, Announcement } from './types';

// Base URL for the Spring Boot backend
const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const ProjectService = {
  getAllProjects: async () => {
    const response = await api.get('/projects');
    return response.data.content || response.data; // Handle Page<T> or List<T>
  },
  getProjectById: async (id: string) => {
    const response = await api.get(`/projects/${id}`);
    return response.data;
  },
  createProject: async (project: Partial<Project>) => {
    const response = await api.post('/projects', project);
    return response.data;
  },
  updateProject: async (id: string, project: Partial<Project>) => {
    const response = await api.put(`/projects/${id}`, project);
    return response.data;
  },
  deleteProject: async (id: string) => {
    await api.delete(`/projects/${id}`);
  }
};

export const TaskService = {
  getAllTasks: async () => {
    const response = await api.get('/tasks');
    return response.data.content || response.data;
  },
  getTasksByProject: async (projectId: string) => {
    const response = await api.get(`/tasks/project/${projectId}`);
    return response.data;
  },
  createTask: async (task: Partial<Task>) => {
    const response = await api.post('/tasks', task);
    return response.data;
  },
  updateTask: async (id: string, task: Partial<Task>) => {
    const response = await api.put(`/tasks/${id}`, task);
    return response.data;
  },
  deleteTask: async (id: string) => {
    await api.delete(`/tasks/${id}`);
  }
};

export const ResourceService = {
  getAllResources: async () => {
    const response = await api.get('/resources');
    return response.data.content || response.data;
  },
  createResource: async (resource: Partial<Resource>) => {
    const response = await api.post('/resources', resource);
    return response.data;
  },
  updateResource: async (id: string, resource: Partial<Resource>) => {
    const response = await api.put(`/resources/${id}`, resource);
    return response.data;
  },
  deleteResource: async (id: string) => {
    await api.delete(`/resources/${id}`);
  },
  allocateResource: async (taskId: string, resourceId: string, quantity: number) => {
    // Use URLSearchParams for request parameters when using POST without a body object but query params
    const params = new URLSearchParams();
    params.append('taskId', taskId);
    params.append('resourceId', resourceId);
    params.append('quantity', quantity.toString());
    await api.post(`/resources/allocate?${params.toString()}`, {});
  },
  updateInventory: async (id: string, quantityChange: number, reason: string) => {
    const params = new URLSearchParams();
    params.append('quantityChange', quantityChange.toString());
    params.append('reason', reason);
    await api.post(`/resources/${id}/inventory?${params.toString()}`, {});
  }
};

export const DocumentService = {
  getDocumentsByProject: async (projectId: string) => {
    const response = await api.get(`/documents/project/${projectId}`);
    return response.data;
  },
  uploadDocument: async (formData: FormData) => {
    // Bypass the shared `api` instance whose default Content-Type is application/json.
    // Letting axios omit the header makes the browser set multipart/form-data
    // with the correct boundary automatically.
    const response = await axios.post(`${API_BASE_URL}/documents`, formData);
    return response.data;
  },
  deleteDocument: async (id: string) => {
    await api.delete(`/documents/${id}`);
  }
};

export const AnnouncementService = {
  getAllAnnouncements: async () => {
    const response = await api.get('/announcements');
    return response.data.content || response.data;
  },
  createAnnouncement: async (announcement: Partial<Announcement>) => {
    const response = await api.post('/announcements', announcement);
    return response.data;
  },
  deleteAnnouncement: async (id: string) => {
    await api.delete(`/announcements/${id}`);
  }
};

export const ReportService = {
  // Daily Reports
  getDailyReportsByProject: async (projectId: string) => {
    const response = await api.get(`/daily-reports/project/${projectId}`);
    return response.data;
  },
  createDailyReport: async (report: Partial<DailyReport>) => {
    const response = await api.post('/daily-reports', report);
    return response.data;
  },

  // Work Logs
  getWorkLogsByTask: async (taskId: string) => {
    const response = await api.get(`/work-logs/task/${taskId}`);
    return response.data;
  },
  createWorkLog: async (log: Partial<WorkLog>) => {
    const response = await api.post('/work-logs', log);
    return response.data;
  },

  // Global Executive Report
  getExecutiveSummary: async () => {
    const response = await api.get('/reports/summary');
    return response.data;
  }
};
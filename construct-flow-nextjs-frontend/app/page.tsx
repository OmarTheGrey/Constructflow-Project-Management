"use client"

import { useState } from "react"
import { Sidebar } from "@/components/sidebar"
import { Dashboard } from "@/components/dashboard"
import { ProjectsSection } from "@/components/projects-section"
import { TaskManager } from "@/components/task-manager"
import { ResourceManagement } from "@/components/resource-management"
import { DocumentManagement } from "@/components/document-management"
import { AnnouncementBoard } from "@/components/announcement-board"
import { ReportsSection } from "@/components/reports-section"
import { Modal } from "@/components/modal"
import { ProjectCreateModal } from "@/components/project-create-modal"
import { TaskCreateModal } from "@/components/task-create-modal"
import { DocumentUploadModal } from "@/components/document-upload-modal"
import { ResourceRequestModal } from "@/components/resource-request-modal"
import { DailyInventoryModal } from "@/components/daily-inventory-modal"
import { FolderViewModal } from "@/components/folder-view-modal"
import { ReportModal } from "@/components/report-modal"
import { AppProvider, useApp } from "@/lib/app-context"

function HomeContent() {
  const { deleteTask, deleteDocument } = useApp()
  const [currentSection, setCurrentSection] = useState("dashboard")
  const [modal, setModal] = useState<{
    type: string
    isOpen: boolean
    data?: any
  }>({
    type: "",
    isOpen: false,
  })

  const openModal = (type: string, data?: any) => {
    setModal({ type, isOpen: true, data })
  }

  const closeModal = () => {
    setModal({ type: "", isOpen: false, data: undefined })
  }

  const renderSection = () => {
    switch (currentSection) {
      case "dashboard":
        return <Dashboard onNewProject={() => openModal("new-project")} />
      case "projects":
        return <ProjectsSection
          onNewProject={() => openModal("new-project")}
          onEditProject={(projectId) => openModal("edit-project", { projectId })}
        />
      case "tasks":
        return (
          <TaskManager
            onNewTask={() => openModal("new-task")}
            onEditTask={(taskId) => openModal("edit-task", { taskId })}
            onDeleteTask={deleteTask}
          />
        )
      case "resources":
        return <ResourceManagement
          onRequestResource={() => openModal("request-resource")}
          onUpdateInventory={() => openModal("daily-inventory")}
        />
      case "documents":
        return (
          <DocumentManagement
            onUploadDocument={() => openModal("upload-document")}
            onViewFolder={(folderName) => openModal("view-folder", { folderName })}
            onDeleteDocument={deleteDocument}
          />
        )
      case "reports":
        return <ReportsSection onOpenGlobalReport={() => openModal("global-report")} />
      case "announcements":
        return <AnnouncementBoard />
      default:
        return <Dashboard onNewProject={() => openModal("new-project")} />
    }
  }

  const renderModalContent = () => {
    switch (modal.type) {
      case "new-project":
        return <ProjectCreateModal onClose={closeModal} />
      case "edit-project":
        return <ProjectCreateModal onClose={closeModal} projectId={modal.data?.projectId} />
      case "new-task":
        return <TaskCreateModal onClose={closeModal} />
      case "edit-task":
        // Assuming TaskCreateModal can handle 'taskId' prop or similar for editing
        return <TaskCreateModal onClose={closeModal} taskId={modal.data?.taskId} />
      case "request-resource":
        return <ResourceRequestModal onClose={closeModal} />
      case "upload-document":
        return <DocumentUploadModal onClose={closeModal} />
      case "daily-inventory":
        return <DailyInventoryModal onClose={closeModal} />
      case "view-folder":
        return <FolderViewModal folderName={modal.data?.folderName} onClose={closeModal} />
      case "global-report":
        return <ReportModal onClose={closeModal} />
      default:
        return null
    }
  }

  return (
    <div className="flex h-screen bg-background">
      <Sidebar currentSection={currentSection} onNavigate={setCurrentSection} />
      <main className="flex-1 overflow-auto">{renderSection()}</main>

      <Modal isOpen={modal.isOpen} onClose={closeModal}>
        {renderModalContent()}
      </Modal>
    </div>
  )
}

export default function Home() {
  return (
    <AppProvider>
      <HomeContent />
    </AppProvider>
  )
}

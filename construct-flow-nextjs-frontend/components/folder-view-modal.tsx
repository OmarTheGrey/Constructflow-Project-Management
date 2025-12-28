"use client"

import { useApp } from "@/lib/app-context"
import { FileText, Download, Trash2 } from "lucide-react"

interface FolderViewModalProps {
  folderName?: string
  onClose: () => void
}

export function FolderViewModal({ folderName = "Documents", onClose }: FolderViewModalProps) {
  const { documents } = useApp()

  const folderDocuments = documents.filter(
    (doc) => folderName?.toLowerCase().includes(doc.type) || doc.folder === folderName?.toLowerCase(),
  )

  return (
    <div className="p-6 max-w-4xl">
      <h2 className="text-2xl font-bold mb-6 text-foreground">Folder: {folderName}</h2>

      <div className="space-y-3 mb-6">
        {folderDocuments.length > 0 ? (
          folderDocuments.map((doc) => (
            <div
              key={doc.id}
              className="flex items-center justify-between p-4 border border-slate-200 rounded-lg hover:bg-slate-50"
            >
              <div className="flex items-center gap-3 flex-1">
                <FileText size={20} className="text-muted-foreground" />
                <div>
                  <p className="font-medium text-foreground">{doc.name}</p>
                  <p className="text-sm text-muted-foreground">{doc.uploadDate}</p>
                </div>
              </div>
              <div className="flex gap-2">
                <button className="p-2 hover:bg-slate-200 rounded transition-colors">
                  <Download size={18} className="text-muted-foreground" />
                </button>
                <button className="p-2 hover:bg-red-100 rounded transition-colors">
                  <Trash2 size={18} className="text-red-600" />
                </button>
              </div>
            </div>
          ))
        ) : (
          <p className="text-muted-foreground text-center py-6">No documents in this folder</p>
        )}
      </div>

      <button
        onClick={onClose}
        className="w-full px-4 py-2 bg-slate-100 text-foreground rounded-lg hover:bg-slate-200 font-medium"
      >
        Close
      </button>
    </div>
  )
}

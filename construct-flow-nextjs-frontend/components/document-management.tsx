"use client"

import { useApp } from "@/lib/app-context"
import { useState } from "react"
import { FileText, Upload, Download, Eye, Trash2, FolderOpen, Search } from "lucide-react"

export function DocumentManagement({
  onUploadDocument,
  onViewFolder,
  onDeleteDocument,
}: {
  onUploadDocument: () => void
  onViewFolder: (folderName: string) => void
  onDeleteDocument: (docId: string) => void
}) {
  const { documents, deleteDocument } = useApp()
  const [searchQuery, setSearchQuery] = useState("")
  const [filterCategory, setFilterCategory] = useState("all")

  const getCategoryColor = (category: string) => {
    switch (category) {
      case "blueprint":
        return "bg-blue-500/20 text-blue-700 border-blue-500/30"
      case "permit":
        return "bg-green-500/20 text-green-700 border-green-500/30"
      case "specification":
        return "bg-amber-500/20 text-amber-700 border-amber-500/30"
      case "contract":
        return "bg-purple-500/20 text-purple-700 border-purple-500/30"
      case "report":
        return "bg-slate-500/20 text-slate-700 border-slate-500/30"
      default:
        return "bg-muted"
    }
  }

  const filteredDocuments = documents.filter((doc) => filterCategory === "all" || doc.type === filterCategory)

  return (
    <div className="min-h-screen bg-background p-8">
      <div className="mb-8">
        <div className="flex justify-between items-start mb-6">
          <div>
            <h1 className="text-3xl font-bold text-foreground">Document Repository</h1>
            <p className="text-muted-foreground mt-2">Centralized storage for project documents and blueprints</p>
          </div>
          <button
            onClick={onUploadDocument}
            className="px-4 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors font-medium flex items-center gap-2"
          >
            <Upload size={20} /> Upload Document
          </button>
        </div>

        <div className="flex gap-4 mb-6">
          <div className="flex-1 relative">
            <Search size={20} className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground" />
            <input
              type="text"
              placeholder="Search documents..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>
          <select
            value={filterCategory}
            onChange={(e) => setFilterCategory(e.target.value)}
            className="px-4 py-2 border border-border rounded-lg bg-input text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
          >
            <option value="all">All Categories</option>
            <option value="blueprint">Blueprints</option>
            <option value="permit">Permits</option>
            <option value="specification">Specifications</option>
            <option value="contract">Contracts</option>
            <option value="report">Reports</option>
          </select>
        </div>
      </div>

      <div className="card-elevated overflow-hidden rounded-lg">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border bg-muted/50">
                <th className="text-left py-4 px-6 font-semibold text-muted-foreground text-xs uppercase">Document</th>
                <th className="text-left py-4 px-6 font-semibold text-muted-foreground text-xs uppercase">Type</th>
                <th className="text-left py-4 px-6 font-semibold text-muted-foreground text-xs uppercase">Folder</th>
                <th className="text-left py-4 px-6 font-semibold text-muted-foreground text-xs uppercase">Uploaded</th>
                <th className="text-left py-4 px-6 font-semibold text-muted-foreground text-xs uppercase">Size</th>
                <th className="text-left py-4 px-6 font-semibold text-muted-foreground text-xs uppercase">Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredDocuments.map((doc) => (
                <tr key={doc.id} className="border-b border-border hover:bg-muted/50 transition-colors">
                  <td className="py-4 px-6">
                    <div className="flex items-center gap-2">
                      <FileText size={18} className="text-muted-foreground" />
                      <span className="font-medium text-foreground">{doc.name}</span>
                    </div>
                  </td>
                  <td className="py-4 px-6">
                    <span className={`px-3 py-1 rounded-full text-xs font-medium border ${getCategoryColor(doc.type)}`}>
                      {doc.type.charAt(0).toUpperCase() + doc.type.slice(1)}
                    </span>
                  </td>
                  <td className="py-4 px-6 text-foreground">{doc.folder}</td>
                  <td className="py-4 px-6 text-foreground text-xs">{doc.uploadDate}</td>
                  <td className="py-4 px-6 text-foreground">{doc.size}</td>
                  <td className="py-4 px-6">
                    <div className="flex gap-2">
                      <button className="p-2 hover:bg-muted rounded transition-colors" title="Download">
                        <Download size={16} className="text-muted-foreground" />
                      </button>
                      <button className="p-2 hover:bg-muted rounded transition-colors" title="View">
                        <Eye size={16} className="text-muted-foreground" />
                      </button>
                      <button
                        onClick={() => {
                          deleteDocument(doc.id)
                          onDeleteDocument(doc.id)
                        }}
                        className="p-2 hover:bg-destructive/10 rounded transition-colors"
                        title="Delete"
                      >
                        <Trash2 size={16} className="text-destructive" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <div className="mt-8 grid grid-cols-3 gap-6">
        <div className="bg-card p-6 rounded-lg border border-border">
          <div className="flex items-center gap-3 mb-4">
            <FolderOpen size={24} className="text-primary" />
            <h3 className="font-semibold text-foreground">Blueprints</h3>
          </div>
          <p className="text-sm text-muted-foreground mb-3">
            {documents.filter((d) => d.type === "blueprint").length} documents
          </p>
          <button
            onClick={() => onViewFolder("Blueprints")}
            className="px-3 py-2 text-sm bg-primary/10 text-primary hover:bg-primary/20 transition-colors rounded font-medium"
          >
            View Folder
          </button>
        </div>

        <div className="bg-card p-6 rounded-lg border border-border">
          <div className="flex items-center gap-3 mb-4">
            <FolderOpen size={24} className="text-amber-500" />
            <h3 className="font-semibold text-foreground">Permits</h3>
          </div>
          <p className="text-sm text-muted-foreground mb-3">
            {documents.filter((d) => d.type === "permit").length} documents
          </p>
          <button
            onClick={() => onViewFolder("Permits")}
            className="px-3 py-2 text-sm bg-amber-500/10 text-amber-600 hover:bg-amber-500/20 transition-colors rounded font-medium"
          >
            View Folder
          </button>
        </div>

        <div className="bg-card p-6 rounded-lg border border-border">
          <div className="flex items-center gap-3 mb-4">
            <FolderOpen size={24} className="text-green-500" />
            <h3 className="font-semibold text-foreground">Contracts</h3>
          </div>
          <p className="text-sm text-muted-foreground mb-3">
            {documents.filter((d) => d.type === "contract").length} documents
          </p>
          <button
            onClick={() => onViewFolder("Contracts")}
            className="px-3 py-2 text-sm bg-green-500/10 text-green-700 hover:bg-green-500/20 transition-colors rounded font-medium"
          >
            View Folder
          </button>
        </div>
      </div>
    </div>
  )
}

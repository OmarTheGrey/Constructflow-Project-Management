"use client"

import type React from "react"

import { X } from "lucide-react"

interface ModalProps {
  isOpen: boolean
  onClose: () => void
  children: React.ReactNode
}

export function Modal({ isOpen, onClose, children }: ModalProps) {
  if (!isOpen) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <div className="bg-background rounded-lg shadow-lg max-w-2xl w-full max-h-[90vh] overflow-auto">
        <div className="flex justify-between items-center p-6 border-b border-border">
          <button onClick={onClose} className="ml-auto p-1 hover:bg-muted rounded transition-colors">
            <X size={24} className="text-foreground" />
          </button>
        </div>
        {children}
      </div>
    </div>
  )
}

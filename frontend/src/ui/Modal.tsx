  import { ModalProps } from "@/interfaces/IModal";
import { ReactNode } from "react";
import { createPortal } from "react-dom";
import {
  AiOutlineInfoCircle,
  AiOutlineCheckCircle,
  AiOutlineWarning,
  AiOutlineCloseCircle,
} from "react-icons/ai";

const iconMap: Record<string, ReactNode> = {
  information: <AiOutlineInfoCircle className="text-blue-500 w-6 h-6" />,
  success: <AiOutlineCheckCircle className="text-green-500 w-6 h-6" />,
  warning: <AiOutlineWarning className="text-yellow-500 w-6 h-6" />,
  danger: <AiOutlineCloseCircle className="text-red-500 w-6 h-6" />,
};

export default function Modal({ message, type = "information", onOk, onCancel }: ModalProps) {
  return createPortal(
    <div className="fixed inset-0 z-50 flex items-center justify-center backdrop-blur-sm bg-white/30">
      <div className="bg-white w-full max-w-sm mx-auto rounded-lg shadow-lg p-6">
        <div className="flex items-start gap-3">
          <div>{iconMap[type]}</div>
          <div className="flex-1">
            <p className="text-gray-800 font-medium">{message}</p>
          </div>
        </div>

        <div className="mt-6 flex justify-end gap-3">
          {onCancel && (
            <button
              onClick={onCancel}
              className="px-4 py-2 rounded-md border text-gray-600 hover:bg-gray-100"
            >
              Cancel
            </button>
          )}
          <button
            onClick={onOk || (() => {})} 
            className="px-4 py-2 rounded-md bg-indigo-600 text-white hover:bg-indigo-700"
          >
            OK
          </button>

        </div>
      </div>
    </div>,
    document.body
  );
}

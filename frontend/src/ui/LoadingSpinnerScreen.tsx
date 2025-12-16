import React from "react";

export default function LoadingSpinnerScreen() {
  return (
    <div className="fixed inset-0 bg-white bg-opacity-70 flex items-center justify-center z-50">
      <div className="w-12 h-12 border-4 border-indigo-500 border-t-transparent rounded-full animate-spin" />
    </div>
  );
}

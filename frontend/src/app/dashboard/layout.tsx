import Sidebar from "@/ui/Sidebar";

export default function DashboardLayout({ children }: {children: React.ReactNode}) {
    return (
        <div className="min-h-screen flex bg-gray-100">
            <Sidebar />

            <main className="flex-1 ml-16 md:p-4">
                {children}
            </main>
        </div>
    )
}
"use client"

import React, { useEffect, useState } from "react";
import { 
    fetchActiveFinancialGoals, 
    fetchCompletedFinancialGoals,
    createFinancialGoal, 
    updateFinancialGoal, 
    deleteFinancialGoal,
    addAmountToGoal,
    completeFinancialGoal,
    type FinancialGoalResponse,
    type CreateFinancialGoalRequest 
} from "@/services/financialGoal";
import LoadingSpinnerScreen from "@/ui/LoadingSpinnerScreen";
import Modal from "@/ui/Modal";
import { ModalProps } from "@/interfaces/IModal";
import { FaPlus, FaEdit, FaTrash, FaCheck, FaPiggyBank, FaChartLine, FaShoppingCart, FaCreditCard, FaBullseye } from "react-icons/fa";
import formatRupiah from "@/utils/formatRupiah";
import convertNumRupiah from "@/utils/convertNumRupiah";

export default function GoalsPage() {
    const [activeGoals, setActiveGoals] = useState<FinancialGoalResponse[]>([]);
    const [completedGoals, setCompletedGoals] = useState<FinancialGoalResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [modal, setModal] = useState<ModalProps | null>(null);
    const [showForm, setShowForm] = useState(false);
    const [showAddAmountModal, setShowAddAmountModal] = useState(false);
    const [selectedGoal, setSelectedGoal] = useState<FinancialGoalResponse | null>(null);
    const [editingGoal, setEditingGoal] = useState<FinancialGoalResponse | null>(null);
    const [addAmount, setAddAmount] = useState("");
    const [form, setForm] = useState<CreateFinancialGoalRequest & { targetAmountDisplay: string }>({
        name: "",
        description: "",
        targetAmount: 0,
        targetAmountDisplay: "",
        deadline: "",
        type: "SAVINGS",
        icon: ""
    });

    const loadData = async () => {
        try {
            const [activeRes, completedRes] = await Promise.all([
                fetchActiveFinancialGoals(),
                fetchCompletedFinancialGoals()
            ]);
            
            setActiveGoals(activeRes.data || []);
            setCompletedGoals(completedRes.data || []);
        } catch (error) {
            if (error instanceof Error) {
                setModal({ message: error.message, type: "danger" });
            } else {
                setModal({ message: "Terjadi Kesalahan", type: "danger" });
            }
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadData();
    }, []);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        
        if (name === "targetAmount") {
            const clean = value.replace(/\D/g, "");
            const formatted = convertNumRupiah(clean);
            setForm(prev => ({ ...prev, targetAmountDisplay: formatted }));
            return;
        }
        
        setForm(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsSubmitting(true);

        try {
            const cleanedTargetAmount = form.targetAmountDisplay.replace(/\D/g, "");
            const targetAmountValue = parseFloat(cleanedTargetAmount) || 0;
            
            const payload: CreateFinancialGoalRequest = {
                ...form,
                targetAmount: targetAmountValue
            };

            if (editingGoal) {
                await updateFinancialGoal(editingGoal.id, payload);
                setModal({ message: "Financial goal berhasil diupdate", type: "success" });
            } else {
                await createFinancialGoal(payload);
                setModal({ message: "Financial goal berhasil dibuat", type: "success" });
            }
            setShowForm(false);
            setEditingGoal(null);
            setForm({
                name: "",
                description: "",
                targetAmount: 0,
                targetAmountDisplay: "",
                deadline: "",
                type: "SAVINGS",
                icon: ""
            });
            loadData();
        } catch (error) {
            if (error instanceof Error) {
                setModal({ message: error.message, type: "danger" });
            } else {
                setModal({ message: "Terjadi Kesalahan", type: "danger" });
            }
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleEdit = (goal: FinancialGoalResponse) => {
        setEditingGoal(goal);
        const targetAmountStr = String(goal.target_amount);
        const formattedTargetAmount = convertNumRupiah(targetAmountStr);
        setForm({
            name: goal.name,
            description: goal.description || "",
            targetAmount: goal.target_amount,
            targetAmountDisplay: formattedTargetAmount,
            deadline: goal.deadline,
            type: goal.type as any,
            icon: goal.icon || ""
        });
        setShowForm(true);
    };

    const handleDelete = async (id: number) => {
        if (!confirm("Apakah Anda yakin ingin menghapus financial goal ini?")) return;

        try {
            await deleteFinancialGoal(id);
            setModal({ message: "Financial goal berhasil dihapus", type: "success" });
            loadData();
        } catch (error) {
            if (error instanceof Error) {
                setModal({ message: error.message, type: "danger" });
            } else {
                setModal({ message: "Terjadi Kesalahan", type: "danger" });
            }
        }
    };

    const handleAddAmountChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        const clean = value.replace(/\D/g, "");
        const formatted = convertNumRupiah(clean);
        setAddAmount(formatted);
    };

    const handleAddAmount = async () => {
        if (!selectedGoal || !addAmount) {
            setModal({ message: "Amount harus diisi", type: "danger" });
            return;
        }

        const cleanedAmount = addAmount.replace(/\D/g, "");
        const amountValue = parseFloat(cleanedAmount) || 0;
        
        if (amountValue <= 0) {
            setModal({ message: "Amount harus lebih besar dari 0", type: "danger" });
            return;
        }

        setIsSubmitting(true);
        try {
            await addAmountToGoal(selectedGoal.id, { amount: amountValue });
            setModal({ message: "Amount berhasil ditambahkan", type: "success" });
            setShowAddAmountModal(false);
            setAddAmount("");
            setSelectedGoal(null);
            loadData();
        } catch (error) {
            if (error instanceof Error) {
                setModal({ message: error.message, type: "danger" });
            } else {
                setModal({ message: "Terjadi Kesalahan", type: "danger" });
            }
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleComplete = async (id: number) => {
        try {
            await completeFinancialGoal(id);
            setModal({ message: "Financial goal berhasil diselesaikan", type: "success" });
            loadData();
        } catch (error) {
            if (error instanceof Error) {
                setModal({ message: error.message, type: "danger" });
            } else {
                setModal({ message: "Terjadi Kesalahan", type: "danger" });
            }
        }
    };

    const getGoalIcon = (type: string) => {
        switch (type) {
            case "SAVINGS": return <FaPiggyBank className="text-2xl" />;
            case "INVESTMENT": return <FaChartLine className="text-2xl" />;
            case "PURCHASE": return <FaShoppingCart className="text-2xl" />;
            case "DEBT_PAYOFF": return <FaCreditCard className="text-2xl" />;
            default: return <FaBullseye className="text-2xl" />;
        }
    };

    const getProgressColor = (percentage: number, isOverdue: boolean) => {
        if (isOverdue) return "bg-red-500";
        if (percentage >= 100) return "bg-green-500";
        if (percentage >= 75) return "bg-blue-500";
        if (percentage >= 50) return "bg-yellow-500";
        return "bg-gray-300";
    };

    const getStatusBadge = (goal: FinancialGoalResponse) => {
        if (goal.is_completed) {
            return <span className="px-2 py-1 bg-green-100 text-green-800 text-xs rounded-full">Selesai</span>;
        }
        if (goal.is_overdue) {
            return <span className="px-2 py-1 bg-red-100 text-red-800 text-xs rounded-full">Terlambat</span>;
        }
        if (goal.days_remaining <= 7) {
            return <span className="px-2 py-1 bg-yellow-100 text-yellow-800 text-xs rounded-full">Mendekati Deadline</span>;
        }
        return <span className="px-2 py-1 bg-blue-100 text-blue-800 text-xs rounded-full">Aktif</span>;
    };

    if (loading) return <LoadingSpinnerScreen />;

    return (
        <div className="p-6 space-y-6">
            <div className="flex justify-between items-center">
                <h2 className="text-2xl font-bold">Financial Goals</h2>
                <button
                    onClick={() => {
                        setShowForm(true);
                        setEditingGoal(null);
                        setForm({
                            name: "",
                            description: "",
                            targetAmount: 0,
                            targetAmountDisplay: "",
                            deadline: "",
                            type: "SAVINGS",
                            icon: ""
                        });
                    }}
                    className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-md flex items-center gap-2"
                >
                    <FaPlus />
                    Tambah Goal
                </button>
            </div>

            {showForm && (
                <div className="bg-white rounded-xl p-6 shadow-lg">
                    <h3 className="text-xl font-bold mb-4">
                        {editingGoal ? "Edit Financial Goal" : "Tambah Financial Goal Baru"}
                    </h3>
                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div>
                            <label className="block mb-1 text-sm font-medium">Nama Goal</label>
                            <input
                                type="text"
                                name="name"
                                value={form.name}
                                onChange={handleChange}
                                className="w-full border rounded px-3 py-2"
                                required
                                placeholder="Contoh: Tabungan Liburan"
                            />
                        </div>

                        <div>
                            <label className="block mb-1 text-sm font-medium">Deskripsi (Opsional)</label>
                            <textarea
                                name="description"
                                value={form.description}
                                onChange={handleChange}
                                className="w-full border rounded px-3 py-2"
                                rows={3}
                                placeholder="Deskripsi goal..."
                            />
                        </div>

                        <div>
                            <label className="block mb-1 text-sm font-medium">Target Amount</label>
                            <input
                                type="text"
                                name="targetAmount"
                                value={form.targetAmountDisplay}
                                onChange={handleChange}
                                className="w-full border rounded px-3 py-2"
                                placeholder="Contoh: Rp. 10.000.000"
                                required
                            />
                        </div>

                        <div>
                            <label className="block mb-1 text-sm font-medium">Deadline</label>
                            <input
                                type="date"
                                name="deadline"
                                value={form.deadline}
                                onChange={handleChange}
                                className="w-full border rounded px-3 py-2"
                                min={new Date().toISOString().split('T')[0]}
                                required
                            />
                        </div>

                        <div>
                            <label className="block mb-1 text-sm font-medium">Tipe Goal</label>
                            <select
                                name="type"
                                value={form.type}
                                onChange={handleChange}
                                className="w-full border rounded px-3 py-2"
                                required
                            >
                                <option value="SAVINGS">Tabungan</option>
                                <option value="INVESTMENT">Investasi</option>
                                <option value="PURCHASE">Pembelian</option>
                                <option value="DEBT_PAYOFF">Pelunasan Hutang</option>
                                <option value="OTHER">Lainnya</option>
                            </select>
                        </div>

                        <div>
                            <label className="block mb-1 text-sm font-medium">Icon (Opsional - Emoji)</label>
                            <input
                                type="text"
                                name="icon"
                                value={form.icon}
                                onChange={handleChange}
                                className="w-full border rounded px-3 py-2"
                                placeholder="Contoh: 🏖️"
                                maxLength={2}
                            />
                        </div>

                        <div className="flex gap-2">
                            <button
                                type="submit"
                                disabled={isSubmitting}
                                className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded disabled:opacity-50"
                            >
                                {isSubmitting ? "Menyimpan..." : "Simpan"}
                            </button>
                            <button
                                type="button"
                                onClick={() => {
                                    setShowForm(false);
                                    setEditingGoal(null);
                                }}
                                className="bg-gray-500 hover:bg-gray-600 text-white px-4 py-2 rounded"
                            >
                                Batal
                            </button>
                        </div>
                    </form>
                </div>
            )}

            {/* Active Goals */}
            <div>
                <h3 className="text-xl font-semibold mb-4">Goals Aktif</h3>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    {activeGoals.map(goal => (
                        <div key={goal.id} className="bg-white rounded-xl p-6 shadow-lg">
                            <div className="flex justify-between items-start mb-4">
                                <div className="flex items-center gap-3">
                                    <div className="text-3xl">
                                        {goal.icon || getGoalIcon(goal.type)}
                                    </div>
                                    <div>
                                        <h4 className="text-lg font-bold text-indigo-600">{goal.name}</h4>
                                        {getStatusBadge(goal)}
                                    </div>
                                </div>
                                <div className="flex gap-2">
                                    <button
                                        onClick={() => {
                                            setSelectedGoal(goal);
                                            setShowAddAmountModal(true);
                                        }}
                                        className="text-green-500 hover:text-green-700"
                                        title="Tambah Amount"
                                    >
                                        <FaPlus />
                                    </button>
                                    <button
                                        onClick={() => handleEdit(goal)}
                                        className="text-blue-500 hover:text-blue-700"
                                    >
                                        <FaEdit />
                                    </button>
                                    <button
                                        onClick={() => handleDelete(goal.id)}
                                        className="text-red-500 hover:text-red-700"
                                    >
                                        <FaTrash />
                                    </button>
                                </div>
                            </div>

                            {goal.description && (
                                <p className="text-sm text-gray-600 mb-4">{goal.description}</p>
                            )}

                            <div className="space-y-2 mb-4">
                                <div className="flex justify-between text-sm">
                                    <span className="text-gray-600">Progress:</span>
                                    <span className="font-semibold">{goal.progress_percentage.toFixed(1)}%</span>
                                </div>
                                <div className="w-full bg-gray-200 rounded-full h-3">
                                    <div
                                        className={`h-3 rounded-full ${getProgressColor(goal.progress_percentage, goal.is_overdue)}`}
                                        style={{ width: `${Math.min(goal.progress_percentage, 100)}%` }}
                                    />
                                </div>
                                <div className="flex justify-between text-xs text-gray-500">
                                    <span>{formatRupiah(goal.current_amount)}</span>
                                    <span>{formatRupiah(goal.target_amount)}</span>
                                </div>
                            </div>

                            <div className="space-y-1 text-sm">
                                <div className="flex justify-between">
                                    <span className="text-gray-600">Sisa:</span>
                                    <span className={`font-semibold ${
                                        goal.remaining_amount <= 0 ? 'text-green-600' : 'text-gray-800'
                                    }`}>
                                        {formatRupiah(goal.remaining_amount)}
                                    </span>
                                </div>
                                <div className="flex justify-between">
                                    <span className="text-gray-600">Deadline:</span>
                                    <span className={`font-semibold ${
                                        goal.days_remaining < 0 ? 'text-red-600' : 
                                        goal.days_remaining <= 7 ? 'text-yellow-600' : 'text-gray-800'
                                    }`}>
                                        {goal.days_remaining < 0 
                                            ? `${Math.abs(goal.days_remaining)} hari terlambat`
                                            : goal.days_remaining === 0
                                            ? "Hari ini"
                                            : `${goal.days_remaining} hari lagi`
                                        }
                                    </span>
                                </div>
                            </div>

                            {goal.progress_percentage >= 100 && !goal.is_completed && (
                                <button
                                    onClick={() => handleComplete(goal.id)}
                                    className="w-full mt-4 bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-md flex items-center justify-center gap-2"
                                >
                                    <FaCheck />
                                    Tandai Selesai
                                </button>
                            )}
                        </div>
                    ))}
                </div>

                {activeGoals.length === 0 && (
                    <div className="text-center py-12 text-gray-500 bg-white rounded-xl">
                        <FaBullseye className="mx-auto text-4xl mb-4 opacity-50" />
                        <p>Belum ada financial goal yang aktif</p>
                        <p className="text-sm">Klik "Tambah Goal" untuk membuat goal baru</p>
                    </div>
                )}
            </div>

            {/* Completed Goals */}
            {completedGoals.length > 0 && (
                <div>
                    <h3 className="text-xl font-semibold mb-4">Goals Selesai</h3>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                        {completedGoals.map(goal => (
                            <div key={goal.id} className="bg-green-50 rounded-xl p-6 shadow-lg border-2 border-green-200">
                                <div className="flex justify-between items-start mb-4">
                                    <div className="flex items-center gap-3">
                                        <div className="text-3xl">
                                            {goal.icon || getGoalIcon(goal.type)}
                                        </div>
                                        <div>
                                            <h4 className="text-lg font-bold text-green-700">{goal.name}</h4>
                                            <span className="px-2 py-1 bg-green-100 text-green-800 text-xs rounded-full">Selesai</span>
                                        </div>
                                    </div>
                                    <button
                                        onClick={() => handleDelete(goal.id)}
                                        className="text-red-500 hover:text-red-700"
                                    >
                                        <FaTrash />
                                    </button>
                                </div>
                                <div className="text-sm text-gray-600">
                                    <p>Target: {formatRupiah(goal.target_amount)}</p>
                                    <p className="text-green-600 font-semibold">Tercapai: {formatRupiah(goal.current_amount)}</p>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {/* Add Amount Modal */}
            {showAddAmountModal && selectedGoal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white rounded-xl p-6 max-w-md w-full mx-4">
                        <h3 className="text-xl font-bold mb-4">Tambah Amount ke Goal</h3>
                        <div className="mb-4">
                            <p className="text-sm text-gray-600 mb-2">Goal: <strong>{selectedGoal.name}</strong></p>
                            <p className="text-sm text-gray-600">
                                Saat ini: {formatRupiah(selectedGoal.current_amount)} / {formatRupiah(selectedGoal.target_amount)}
                            </p>
                        </div>
                        <div className="mb-4">
                            <label className="block mb-1 text-sm font-medium">Amount</label>
                            <input
                                type="text"
                                value={addAmount}
                                onChange={handleAddAmountChange}
                                className="w-full border rounded px-3 py-2"
                                placeholder="Contoh: Rp. 100.000"
                                required
                            />
                        </div>
                        <div className="flex gap-2">
                            <button
                                onClick={handleAddAmount}
                                disabled={isSubmitting}
                                className="flex-1 bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded disabled:opacity-50"
                            >
                                {isSubmitting ? "Menambahkan..." : "Tambahkan"}
                            </button>
                            <button
                                onClick={() => {
                                    setShowAddAmountModal(false);
                                    setSelectedGoal(null);
                                    setAddAmount("");
                                }}
                                className="flex-1 bg-gray-500 hover:bg-gray-600 text-white px-4 py-2 rounded"
                            >
                                Batal
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {modal && (
                <Modal
                    type={modal.type}
                    message={modal.message}
                    onOk={() => {
                        setModal(null);
                        if (modal.type === "success") {
                            setShowForm(false);
                            setShowAddAmountModal(false);
                        }
                    }}
                />
            )}
        </div>
    );
}


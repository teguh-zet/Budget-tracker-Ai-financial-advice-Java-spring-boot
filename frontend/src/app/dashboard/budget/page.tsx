"use client"

import React, { useEffect, useState } from "react";
import { 
    fetchAllBudgets, 
    fetchActiveBudgets, 
    createBudget, 
    updateBudget, 
    deleteBudget,
    type BudgetResponse,
    type CreateBudgetRequest 
} from "@/services/budget";
import { fetchAllCategories } from "@/services/category";
import LoadingSpinnerScreen from "@/ui/LoadingSpinnerScreen";
import Modal from "@/ui/Modal";
import { ModalProps } from "@/interfaces/IModal";
import { FaPlus, FaEdit, FaTrash, FaChartLine } from "react-icons/fa";
import formatRupiah from "@/utils/formatRupiah";
import convertNumRupiah from "@/utils/convertNumRupiah";

export default function BudgetPage() {
    const [budgets, setBudgets] = useState<BudgetResponse[]>([]);
    const [categories, setCategories] = useState<any[]>([]);
    const [loading, setLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [modal, setModal] = useState<ModalProps | null>(null);
    const [showForm, setShowForm] = useState(false);
    const [editingBudget, setEditingBudget] = useState<BudgetResponse | null>(null);
    const [form, setForm] = useState<CreateBudgetRequest & { amountDisplay: string }>({
        categoryId: 0,
        amount: 0,
        amountDisplay: "",
        period: "MONTHLY",
        periodStart: new Date().toISOString().slice(0, 10),
        periodEnd: "",
        description: ""
    });

    const loadData = async () => {
        try {
            const [budgetsRes, categoriesRes] = await Promise.all([
                fetchActiveBudgets(),
                fetchAllCategories("expense")
            ]);
            
            setBudgets(budgetsRes.data || []);
            setCategories(categoriesRes.data || []);
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

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        
        if (name === "amount") {
            const clean = value.replace(/\D/g, "");
            const formatted = convertNumRupiah(clean);
            setForm(prev => ({ ...prev, amountDisplay: formatted }));
            return;
        }
        
        setForm(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsSubmitting(true);

        try {
            const cleanedAmount = form.amountDisplay.replace(/\D/g, "");
            const amountValue = parseInt(cleanedAmount) || 0;
            
            const payload: CreateBudgetRequest = {
                ...form,
                amount: amountValue
            };

            if (editingBudget) {
                await updateBudget(editingBudget.id, payload);
                setModal({ message: "Budget berhasil diupdate", type: "success" });
            } else {
                await createBudget(payload);
                setModal({ message: "Budget berhasil dibuat", type: "success" });
            }
            setShowForm(false);
            setEditingBudget(null);
            setForm({
                categoryId: 0,
                amount: 0,
                amountDisplay: "",
                period: "MONTHLY",
                periodStart: new Date().toISOString().slice(0, 10),
                periodEnd: "",
                description: ""
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

    const handleEdit = (budget: BudgetResponse) => {
        setEditingBudget(budget);
        const amountStr = String(budget.amount);
        const formattedAmount = convertNumRupiah(amountStr);
        setForm({
            categoryId: budget.category_id,
            amount: budget.amount,
            amountDisplay: formattedAmount,
            period: budget.period as "MONTHLY" | "WEEKLY" | "YEARLY",
            periodStart: budget.period_start,
            periodEnd: budget.period_end || "",
            description: budget.description || ""
        });
        setShowForm(true);
    };

    const handleDelete = async (id: number) => {
        if (!confirm("Apakah Anda yakin ingin menghapus budget ini?")) return;

        try {
            await deleteBudget(id);
            setModal({ message: "Budget berhasil dihapus", type: "success" });
            loadData();
        } catch (error) {
            if (error instanceof Error) {
                setModal({ message: error.message, type: "danger" });
            } else {
                setModal({ message: "Terjadi Kesalahan", type: "danger" });
            }
        }
    };

    const getProgressColor = (percentage: number) => {
        if (percentage >= 100) return "bg-red-500";
        if (percentage >= 80) return "bg-yellow-500";
        return "bg-green-500";
    };

    if (loading) return <LoadingSpinnerScreen />;

    return (
        <div className="p-6 space-y-6">
            <div className="flex justify-between items-center">
                <h2 className="text-2xl font-bold">Budget Management</h2>
                <button
                    onClick={() => {
                        setShowForm(true);
                        setEditingBudget(null);
                        setForm({
                            categoryId: 0,
                            amount: 0,
                            amountDisplay: "",
                            period: "MONTHLY",
                            periodStart: new Date().toISOString().slice(0, 10),
                            periodEnd: "",
                            description: ""
                        });
                    }}
                    className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-md flex items-center gap-2"
                >
                    <FaPlus />
                    Tambah Budget
                </button>
            </div>

            {showForm && (
                <div className="bg-white rounded-xl p-6 shadow-lg">
                    <h3 className="text-xl font-bold mb-4">
                        {editingBudget ? "Edit Budget" : "Tambah Budget Baru"}
                    </h3>
                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div>
                            <label className="block mb-1 text-sm font-medium">Kategori</label>
                            <select
                                name="categoryId"
                                value={form.categoryId}
                                onChange={handleChange}
                                className="w-full border rounded px-3 py-2"
                                required
                            >
                                <option value={0}>-- Pilih Kategori --</option>
                                {categories.map(cat => (
                                    <option key={cat.id} value={cat.id}>{cat.name}</option>
                                ))}
                            </select>
                        </div>

                        <div>
                            <label className="block mb-1 text-sm font-medium">Jumlah Budget</label>
                            <input
                                type="text"
                                name="amount"
                                value={form.amountDisplay}
                                onChange={handleChange}
                                className="w-full border rounded px-3 py-2"
                                placeholder="Contoh: Rp. 1.000.000"
                                required
                            />
                        </div>

                        <div>
                            <label className="block mb-1 text-sm font-medium">Periode</label>
                            <select
                                name="period"
                                value={form.period}
                                onChange={handleChange}
                                className="w-full border rounded px-3 py-2"
                                required
                            >
                                <option value="MONTHLY">Bulanan</option>
                                <option value="WEEKLY">Mingguan</option>
                                <option value="YEARLY">Tahunan</option>
                            </select>
                        </div>

                        <div>
                            <label className="block mb-1 text-sm font-medium">Tanggal Mulai</label>
                            <input
                                type="date"
                                name="periodStart"
                                value={form.periodStart}
                                onChange={handleChange}
                                className="w-full border rounded px-3 py-2"
                                required
                            />
                        </div>

                        <div>
                            <label className="block mb-1 text-sm font-medium">Tanggal Akhir (Opsional)</label>
                            <input
                                type="date"
                                name="periodEnd"
                                value={form.periodEnd}
                                onChange={handleChange}
                                className="w-full border rounded px-3 py-2"
                            />
                        </div>

                        <div>
                            <label className="block mb-1 text-sm font-medium">Deskripsi (Opsional)</label>
                            <textarea
                                name="description"
                                value={form.description}
                                onChange={(e) => setForm(prev => ({ ...prev, description: e.target.value }))}
                                className="w-full border rounded px-3 py-2"
                                rows={3}
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
                                    setEditingBudget(null);
                                }}
                                className="bg-gray-500 hover:bg-gray-600 text-white px-4 py-2 rounded"
                            >
                                Batal
                            </button>
                        </div>
                    </form>
                </div>
            )}

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {budgets.map(budget => (
                    <div key={budget.id} className="bg-white rounded-xl p-6 shadow-lg">
                        <div className="flex justify-between items-start mb-4">
                            <div>
                                <h3 className="text-lg font-bold text-indigo-600">
                                    {budget.category?.name || "Semua Kategori"}
                                </h3>
                                <p className="text-sm text-gray-500">{budget.period}</p>
                            </div>
                            <div className="flex gap-2">
                                <button
                                    onClick={() => handleEdit(budget)}
                                    className="text-blue-500 hover:text-blue-700"
                                >
                                    <FaEdit />
                                </button>
                                <button
                                    onClick={() => handleDelete(budget.id)}
                                    className="text-red-500 hover:text-red-700"
                                >
                                    <FaTrash />
                                </button>
                            </div>
                        </div>

                        <div className="space-y-2 mb-4">
                            <div className="flex justify-between text-sm">
                                <span className="text-gray-600">Budget:</span>
                                <span className="font-semibold">{formatRupiah(budget.amount)}</span>
                            </div>
                            <div className="flex justify-between text-sm">
                                <span className="text-gray-600">Terpakai:</span>
                                <span className="font-semibold">{formatRupiah(budget.spent_amount)}</span>
                            </div>
                            <div className="flex justify-between text-sm">
                                <span className="text-gray-600">Sisa:</span>
                                <span className={`font-semibold ${
                                    budget.remaining_amount < 0 ? 'text-red-600' : 'text-green-600'
                                }`}>
                                    {formatRupiah(budget.remaining_amount)}
                                </span>
                            </div>
                        </div>

                        <div className="mb-2">
                            <div className="flex justify-between text-xs mb-1">
                                <span>Progress</span>
                                <span className="font-semibold">{budget.usage_percentage.toFixed(1)}%</span>
                            </div>
                            <div className="w-full bg-gray-200 rounded-full h-2">
                                <div
                                    className={`h-2 rounded-full ${getProgressColor(budget.usage_percentage)}`}
                                    style={{ width: `${Math.min(budget.usage_percentage, 100)}%` }}
                                />
                            </div>
                        </div>

                        {budget.description && (
                            <p className="text-xs text-gray-500 mt-2">{budget.description}</p>
                        )}
                    </div>
                ))}
            </div>

            {budgets.length === 0 && !showForm && (
                <div className="text-center py-12 text-gray-500">
                    <FaChartLine className="mx-auto text-4xl mb-4 opacity-50" />
                    <p>Belum ada budget yang dibuat</p>
                    <p className="text-sm">Klik "Tambah Budget" untuk membuat budget baru</p>
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
                        }
                    }}
                />
            )}
        </div>
    );
}



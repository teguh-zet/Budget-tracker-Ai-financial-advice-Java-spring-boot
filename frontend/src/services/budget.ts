import api from "@/api";
import { handleApiError } from "@/utils/handleApiError";
import getTokenHeader from "@/utils/getTokenHeader";

export interface CreateBudgetRequest {
    categoryId: number;
    amount: number;
    period: "MONTHLY" | "WEEKLY" | "YEARLY";
    periodStart: string;
    periodEnd?: string;
    description?: string;
}

export interface UpdateBudgetRequest {
    categoryId?: number;
    amount?: number;
    period?: "MONTHLY" | "WEEKLY" | "YEARLY";
    periodStart?: string;
    periodEnd?: string;
    description?: string;
    isActive?: boolean;
}

export interface BudgetResponse {
    id: number;
    user_id: number;
    category_id: number;
    category?: {
        id: number;
        name: string;
        type: string;
    };
    amount: number;
    period: string;
    period_start: string;
    period_end?: string;
    description?: string;
    is_active: boolean;
    spent_amount: number;
    remaining_amount: number;
    usage_percentage: number;
    created_at: string;
    updated_at: string;
}

export const fetchAllBudgets = async () => {
    try {
        const res = await api.get('/budget', {
            headers: getTokenHeader()
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Budget Error");
    }
}

export const fetchActiveBudgets = async () => {
    try {
        const res = await api.get('/budget/active', {
            headers: getTokenHeader()
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Budget Error");
    }
}

export const fetchBudgetById = async (id: number) => {
    try {
        const res = await api.get(`/budget/${id}`, {
            headers: getTokenHeader()
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Budget Error");
    }
}

export const createBudget = async (data: CreateBudgetRequest) => {
    try {
        const res = await api.post('/budget', data, {
            headers: getTokenHeader()
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Budget Error");
    }
}

export const updateBudget = async (id: number, data: UpdateBudgetRequest) => {
    try {
        const res = await api.put(`/budget/${id}`, data, {
            headers: getTokenHeader()
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Budget Error");
    }
}

export const deleteBudget = async (id: number) => {
    try {
        const res = await api.delete(`/budget/${id}`, {
            headers: getTokenHeader()
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Budget Error");
    }
}



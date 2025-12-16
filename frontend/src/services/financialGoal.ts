import api from "@/api";
import { handleApiError } from "@/utils/handleApiError";
import getTokenHeader from "@/utils/getTokenHeader";

export interface CreateFinancialGoalRequest {
    name: string;
    description?: string;
    targetAmount: number;
    deadline: string; // YYYY-MM-DD
    type: "SAVINGS" | "INVESTMENT" | "PURCHASE" | "DEBT_PAYOFF" | "OTHER";
    icon?: string;
}

export interface UpdateFinancialGoalRequest {
    name?: string;
    description?: string;
    targetAmount?: number;
    deadline?: string;
    type?: "SAVINGS" | "INVESTMENT" | "PURCHASE" | "DEBT_PAYOFF" | "OTHER";
    status?: "ACTIVE" | "COMPLETED" | "PAUSED" | "CANCELLED";
    icon?: string;
    currentAmount?: number;
}

export interface AddAmountToGoalRequest {
    amount: number;
    note?: string;
}

export interface FinancialGoalResponse {
    id: number;
    user_id: number;
    name: string;
    description?: string;
    target_amount: number;
    current_amount: number;
    deadline: string;
    type: string;
    status: string;
    icon?: string;
    progress_percentage: number;
    remaining_amount: number;
    days_remaining: number;
    is_completed: boolean;
    is_overdue: boolean;
    created_at: string;
    updated_at: string;
}

export const fetchAllFinancialGoals = async () => {
    try {
        const res = await api.get('/financial-goals', {
            headers: getTokenHeader()
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Financial Goals Error");
    }
}

export const fetchActiveFinancialGoals = async () => {
    try {
        const res = await api.get('/financial-goals/active', {
            headers: getTokenHeader()
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Financial Goals Error");
    }
}

export const fetchCompletedFinancialGoals = async () => {
    try {
        const res = await api.get('/financial-goals/completed', {
            headers: getTokenHeader()
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Financial Goals Error");
    }
}

export const fetchFinancialGoalById = async (id: number) => {
    try {
        const res = await api.get(`/financial-goals/${id}`, {
            headers: getTokenHeader()
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Financial Goals Error");
    }
}

export const createFinancialGoal = async (data: CreateFinancialGoalRequest) => {
    try {
        const res = await api.post('/financial-goals', data, {
            headers: getTokenHeader()
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Financial Goals Error");
    }
}

export const updateFinancialGoal = async (id: number, data: UpdateFinancialGoalRequest) => {
    try {
        const res = await api.put(`/financial-goals/${id}`, data, {
            headers: getTokenHeader()
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Financial Goals Error");
    }
}

export const deleteFinancialGoal = async (id: number) => {
    try {
        const res = await api.delete(`/financial-goals/${id}`, {
            headers: getTokenHeader()
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Financial Goals Error");
    }
}

export const addAmountToGoal = async (id: number, data: AddAmountToGoalRequest) => {
    try {
        const res = await api.post(`/financial-goals/${id}/add-amount`, data, {
            headers: getTokenHeader()
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Financial Goals Error");
    }
}

export const completeFinancialGoal = async (id: number) => {
    try {
        const res = await api.post(`/financial-goals/${id}/complete`, {}, {
            headers: getTokenHeader()
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Financial Goals Error");
    }
}



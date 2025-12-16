import api from "@/api";
import { handleApiError } from "@/utils/handleApiError";
import getTokenHeader from "@/utils/getTokenHeader";

export const fetchAllCategories = async (type?: string) => {
    try {
        const params = type ? { type } : {};
        const res = await api.get('/category', {
            params,
            headers: getTokenHeader(),
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Failed to fetch categories");
    }
}

export const fetchCategoryById = async (id: number) => {
    try {
        const res = await api.get(`/category/${id}`, {
            headers: getTokenHeader(),
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Failed to fetch category id");
    }
}

export const createCategory = async (data: {name: string; description?: string}) => {
    try {
        const res = await api.post('/category', data, {
            headers: getTokenHeader(),
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Failed to fetch categories");
    }
}

export const updateCategory = async (id: number, data: {name: string; description?: string}) => {
    try {
        const res = await api.put(`/category/${id}`, data, {
            headers: getTokenHeader(),
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Failed to fetch categories");
    }
}

export const deleteCategory = async (id: number) => {
    try {
        const res = await api.delete(`/category/${id}`, {
            headers: getTokenHeader(),
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Failed to fetch categories");
    }
}
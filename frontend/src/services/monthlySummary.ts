import api from "@/api";
import { handleApiError } from "@/utils/handleApiError";
import getTokenHeader from "@/utils/getTokenHeader";

export const fetchAllMonthlySummaries = async () => {
    try {
        const res = await api.get('/monthly-summary', {
            headers: getTokenHeader()
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Monthly Summary Error");
    }
}

export const fetchAllMonthlySummaryById = async (id: number) => {
    try {
        const res = await api.get(`/monthly-summary/${id}`, {
            headers: getTokenHeader()
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Monthly Summary Error");
    }
}
export const createMonthlySummary = async (data: Record<string, unknown>) => {
    try {
        const res = await api.post('/monthly-summary', data, {
            headers: getTokenHeader()
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Monthly Summary Error");
    }
}
export const updateMonthlySummary = async (id: number, data: Record<string, unknown>) => {
    try {
        const res = await api.put(`/monthly-summary/${id}`, data, {
            headers: getTokenHeader()
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Monthly Summary Error");
    }
}
export const deleteMonthlySummary = async (id: number) => {
    try {
        const res = await api.delete(`/monthly-summary/${id}`, {
            headers: getTokenHeader()
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Monthly Summary Error");
    }
}

export const generateMonthlySummary = async () => {
    try {
        const res = await api.post('/monthly-summary/generate', {}, {
            headers: getTokenHeader(),
            timeout: 300000
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Monthly Summary Error");
    }
}

export const downloadSummaryPDF = async (id: number) => {
    try {
        const res = await api.get(`/monthly-summary/${id}/export-pdf`, {
            headers: getTokenHeader(),
            responseType: 'blob',
            timeout: 60000 // 60 seconds timeout for PDF generation
        });
        
        // Check if response is actually a blob
        if (!(res.data instanceof Blob)) {
            throw new Error("Response is not a valid PDF file");
        }
        
        // Create blob and download
        const blob = new Blob([res.data], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `Financial_Summary_${id}.pdf`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
        
        return { success: true };
    } catch (error: any) {
        // Handle blob error response
        if (error.response && error.response.data instanceof Blob) {
            const reader = new FileReader();
            reader.onload = () => {
                try {
                    const text = reader.result as string;
                    const json = JSON.parse(text);
                    throw new Error(json.message || "Gagal mengunduh PDF");
                } catch (e) {
                    throw new Error("Gagal mengunduh PDF: " + error.message);
                }
            };
            reader.readAsText(error.response.data);
        }
        handleApiError(error, "PDF Export Error");
        throw error;
    }
}
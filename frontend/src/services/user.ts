import api from "@/api"
import { handleApiError } from "@/utils/handleApiError"
import getTokenHeader from "@/utils/getTokenHeader"

export const fetchAllUser = async() => {
    try {
        const res = await api.get("/users", {
            headers: getTokenHeader()
        });
        return res.data
    } catch (error) {
        handleApiError(error, "User Service Error")
    }
}

export const fetchUserById = async(id: number) => {
    try {
        const res = await api.get(`/users/${id}`, {
            headers: getTokenHeader()
        });
        return res.data
    } catch (error) {
        handleApiError(error, "User Service Error")
    }
}

export const createUser = async(data: Record<string, unknown>) => {
    try {
        const res = await api.post("/users", data, {
            headers: getTokenHeader()
        });
        return res.data
    } catch (error) {
        handleApiError(error, "User Service Error")
    }
}

export const updateUser = async(id: number, data: Record<string, unknown>) => {
    try {
        const res = await api.put(`/users/${id}`, data, {
            headers: getTokenHeader()
        });
        return res.data
    } catch (error) {
        handleApiError(error, "User Service Error")
    }
}

export const deleteUser = async(id: number) => {
    try {
        const res = await api.delete(`/users/${id}`, {
            headers: getTokenHeader()
        });
        return res.data
    } catch (error) {
        handleApiError(error, "User Service Error")
    }
}

export const uploadProfilePicture = async (file: File) => {
    try {
        const formData = new FormData();
        formData.append('file', file);
        
        const res = await api.post('/users/profile/picture', formData, {
            headers: {
                ...getTokenHeader(),
                'Content-Type': 'multipart/form-data'
            }
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Profile Picture Upload Error");
    }
}

export const getProfilePictureUrl = (profilePicture: string | null | undefined, userId?: number): string => {
    if (!profilePicture) {
        // Return empty string, will be handled by component to show initial
        return '';
    }
    // If it's already a full URL, return as is
    if (profilePicture.startsWith('http')) {
        return profilePicture;
    }
    // Construct full URL from API endpoint
    // Backend returns URL like: /api/v1/users/profile/picture?userId=1
    const baseURL = process.env.NEXT_PUBLIC_API_DEV_BASE_URL_V1 || 'http://localhost:5001/api/v1';
    const apiBase = baseURL.replace('/api/v1', '');
    
    // If profilePicture already starts with /api/v1, use it directly
    if (profilePicture.startsWith('/api/v1')) {
        return `${apiBase}${profilePicture}`;
    }
    
    // Otherwise, assume it's a relative path and construct URL
    return `${apiBase}${profilePicture}`;
}

// Function to get profile picture as blob URL (for authenticated requests)
export const getProfilePictureBlobUrl = async (profilePicture: string | null | undefined, userId?: number): Promise<string | null> => {
    if (!profilePicture) {
        return null;
    }
    
    try {
        const url = getProfilePictureUrl(profilePicture, userId);
        if (!url) return null;
        
        const token = typeof window !== "undefined" ? localStorage.getItem("token") : null;
        if (!token) return null;
        
        const response = await fetch(url, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (!response.ok) {
            return null;
        }
        
        const blob = await response.blob();
        return URL.createObjectURL(blob);
    } catch (error) {
        console.error("Error loading profile picture:", error);
        return null;
    }
}

export const deleteProfilePicture = async () => {
    try {
        const res = await api.delete('/users/profile/picture', {
            headers: getTokenHeader()
        });
        return res.data;
    } catch (error) {
        handleApiError(error, "Profile Picture Delete Error");
    }
}
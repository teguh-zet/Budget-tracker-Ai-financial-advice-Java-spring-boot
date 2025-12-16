
import api from "@/api";
import { LoginData, RegisterData } from "@/interfaces/IAuth";
import { handleApiError } from "@/utils/handleApiError";

export const login = async (userDataLogin: LoginData) => {
    try {
        const response = await api.post("/auth/login", userDataLogin);
        return response.data
    } catch (error) {
        handleApiError(error, "Login Failed");
    }
}

export const register = async (userDataRegist: RegisterData) => {
    try {
        const response = await api.post("/auth/register", userDataRegist);
        return response.data
    } catch (error) {
        handleApiError(error, "Register Failed");
    }
}

export const profile = async (token: string) => {
    try {
        const response = await api.get("/auth/profile", {
            headers: { Authorization: `Bearer ${token}` }
        });
        return response.data
    } catch (error) {
        handleApiError(error, "Get Profile Failed");
    }
}

export const logout = () => {
    localStorage.removeItem("token");
}
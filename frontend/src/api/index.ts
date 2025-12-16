import axios from "axios";

// Use production URL in production, otherwise use dev URL
const baseURL = process.env.NODE_ENV === 'production' 
    ? process.env.NEXT_PUBLIC_API_PROD_BASE_URL_V1 
    : process.env.NEXT_PUBLIC_API_DEV_BASE_URL_V1 || 'http://localhost:5001/api/v1';

const api = axios.create({
    baseURL,
    timeout: 10000,
    headers: {
        "Content-Type": "application/json"
    }
})

export default api;
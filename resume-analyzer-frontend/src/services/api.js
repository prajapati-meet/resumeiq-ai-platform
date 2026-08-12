import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL;

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    }
});

api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

export const authApi = {
    register: (data) => api.post('/api/auth/register', data),
    login: (data) => api.post('/api/auth/login', data),
};

export const resumeApi = {
    upload: (formData) => api.post('/api/resume/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    }),
    getAnalysis: (resumeId) => api.get(`/api/resume/analysis/${resumeId}`),
};

export const aiApi = {
    getSuggestion: (resumeId) => api.get(`/api/ai/suggestion/${resumeId}`),
};

export default api;
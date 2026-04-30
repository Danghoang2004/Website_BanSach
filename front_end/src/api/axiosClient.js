import axios from 'axios';
import { API_BASE_URL } from '../constants/env';
import { tokenService } from '../services/tokenService';

const axiosClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

axiosClient.interceptors.request.use((config) => {
    const token = tokenService.getToken();
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

axiosClient.interceptors.response.use(
    (response) => response,
    (error) => {
        const message =
            error?.response?.data?.message ||
            error?.response?.data?.error ||
            error?.message ||
            'Co loi xay ra khi goi API';

        return Promise.reject(new Error(message));
    },
);

export default axiosClient;

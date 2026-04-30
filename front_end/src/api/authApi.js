import axiosClient from './axiosClient';
import { API_ENDPOINTS } from '../constants/apiEndpoints';

export const authApi = {
    login: async (payload) => {
        const response = await axiosClient.post(API_ENDPOINTS.AUTH.LOGIN, payload);
        return response.data;
    },
    register: async (payload) => {
        const response = await axiosClient.post(API_ENDPOINTS.AUTH.REGISTER, payload);
        return response.data;
    },
};

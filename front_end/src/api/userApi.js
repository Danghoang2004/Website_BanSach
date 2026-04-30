import axiosClient from './axiosClient';
import { API_ENDPOINTS } from '../constants/apiEndpoints';

export const userApi = {
    updateProfile: async (payload) => {
        const response = await axiosClient.put(API_ENDPOINTS.USER.UPDATE_PROFILE, payload);
        return response.data;
    },
};

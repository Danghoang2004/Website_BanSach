import axiosClient from './axiosClient';
import { API_ENDPOINTS } from '../constants/apiEndpoints';

export const orderApi = {
    createOrder: async (payload) => {
        const response = await axiosClient.post(API_ENDPOINTS.ORDERS.ROOT, payload);
        return response.data;
    },
    getMyOrders: async ({ page = 0, size = 10 } = {}) => {
        const response = await axiosClient.get(API_ENDPOINTS.ORDERS.ROOT, {
            params: { page, size },
        });
        return response.data;
    },
    getOrderDetail: async (id) => {
        const response = await axiosClient.get(API_ENDPOINTS.ORDERS.DETAIL(id));
        return response.data;
    },
    cancelOrder: async (id) => {
        const response = await axiosClient.delete(API_ENDPOINTS.ORDERS.CANCEL(id));
        return response.data;
    },
};

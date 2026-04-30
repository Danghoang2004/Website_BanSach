import axiosClient from './axiosClient';
import { API_ENDPOINTS } from '../constants/apiEndpoints';

export const adminApi = {
    getProducts: async ({ page = 0, size = 10 } = {}) => {
        const response = await axiosClient.get(API_ENDPOINTS.ADMIN.PRODUCTS, {
            params: { page, size },
        });
        return response.data;
    },
    deleteProduct: async (id) => {
        const response = await axiosClient.delete(API_ENDPOINTS.ADMIN.PRODUCT_DETAIL(id));
        return response.data;
    },
    getOrders: async ({ page = 0, size = 10 } = {}) => {
        const response = await axiosClient.get(API_ENDPOINTS.ADMIN.ORDERS, {
            params: { page, size },
        });
        return response.data;
    },
};

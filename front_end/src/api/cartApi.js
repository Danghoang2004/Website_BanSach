import axiosClient from './axiosClient';
import { API_ENDPOINTS } from '../constants/apiEndpoints';

export const cartApi = {
    getCart: async () => {
        const response = await axiosClient.get(API_ENDPOINTS.CART.ROOT);
        return response.data;
    },
    addItem: async (payload) => {
        const response = await axiosClient.post(API_ENDPOINTS.CART.ITEMS, payload);
        return response.data;
    },
    updateItem: async (itemId, payload) => {
        const response = await axiosClient.put(API_ENDPOINTS.CART.ITEM_DETAIL(itemId), payload);
        return response.data;
    },
    removeItem: async (itemId) => {
        const response = await axiosClient.delete(API_ENDPOINTS.CART.ITEM_DETAIL(itemId));
        return response.data;
    },
    clearCart: async () => {
        const response = await axiosClient.delete(API_ENDPOINTS.CART.ROOT);
        return response.data;
    },
};

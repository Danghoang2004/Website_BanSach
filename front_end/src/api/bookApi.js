import axiosClient from './axiosClient';
import { API_ENDPOINTS } from '../constants/apiEndpoints';

export const bookApi = {
    getBooks: async ({ page = 0, size = 8, keyword = '' } = {}) => {
        if (keyword) {
            const response = await axiosClient.get(API_ENDPOINTS.BOOKS.SEARCH, {
                params: { keyword, page, size },
            });
            return response.data;
        }

        const response = await axiosClient.get(API_ENDPOINTS.BOOKS.LIST, {
            params: { page, size },
        });
        return response.data;
    },

    getBookDetail: async (id) => {
        const response = await axiosClient.get(API_ENDPOINTS.BOOKS.DETAIL(id));
        return response.data;
    },
};

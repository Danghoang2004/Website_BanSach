export const API_ENDPOINTS = {
    AUTH: {
        LOGIN: '/tai-khoan/dang-nhap',
        REGISTER: '/tai-khoan/dang-ky',
    },
    BOOKS: {
        LIST: '/products',
        DETAIL: (id) => `/products/${id}`,
        SEARCH: '/products/search',
        CLASSIFY: '/products/classify',
    },
    CART: {
        ROOT: '/cart',
        ITEMS: '/cart/items',
        ITEM_DETAIL: (itemId) => `/cart/items/${itemId}`,
    },
    ORDERS: {
        ROOT: '/orders',
        DETAIL: (id) => `/orders/${id}`,
        CANCEL: (id) => `/orders/${id}`,
    },
    USER: {
        UPDATE_PROFILE: '/user/update-profile',
    },
    ADMIN: {
        PRODUCTS: '/api/products',
        PRODUCT_DETAIL: (id) => `/api/products/${id}`,
        ORDERS: '/api/admin/orders',
    },
};

import { createSlice } from '@reduxjs/toolkit';
import { storageService } from '../services/storageService';
import { tokenService } from '../services/tokenService';

const AUTH_USER_KEY = 'bookstore_auth_user';

const savedUser = storageService.get(AUTH_USER_KEY, null);

const authSlice = createSlice({
    name: 'auth',
    initialState: {
        user: savedUser,
        isAuthenticated: Boolean(savedUser && tokenService.getToken()),
    },
    reducers: {
        setCredentials: (state, action) => {
            const payload = action.payload;
            state.user = {
                userId: payload.userId,
                username: payload.username,
                roles: payload.roles || [],
            };
            state.isAuthenticated = true;

            tokenService.setToken(payload.jwt);
            storageService.set(AUTH_USER_KEY, state.user);
        },
        logout: (state) => {
            state.user = null;
            state.isAuthenticated = false;
            tokenService.clearToken();
            storageService.remove(AUTH_USER_KEY);
        },
    },
});

export const { setCredentials, logout } = authSlice.actions;
export default authSlice.reducer;

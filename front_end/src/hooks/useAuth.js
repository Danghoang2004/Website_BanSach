import { useMemo } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { hasRole } from '../constants/roles';
import { logout } from '../store/authSlice';

export const useAuth = () => {
    const dispatch = useDispatch();
    const { user, isAuthenticated } = useSelector((state) => state.auth);

    const api = useMemo(
        () => ({
            user,
            isAuthenticated,
            hasRole: (role) => hasRole(user?.roles || [], role),
            logout: () => dispatch(logout()),
        }),
        [dispatch, isAuthenticated, user],
    );

    return api;
};

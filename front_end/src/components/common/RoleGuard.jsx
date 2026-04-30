import { Navigate } from 'react-router-dom';
import { ROUTES } from '../../constants/routes';
import { useAuth } from '../../hooks/useAuth';

const RoleGuard = ({ role, children }) => {
    const auth = useAuth();

    if (!auth.isAuthenticated) {
        return <Navigate to={ROUTES.LOGIN} replace />;
    }

    if (!auth.hasRole(role)) {
        return <Navigate to={ROUTES.HOME} replace />;
    }

    return children;
};

export default RoleGuard;

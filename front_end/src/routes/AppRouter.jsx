import { Navigate, Route, Routes } from 'react-router-dom';
import { ROLES } from '../constants/roles';
import { ROUTES } from '../constants/routes';
import RoleGuard from '../components/common/RoleGuard';
import ProtectedRoute from '../components/common/ProtectedRoute';
import AdminLayout from '../layouts/AdminLayout';
import UserLayout from '../layouts/UserLayout';
import LoginPage from '../pages/auth/LoginPage';
import RegisterPage from '../pages/auth/RegisterPage';
import AdminDashboardPage from '../pages/admin/AdminDashboardPage';
import BookDetailPage from '../pages/shop/BookDetailPage';
import CartPage from '../pages/shop/CartPage';
import CheckoutPage from '../pages/shop/CheckoutPage';
import HomePage from '../pages/shop/HomePage';
import OrdersPage from '../pages/shop/OrdersPage';
import ProfilePage from '../pages/shop/ProfilePage';

const AppRouter = () => {
    return (
        <Routes>
            <Route element={<UserLayout />}>
                <Route path={ROUTES.HOME} element={<HomePage />} />
                <Route path={ROUTES.BOOK_DETAIL} element={<BookDetailPage />} />
                <Route path={ROUTES.LOGIN} element={<LoginPage />} />
                <Route path={ROUTES.REGISTER} element={<RegisterPage />} />

                <Route element={<ProtectedRoute />}>
                    <Route path={ROUTES.CART} element={<CartPage />} />
                    <Route path={ROUTES.CHECKOUT} element={<CheckoutPage />} />
                    <Route path={ROUTES.ORDERS} element={<OrdersPage />} />
                    <Route path={ROUTES.PROFILE} element={<ProfilePage />} />
                </Route>
            </Route>

            <Route
                path={ROUTES.ADMIN_DASHBOARD}
                element={
                    <RoleGuard role={ROLES.ADMIN}>
                        <AdminLayout />
                    </RoleGuard>
                }
            >
                <Route index element={<AdminDashboardPage />} />
            </Route>

            <Route path="*" element={<Navigate to={ROUTES.HOME} replace />} />
        </Routes>
    );
};

export default AppRouter;

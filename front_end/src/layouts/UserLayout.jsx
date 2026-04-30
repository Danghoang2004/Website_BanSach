import { Link, NavLink, Outlet } from 'react-router-dom';
import { ROUTES } from '../constants/routes';
import { useAuth } from '../hooks/useAuth';

const UserLayout = () => {
    const auth = useAuth();

    return (
        <div className="app-shell">
            <header className="site-header">
                <Link to={ROUTES.HOME} className="brand">
                    BookVerse
                </Link>

                <nav className="site-nav">
                    <NavLink to={ROUTES.HOME}>Trang chu</NavLink>
                    <NavLink to={ROUTES.CART}>Gio hang</NavLink>
                    <NavLink to={ROUTES.ORDERS}>Don hang</NavLink>
                    <NavLink to={ROUTES.PROFILE}>Ho so</NavLink>
                    {auth.hasRole('ADMIN') && <NavLink to={ROUTES.ADMIN_DASHBOARD}>Admin</NavLink>}
                </nav>

                <div className="auth-actions">
                    {auth.isAuthenticated ? (
                        <>
                            <span>Xin chao, {auth.user?.username}</span>
                            <button onClick={auth.logout}>Dang xuat</button>
                        </>
                    ) : (
                        <>
                            <Link to={ROUTES.LOGIN}>Dang nhap</Link>
                            <Link to={ROUTES.REGISTER}>Dang ky</Link>
                        </>
                    )}
                </div>
            </header>

            <main className="container">
                <Outlet />
            </main>
        </div>
    );
};

export default UserLayout;

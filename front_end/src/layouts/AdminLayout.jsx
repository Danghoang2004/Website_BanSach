import { Link, NavLink, Outlet } from 'react-router-dom';
import { ROUTES } from '../constants/routes';

const AdminLayout = () => {
    return (
        <div className="admin-shell">
            <aside className="admin-sidebar">
                <h2>Admin Panel</h2>
                <NavLink to={ROUTES.ADMIN_DASHBOARD}>Tong quan</NavLink>
                <NavLink to={ROUTES.HOME}>Ve trang ban hang</NavLink>
            </aside>

            <section className="admin-content">
                <header className="admin-header">
                    <Link to={ROUTES.HOME}>BookVerse</Link>
                    <p>Quan tri san pham va don hang</p>
                </header>
                <Outlet />
            </section>
        </div>
    );
};

export default AdminLayout;

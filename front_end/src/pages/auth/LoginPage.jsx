import { useState } from 'react';
import { useDispatch } from 'react-redux';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { authApi } from '../../api/authApi';
import { ROUTES } from '../../constants/routes';
import { setCredentials } from '../../store/authSlice';

const LoginPage = () => {
    const [form, setForm] = useState({ username: '', password: '' });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const location = useLocation();

    const onSubmit = async (event) => {
        event.preventDefault();
        setError('');
        setLoading(true);

        try {
            const data = await authApi.login(form);
            dispatch(setCredentials(data));
            navigate(location.state?.from?.pathname || ROUTES.HOME, { replace: true });
        } catch (apiError) {
            setError(apiError.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <section className="card auth-card">
            <h1>Dang nhap</h1>
            <form onSubmit={onSubmit} className="form-grid">
                <label>
                    Username
                    <input
                        value={form.username}
                        onChange={(event) => setForm({ ...form, username: event.target.value })}
                        required
                    />
                </label>
                <label>
                    Mat khau
                    <input
                        type="password"
                        value={form.password}
                        onChange={(event) => setForm({ ...form, password: event.target.value })}
                        required
                    />
                </label>

                {error && <p className="error-text">{error}</p>}

                <button disabled={loading} type="submit">
                    {loading ? 'Dang xu ly...' : 'Dang nhap'}
                </button>
            </form>

            <p>
                Chua co tai khoan? <Link to={ROUTES.REGISTER}>Dang ky ngay</Link>
            </p>
        </section>
    );
};

export default LoginPage;

import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authApi } from '../../api/authApi';
import { ROUTES } from '../../constants/routes';

const defaultForm = {
    username: '',
    password: '',
    fullName: '',
    email: '',
    phone: '',
    address: '',
};

const RegisterPage = () => {
    const [form, setForm] = useState(defaultForm);
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const onSubmit = async (event) => {
        event.preventDefault();
        setError('');
        setMessage('');
        setLoading(true);

        try {
            await authApi.register(form);
            setMessage('Dang ky thanh cong. Ban se duoc chuyen den trang dang nhap.');
            setTimeout(() => navigate(ROUTES.LOGIN), 1200);
        } catch (apiError) {
            setError(apiError.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <section className="card auth-card">
            <h1>Tao tai khoan</h1>
            <form onSubmit={onSubmit} className="form-grid two-columns">
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

                <label>
                    Ho ten
                    <input
                        value={form.fullName}
                        onChange={(event) => setForm({ ...form, fullName: event.target.value })}
                        required
                    />
                </label>

                <label>
                    Email
                    <input
                        type="email"
                        value={form.email}
                        onChange={(event) => setForm({ ...form, email: event.target.value })}
                        required
                    />
                </label>

                <label>
                    So dien thoai
                    <input
                        value={form.phone}
                        onChange={(event) => setForm({ ...form, phone: event.target.value })}
                    />
                </label>

                <label>
                    Dia chi
                    <input
                        value={form.address}
                        onChange={(event) => setForm({ ...form, address: event.target.value })}
                    />
                </label>

                {error && <p className="error-text full-width">{error}</p>}
                {message && <p className="success-text full-width">{message}</p>}

                <button disabled={loading} type="submit" className="full-width">
                    {loading ? 'Dang xu ly...' : 'Dang ky'}
                </button>
            </form>

            <p>
                Da co tai khoan? <Link to={ROUTES.LOGIN}>Dang nhap</Link>
            </p>
        </section>
    );
};

export default RegisterPage;

import { useState } from 'react';
import { userApi } from '../../api/userApi';
import { useAuth } from '../../hooks/useAuth';

const defaultForm = {
    fullName: '',
    address: '',
    phone: '',
    email: '',
};

const ProfilePage = () => {
    const auth = useAuth();
    const [form, setForm] = useState(defaultForm);
    const [error, setError] = useState('');
    const [message, setMessage] = useState('');
    const [loading, setLoading] = useState(false);

    const onSubmit = async (event) => {
        event.preventDefault();
        setError('');
        setMessage('');
        setLoading(true);

        try {
            await userApi.updateProfile(form);
            setMessage('Cap nhat thong tin thanh cong.');
        } catch (apiError) {
            setError(apiError.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <section className="card auth-card">
            <h1>Thong tin ca nhan</h1>
            <p>Dang dang nhap voi username: {auth.user?.username}</p>

            <form onSubmit={onSubmit} className="form-grid">
                <label>
                    Ho ten
                    <input
                        value={form.fullName}
                        onChange={(event) => setForm({ ...form, fullName: event.target.value })}
                        required
                    />
                </label>
                <label>
                    Dia chi
                    <input
                        value={form.address}
                        onChange={(event) => setForm({ ...form, address: event.target.value })}
                    />
                </label>
                <label>
                    So dien thoai
                    <input
                        value={form.phone}
                        onChange={(event) => setForm({ ...form, phone: event.target.value })}
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

                {error && <p className="error-text">{error}</p>}
                {message && <p className="success-text">{message}</p>}

                <button disabled={loading} type="submit">
                    {loading ? 'Dang cap nhat...' : 'Luu thay doi'}
                </button>
            </form>
        </section>
    );
};

export default ProfilePage;

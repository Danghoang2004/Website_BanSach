import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { orderApi } from '../../api/orderApi';
import { ROUTES } from '../../constants/routes';

const defaultForm = {
    receiverName: '',
    receiverPhone: '',
    shippingAddress: '',
    shippingMethod: 'Giao hang tieu chuan',
    shippingFee: 20000,
    paymentMethod: 'COD',
};

const CheckoutPage = () => {
    const [form, setForm] = useState(defaultForm);
    const [error, setError] = useState('');
    const [message, setMessage] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const onSubmit = async (event) => {
        event.preventDefault();
        setError('');
        setMessage('');
        setLoading(true);

        try {
            await orderApi.createOrder({ ...form, shippingFee: Number(form.shippingFee) });
            setMessage('Dat hang thanh cong. Dang chuyen sang trang lich su don.');
            setTimeout(() => navigate(ROUTES.ORDERS), 1200);
        } catch (apiError) {
            setError(apiError.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <section className="card auth-card">
            <h1>Thong tin thanh toan</h1>
            <form onSubmit={onSubmit} className="form-grid">
                <label>
                    Ten nguoi nhan
                    <input
                        value={form.receiverName}
                        onChange={(event) => setForm({ ...form, receiverName: event.target.value })}
                        required
                    />
                </label>
                <label>
                    So dien thoai
                    <input
                        value={form.receiverPhone}
                        onChange={(event) => setForm({ ...form, receiverPhone: event.target.value })}
                        required
                    />
                </label>
                <label>
                    Dia chi giao hang
                    <input
                        value={form.shippingAddress}
                        onChange={(event) => setForm({ ...form, shippingAddress: event.target.value })}
                        required
                    />
                </label>
                <label>
                    Phi van chuyen
                    <input
                        type="number"
                        value={form.shippingFee}
                        onChange={(event) => setForm({ ...form, shippingFee: event.target.value })}
                    />
                </label>
                <label>
                    Phuong thuc thanh toan
                    <select
                        value={form.paymentMethod}
                        onChange={(event) => setForm({ ...form, paymentMethod: event.target.value })}
                    >
                        <option value="COD">COD</option>
                        <option value="BANK_TRANSFER">BANK_TRANSFER</option>
                        <option value="WALLET">WALLET</option>
                    </select>
                </label>

                {error && <p className="error-text">{error}</p>}
                {message && <p className="success-text">{message}</p>}

                <button disabled={loading} type="submit">
                    {loading ? 'Dang dat hang...' : 'Xac nhan dat hang'}
                </button>
            </form>
        </section>
    );
};

export default CheckoutPage;

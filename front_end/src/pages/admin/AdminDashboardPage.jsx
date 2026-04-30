import { useEffect, useState } from 'react';
import { adminApi } from '../../api/adminApi';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { formatCurrency } from '../../utils/formatCurrency';
import { formatDateTime } from '../../utils/formatDate';

const AdminDashboardPage = () => {
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [products, setProducts] = useState([]);
    const [orders, setOrders] = useState([]);

    const loadData = async () => {
        setLoading(true);
        setError('');

        try {
            const [productsData, ordersData] = await Promise.all([
                adminApi.getProducts({ page: 0, size: 6 }),
                adminApi.getOrders({ page: 0, size: 6 }),
            ]);

            setProducts(productsData.content || []);
            setOrders(ordersData.content || []);
        } catch (apiError) {
            setError(apiError.message);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadData();
    }, []);

    const removeProduct = async (id) => {
        try {
            await adminApi.deleteProduct(id);
            await loadData();
        } catch (apiError) {
            setError(apiError.message);
        }
    };

    if (loading) {
        return <LoadingSpinner text="Dang tai du lieu admin..." />;
    }

    return (
        <section>
            {error && <p className="error-text card">{error}</p>}

            <div className="admin-grid">
                <article className="card">
                    <h2>San pham moi nhat</h2>
                    <div className="list">
                        {products.map((item) => (
                            <div key={item.id} className="list-item stretch">
                                <div>
                                    <strong>{item.title || item.name || `Sach #${item.id}`}</strong>
                                    <p>{formatCurrency(item.price)}</p>
                                </div>
                                <button className="danger" onClick={() => removeProduct(item.id)}>
                                    Xoa
                                </button>
                            </div>
                        ))}
                    </div>
                </article>

                <article className="card">
                    <h2>Don hang gan day</h2>
                    <div className="list">
                        {orders.map((order) => (
                            <div key={order.id} className="list-item">
                                <strong>Don #{order.id}</strong>
                                <span>{order.status}</span>
                                <span>{formatDateTime(order.orderDate)}</span>
                                <span>{formatCurrency(order.totalAmount)}</span>
                            </div>
                        ))}
                    </div>
                </article>
            </div>
        </section>
    );
};

export default AdminDashboardPage;

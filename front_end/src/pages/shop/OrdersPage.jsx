import { useEffect, useState } from 'react';
import { orderApi } from '../../api/orderApi';
import EmptyState from '../../components/common/EmptyState';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import Pagination from '../../components/common/Pagination';
import { formatCurrency } from '../../utils/formatCurrency';
import { formatDateTime } from '../../utils/formatDate';

const OrdersPage = () => {
    const [ordersPage, setOrdersPage] = useState(null);
    const [page, setPage] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const loadOrders = async () => {
            setLoading(true);
            setError('');
            try {
                const response = await orderApi.getMyOrders({ page, size: 5 });
                setOrdersPage(response);
            } catch (apiError) {
                setError(apiError.message);
            } finally {
                setLoading(false);
            }
        };

        loadOrders();
    }, [page]);

    const cancelOrder = async (id) => {
        try {
            await orderApi.cancelOrder(id);
            const response = await orderApi.getMyOrders({ page, size: 5 });
            setOrdersPage(response);
        } catch (apiError) {
            setError(apiError.message);
        }
    };

    return (
        <section className="card">
            <h1>Lich su don hang</h1>
            {loading && <LoadingSpinner />}
            {error && <p className="error-text">{error}</p>}

            {!loading && !error && ordersPage?.content?.length === 0 && (
                <EmptyState title="Ban chua co don hang" description="Hay mua sach va quay lai day de theo doi van don." />
            )}

            <div className="list">
                {ordersPage?.content?.map((order) => (
                    <article key={order.id} className="list-item stretch">
                        <div>
                            <h3>Don #{order.id}</h3>
                            <p>Ngay dat: {formatDateTime(order.orderDate)}</p>
                            <p>Trang thai: {order.status}</p>
                            <p>Tong tien: {formatCurrency(order.totalAmount)}</p>
                        </div>

                        <button className="danger" onClick={() => cancelOrder(order.id)}>
                            Huy don
                        </button>
                    </article>
                ))}
            </div>

            <Pagination page={page} totalPages={ordersPage?.totalPages || 0} onChange={setPage} />
        </section>
    );
};

export default OrdersPage;

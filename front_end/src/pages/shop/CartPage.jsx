import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { cartApi } from '../../api/cartApi';
import EmptyState from '../../components/common/EmptyState';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { ROUTES } from '../../constants/routes';
import { formatCurrency } from '../../utils/formatCurrency';

const CartPage = () => {
    const [cart, setCart] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const loadCart = async () => {
        setLoading(true);
        setError('');
        try {
            const data = await cartApi.getCart();
            setCart(data);
        } catch (apiError) {
            setError(apiError.message);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadCart();
    }, []);

    const updateQuantity = async (itemId, quantity) => {
        if (quantity < 1) {
            return;
        }

        try {
            const data = await cartApi.updateItem(itemId, { quantity });
            setCart(data);
        } catch (apiError) {
            setError(apiError.message);
        }
    };

    const removeItem = async (itemId) => {
        try {
            const data = await cartApi.removeItem(itemId);
            setCart(data);
        } catch (apiError) {
            setError(apiError.message);
        }
    };

    const clearCart = async () => {
        try {
            await cartApi.clearCart();
            await loadCart();
        } catch (apiError) {
            setError(apiError.message);
        }
    };

    if (loading) {
        return <LoadingSpinner />;
    }

    return (
        <section className="card">
            <h1>Gio hang cua ban</h1>
            {error && <p className="error-text">{error}</p>}

            {!cart?.items?.length ? (
                <EmptyState title="Gio hang trong" description="Hay them sach vao gio hang de tiep tuc mua sam." />
            ) : (
                <>
                    <div className="list">
                        {cart.items.map((item) => (
                            <article key={item.id} className="list-item">
                                <div>
                                    <h3>{item.bookTitle}</h3>
                                    <p>{formatCurrency(item.priceAfterDiscount || item.bookPrice)}</p>
                                </div>

                                <div className="inline-actions">
                                    <button onClick={() => updateQuantity(item.id, item.quantity - 1)}>-</button>
                                    <span>{item.quantity}</span>
                                    <button onClick={() => updateQuantity(item.id, item.quantity + 1)}>+</button>
                                    <button className="danger" onClick={() => removeItem(item.id)}>
                                        Xoa
                                    </button>
                                </div>
                            </article>
                        ))}
                    </div>

                    <div className="checkout-bar">
                        <strong>Tong tien: {formatCurrency(cart.totalAmount)}</strong>
                        <div className="inline-actions">
                            <button className="ghost" onClick={clearCart}>
                                Xoa gio hang
                            </button>
                            <Link className="button-link" to={ROUTES.CHECKOUT}>
                                Tien hanh thanh toan
                            </Link>
                        </div>
                    </div>
                </>
            )}
        </section>
    );
};

export default CartPage;

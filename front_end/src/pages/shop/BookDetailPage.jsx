import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { bookApi } from '../../api/bookApi';
import { cartApi } from '../../api/cartApi';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { ROUTES } from '../../constants/routes';
import { useAuth } from '../../hooks/useAuth';
import { formatCurrency } from '../../utils/formatCurrency';

const BookDetailPage = () => {
    const { id } = useParams();
    const [book, setBook] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [adding, setAdding] = useState(false);
    const auth = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        const loadBook = async () => {
            setLoading(true);
            setError('');
            try {
                const data = await bookApi.getBookDetail(id);
                setBook(data);
            } catch (apiError) {
                setError(apiError.message);
            } finally {
                setLoading(false);
            }
        };

        loadBook();
    }, [id]);

    const addToCart = async () => {
        if (!auth.isAuthenticated) {
            navigate(ROUTES.LOGIN, { state: { from: { pathname: `/sach/${id}` } } });
            return;
        }

        setAdding(true);
        try {
            await cartApi.addItem({ bookId: Number(id), quantity: 1 });
            navigate(ROUTES.CART);
        } catch (apiError) {
            setError(apiError.message);
        } finally {
            setAdding(false);
        }
    };

    if (loading) {
        return <LoadingSpinner />;
    }

    if (error) {
        return <p className="error-text card">{error}</p>;
    }

    if (!book) {
        return <p className="card">Khong tim thay sach.</p>;
    }

    return (
        <section className="book-detail card">
            <div className="book-cover large">
                {book.coverImage ? <img src={book.coverImage} alt={book.title} /> : <span>No image</span>}
            </div>

            <div>
                <h1>{book.title}</h1>
                <p>Tac gia: {book.authorName || '-'}</p>
                <p>The loai: {book.categoryName || '-'}</p>
                <p>{book.description || 'Chua co mo ta cho sach nay.'}</p>
                <h2>{formatCurrency(book.price)}</h2>

                <div className="row-gap">
                    <button onClick={addToCart} disabled={adding}>
                        {adding ? 'Dang them...' : 'Them vao gio'}
                    </button>
                    <Link to={ROUTES.HOME}>Quay lai danh sach</Link>
                </div>
            </div>
        </section>
    );
};

export default BookDetailPage;

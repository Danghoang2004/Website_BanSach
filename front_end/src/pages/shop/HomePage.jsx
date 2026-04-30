import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { bookApi } from '../../api/bookApi';
import EmptyState from '../../components/common/EmptyState';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import Pagination from '../../components/common/Pagination';
import { useDebounce } from '../../hooks/useDebounce';
import { formatCurrency } from '../../utils/formatCurrency';

const HomePage = () => {
    const [booksPage, setBooksPage] = useState(null);
    const [page, setPage] = useState(0);
    const [keyword, setKeyword] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const debouncedKeyword = useDebounce(keyword);

    useEffect(() => {
        const loadBooks = async () => {
            setLoading(true);
            setError('');
            try {
                const data = await bookApi.getBooks({ page, size: 8, keyword: debouncedKeyword });
                setBooksPage(data);
            } catch (apiError) {
                setError(apiError.message);
            } finally {
                setLoading(false);
            }
        };

        loadBooks();
    }, [debouncedKeyword, page]);

    return (
        <section>
            <div className="hero card">
                <h1>Kho sach online cho nguoi yeu tri thuc</h1>
                <p>Kham pha cac dau sach moi, uu dai theo tung tua va dat hang nhanh chong.</p>
            </div>

            <div className="toolbar card">
                <input
                    placeholder="Tim theo ten sach..."
                    value={keyword}
                    onChange={(event) => {
                        setKeyword(event.target.value);
                        setPage(0);
                    }}
                />
            </div>

            {loading && <LoadingSpinner />}
            {error && <p className="error-text card">{error}</p>}

            {!loading && !error && booksPage?.content?.length === 0 && (
                <EmptyState
                    title="Khong co san pham"
                    description="Thu doi tu khoa khac de tim sach phu hop voi ban."
                />
            )}

            <div className="book-grid">
                {booksPage?.content?.map((book) => (
                    <article key={book.id} className="book-card card">
                        <div className="book-cover">
                            {book.coverImage ? <img src={book.coverImage} alt={book.title} /> : <span>No image</span>}
                        </div>
                        <div className="book-info">
                            <h3>{book.title}</h3>
                            <p>{book.authorName || 'Dang cap nhat tac gia'}</p>
                            <strong>{formatCurrency(book.price)}</strong>
                            <Link to={`/sach/${book.id}`}>Xem chi tiet</Link>
                        </div>
                    </article>
                ))}
            </div>

            <Pagination page={page} totalPages={booksPage?.totalPages || 0} onChange={setPage} />
        </section>
    );
};

export default HomePage;

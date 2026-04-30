const Pagination = ({ page, totalPages, onChange }) => {
    if (!totalPages || totalPages <= 1) {
        return null;
    }

    return (
        <div className="pagination">
            <button onClick={() => onChange(page - 1)} disabled={page <= 0}>
                Truoc
            </button>
            <span>
                Trang {page + 1}/{totalPages}
            </span>
            <button onClick={() => onChange(page + 1)} disabled={page >= totalPages - 1}>
                Sau
            </button>
        </div>
    );
};

export default Pagination;

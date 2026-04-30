const LoadingSpinner = ({ text = 'Dang tai du lieu...' }) => {
    return (
        <div className="loading-wrap">
            <div className="loading-spinner" />
            <p>{text}</p>
        </div>
    );
};

export default LoadingSpinner;

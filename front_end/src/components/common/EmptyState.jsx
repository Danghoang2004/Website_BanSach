const EmptyState = ({ title, description }) => {
    return (
        <section className="empty-state card">
            <h3>{title}</h3>
            <p>{description}</p>
        </section>
    );
};

export default EmptyState;

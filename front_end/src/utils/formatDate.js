export const formatDateTime = (isoDate) => {
    if (!isoDate) {
        return '-';
    }

    return new Date(isoDate).toLocaleString('vi-VN');
};

export const ROLES = {
    ADMIN: 'ADMIN',
    USER: 'USER',
};

export const hasRole = (roles = [], expectedRole) => {
    const normalized = roles.map((role) => role.replace('ROLE_', ''));
    return normalized.includes(expectedRole);
};

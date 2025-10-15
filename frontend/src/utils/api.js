import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add auth token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
  getLoyaltyPoints: () => api.get('/auth/loyalty-points'),
};

export const vendorAPI = {
  getAll: () => api.get('/vendors'),
  getById: (id) => api.get(`/vendors/${id}`),
};

export const menuAPI = {
  getByVendor: (vendorId) => api.get(`/menu/vendor/${vendorId}`),
  getById: (id) => api.get(`/menu/${id}`),
};

export const slotAPI = {
  getActive: () => api.get('/slots'),
};

export const orderAPI = {
  create: (orderData) => api.post('/orders', orderData),
  getMyOrders: () => api.get('/orders'),
  getById: (id) => api.get(`/orders/${id}`),
};

export const paymentAPI = {
  createOrder: (orderId) => api.post(`/payments/create-order?orderId=${orderId}`),
  verify: (paymentData) => api.post('/payments/verify', paymentData),
  getByOrderId: (orderId) => api.get(`/payments/order/${orderId}`),
};

export const adminAPI = {
  getAllOrders: () => api.get('/admin/orders'),
  getAllVendors: () => api.get('/admin/vendors'),
  getAllUsers: () => api.get('/admin/users'),
  getStats: () => api.get('/admin/stats'),
  updateOrderStatus: (id, status) => api.put(`/admin/orders/${id}/status?status=${status}`),
};

export default api;

import axios from 'axios';
import type { Product } from '../store/productsSlice';
import { mockApi } from './mock';

const API_BASE_URL = 'http://192.168.1.10:8080/api';
const USE_MOCK = import.meta.env.DEV;

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
});

export const getProducts = async (): Promise<Product[]> => {
  if (USE_MOCK) {
    return mockApi.getProducts();
  }
  const response = await apiClient.get('/products');
  return response.data;
};

export const addToCart = async (): Promise<{ success: boolean }> => {
  if (USE_MOCK) {
    return mockApi.addToCart();
  }
  const response = await apiClient.post('/cart/add');
  return response.data;
};

export const getCart = async () => {
  if (USE_MOCK) {
    return { items: [] };
  }
  const response = await apiClient.get('/cart');
  return response.data;
};

export const checkout = async (): Promise<{ orderId: string; success: boolean }> => {
  if (USE_MOCK) {
    return mockApi.checkout();
  }
  const response = await apiClient.post('/checkout');
  return response.data;
};

export const getStock = async (productId: number) => {
  const response = await apiClient.get(`/stock/${productId}`);
  return response.data;
};

export default apiClient;

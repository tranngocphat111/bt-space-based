import axios from 'axios';
import type { Product } from '../store/productsSlice';
import { mockApi } from './mock';

const API_BASE_URL = 'http://192.168.1.10:8080/api';
const USE_MOCK = import.meta.env.DEV;

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
});

// Get or create session ID
const getSessionId = (): string => {
  let sessionId = sessionStorage.getItem('sessionId');
  if (!sessionId) {
    sessionId = `sess_${Date.now()}_${Math.random().toString(36).substring(7)}`;
    sessionStorage.setItem('sessionId', sessionId);
  }
  return sessionId;
};

export const getProducts = async (): Promise<Product[]> => {
  if (USE_MOCK) {
    return mockApi.getProducts();
  }
  try {
    const response = await apiClient.get('/products');
    return response.data;
  } catch (error) {
    console.error('Error fetching products:', error);
    throw new Error('Không thể tải danh sách sản phẩm');
  }
};

export const getProductDetail = async (productId: number): Promise<Product> => {
  if (USE_MOCK) {
    return mockApi.getProductDetail(productId);
  }
  try {
    const response = await apiClient.get(`/products/${productId}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching product detail:', error);
    throw new Error('Không thể tải thông tin sản phẩm');
  }
};

export const addToCart = async (product: Product, quantity: number = 1): Promise<{ success: boolean }> => {
  if (USE_MOCK) {
    return mockApi.addToCart();
  }
  try {
    const sessionId = getSessionId();
    const response = await apiClient.post('/cart/add', {
      session_id: sessionId,
      product_id: product.id,
      quantity: quantity,
    });
    return response.data;
  } catch (error) {
    console.error('Error adding to cart:', error);
    throw new Error('Không thể thêm sản phẩm vào giỏ');
  }
};

export const getCart = async () => {
  if (USE_MOCK) {
    return { items: [] };
  }
  try {
    const sessionId = getSessionId();
    const response = await apiClient.get('/cart', {
      params: { session_id: sessionId }
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching cart:', error);
    throw new Error('Không thể tải giỏ hàng');
  }
};

export interface CheckoutRequest {
  session_id: string;
  items: Array<{
    product_id: number;
    quantity: number;
    unit_price: number;
  }>;
  total_amount: number;
}

export const checkout = async (cartItems: any[]): Promise<{ orderId: string; success: boolean }> => {
  if (USE_MOCK) {
    return mockApi.checkout();
  }
  try {
    const sessionId = getSessionId();
    
    // Build checkout request
    const checkoutRequest: CheckoutRequest = {
      session_id: sessionId,
      items: cartItems.map(item => ({
        product_id: item.id,
        quantity: item.quantity,
        unit_price: item.price,
      })),
      total_amount: cartItems.reduce((sum: number, item: any) => sum + item.price * item.quantity, 0),
    };

    const response = await apiClient.post('/checkout', checkoutRequest);
    
    // Clear session on success
    if (response.data.success) {
      sessionStorage.removeItem('sessionId');
    }
    
    return response.data;
  } catch (error: any) {
    console.error('Error during checkout:', error);
    throw new Error(error.response?.data?.message || 'Không thể hoàn tất thanh toán');
  }
};

export const getStock = async (productId: number) => {
  if (USE_MOCK) {
    return { stock: 50 };
  }
  try {
    const response = await apiClient.get(`/stock/${productId}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching stock:', error);
    throw new Error('Không thể kiểm tra kho');
  }
};

export default apiClient;

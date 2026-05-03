import axios from 'axios';
import type { Product } from '../store/productsSlice';

const API_BASE_URL = 'http://192.168.1.62:8080/api';

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

  try {
    const response = await apiClient.get('/products');
    return response.data;
  } catch (error) {
    console.error('Error fetching products:', error);
    throw new Error('Không thể tải danh sách sản phẩm');
  }
};

export const getProductDetail = async (productId: number): Promise<Product> => {

  try {
    const response = await apiClient.get(`/products/${productId}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching product detail:', error);
    throw new Error('Không thể tải thông tin sản phẩm');
  }
};

export const addToCart = async (product: Product, quantity: number = 1): Promise<{ success: boolean }> => {

  try {
    const sessionId = getSessionId();
    const response = await apiClient.post('/cart/add', {
      userId: sessionId,
      productId: product.id.toString(),
      productName: product.name,
      price: product.price,
      quantity: quantity,
    });
    return response.data;
  } catch (error) {
    console.error('Error adding to cart:', error);
    throw new Error('Không thể thêm sản phẩm vào giỏ');
  }
};

export const getCart = async () => {
  try {
    const sessionId = getSessionId();
    const response = await apiClient.get('/cart', {
      params: { userId: sessionId }
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching cart:', error);
    throw new Error('Không thể tải giỏ hàng');
  }
};

export const removeFromCart = async (productId: number) => {
  try {
    const sessionId = getSessionId();
    const response = await apiClient.delete('/cart/remove', {
      params: { userId: sessionId, productId: productId.toString() }
    });
    return response.data;
  } catch (error) {
    console.error('Error removing from cart:', error);
    throw new Error('Không thể xóa sản phẩm khỏi giỏ hàng');
  }
};

export const updateCartQuantity = async (productId: number, quantity: number) => {
  try {
    const sessionId = getSessionId();
    const response = await apiClient.put('/cart/update', null, {
      params: { userId: sessionId, productId: productId.toString(), quantity }
    });
    return response.data;
  } catch (error) {
    console.error('Error updating cart quantity:', error);
    throw new Error('Không thể cập nhật số lượng sản phẩm');
  }
};

export const clearCartAPI = async () => {
  try {
    const sessionId = getSessionId();
    await apiClient.delete('/cart/clear', {
      params: { userId: sessionId }
    });
    return { success: true };
  } catch (error) {
    console.error('Error clearing cart:', error);
    throw new Error('Không thể làm trống giỏ hàng');
  }
};

export interface CheckoutRequest {
  userId: string;
  items: Array<{
    product_id: number;
    quantity: number;
    unit_price: number;
  }>;
  total_amount: number;
}

export const checkout = async (cartItems: any[]): Promise<{ orderId: string; success: boolean }> => {

  try {
    const sessionId = getSessionId();
    
    // Build checkout request
    const checkoutRequest: CheckoutRequest = {
      userId: sessionId,
      items: cartItems.map(item => ({
        product_id: item.id,
        quantity: item.quantity,
        unit_price: item.price,
      })),
      total_amount: cartItems.reduce((sum: number, item: any) => sum + item.price * item.quantity, 0),
    };

    const response = await apiClient.post('/checkout', checkoutRequest);
    console.log(response.data);
    // Clear session on success
    if (response.data) {
      sessionStorage.removeItem('sessionId');
    }
    
    return response.data;
  } catch (error: any) {
    console.error('Error during checkout:', error);
    throw new Error(error.response?.data?.message || 'Không thể hoàn tất thanh toán');
  }
};

export const getStock = async (productId: number) => {

  try {
    const response = await apiClient.get(`/stock/${productId}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching stock:', error);
    throw new Error('Không thể kiểm tra kho');
  }
};

export default apiClient;

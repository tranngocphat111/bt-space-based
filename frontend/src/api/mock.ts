import type { Product } from '../store/productsSlice';

const MOCK_PRODUCTS: Product[] = [
  {
    id: 1,
    name: 'iPhone 15 Pro Max 256GB',
    description: 'Chip A17 Pro, Camera 48MP, Titanium',
    price: 28990000,
    image_url: 'https://images.unsplash.com/photo-1592286927505-1def25115558?w=300&h=300&fit=crop',
    category: 'Điện thoại',
    stock: 45,
  },
  {
    id: 2,
    name: 'Samsung Galaxy S24 Ultra',
    description: 'Snapdragon 8 Gen 3, Display 6.9",1440p',
    price: 26990000,
    image_url: 'https://images.unsplash.com/photo-1511707267537-b85faf00021e?w=300&h=300&fit=crop',
    category: 'Điện thoại',
    stock: 32,
  },
  {
    id: 3,
    name: 'MacBook Pro 16" M4',
    description: 'CPU 12-core, GPU 20-core, 32GB RAM',
    price: 69990000,
    image_url: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=300&h=300&fit=crop',
    category: 'Laptop',
    stock: 12,
  },
  {
    id: 4,
    name: 'iPad Pro 12.9" M4',
    description: 'Display Liquid Retina XDR, 512GB',
    price: 34990000,
    image_url: 'https://images.unsplash.com/photo-1561154464-82256b60f171?w=300&h=300&fit=crop',
    category: 'Máy tính bảng',
    stock: 28,
  },
  {
    id: 5,
    name: 'Sony WH-1000XM5',
    description: 'Noise cancelling, 30h battery, ANC',
    price: 8990000,
    image_url: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=300&h=300&fit=crop',
    category: 'Tai nghe',
    stock: 78,
  },
  {
    id: 6,
    name: 'AirPods Pro 2',
    description: 'Active Noise Cancellation, Spatial Audio',
    price: 6490000,
    image_url: 'https://images.unsplash.com/photo-1606841837239-c5a626a37d7f?w=300&h=300&fit=crop',
    category: 'Tai nghe',
    stock: 3,
  },
  {
    id: 7,
    name: 'Apple Watch Series 9',
    description: '41mm, Always-On Retina display, Fitness',
    price: 12990000,
    image_url: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=300&h=300&fit=crop',
    category: 'Đồng hồ',
    stock: 15,
  },
  {
    id: 8,
    name: 'iPad Air 11" M2',
    description: 'Display 11", 256GB, WiFi + 5G',
    price: 19990000,
    image_url: 'https://images.unsplash.com/photo-1561154464-82256b60f171?w=300&h=300&fit=crop',
    category: 'Máy tính bảng',
    stock: 0,
  },
];

export const mockApi = {
  getProducts: async (): Promise<Product[]> => {
    return new Promise(resolve => {
      setTimeout(() => resolve(MOCK_PRODUCTS), 1000);
    });
  },

  getProductDetail: async (productId: number): Promise<Product> => {
    return new Promise((resolve, reject) => {
      const product = MOCK_PRODUCTS.find(p => p.id === productId);
      if (product) {
        setTimeout(() => resolve(product), 500);
      } else {
        setTimeout(() => reject(new Error('Product not found')), 500);
      }
    });
  },

  addToCart: async (): Promise<{ success: boolean }> => {
    return new Promise(resolve => {
      setTimeout(() => resolve({ success: true }), 300);
    });
  },

  checkout: async (): Promise<{ orderId: string; success: boolean }> => {
    return new Promise(resolve => {
      setTimeout(() => resolve({ orderId: 'ORD-' + Date.now(), success: true }), 1500);
    });
  },
};

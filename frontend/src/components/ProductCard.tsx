import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ShoppingBag, Eye } from 'lucide-react';
import { addToCart } from '../api/client';
import type { Product } from '../store/productsSlice';
import { showToast } from '../utils/toast';

interface ProductCardProps {
  product: Product;
}

export function ProductCard({ product }: ProductCardProps) {
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);

  const maxStock = 100;
  const stockPercentage = (product.stock / maxStock) * 100;
  const isSoldOut = product.stock === 0;

  const handleAddToCart = async () => {
    if (isSoldOut) return;

    setIsLoading(true);
    try {
      await addToCart(product, 1);
      showToast.success(`Đã thêm "${product.name}" vào giỏ`);
    } catch (error) {
      showToast.error('Không thể thêm sản phẩm vào giỏ');
    } finally {
      setIsLoading(false);
    }
  };

  const handleViewDetail = () => {
    navigate(`/product/${product.id}`);
  };

  return (
    <div className="bg-white rounded-xl shadow-md hover:shadow-xl transition-shadow overflow-hidden h-full flex flex-col">
      <div className="relative h-48 overflow-hidden bg-gray-200">
        <img
          src={product.imageUrl}
          alt={product.name}
          className="w-full h-full object-cover hover:scale-105 transition-transform duration-300"
        />
      </div>

      <div className="p-4 flex-1 flex flex-col">
        <h3 className="text-lg font-bold text-gray-800 line-clamp-2 mb-2 h-14">
          {product.name}
        </h3>

        <p className="text-red-600 font-bold text-xl mb-3">
          {product.price.toLocaleString('vi-VN')} đ
        </p>

        <div className="mb-4">
          <p className="text-xs text-gray-600 mb-1">
            {isSoldOut ? 'Đã bán hết' : `Còn lại: ${product.stock}`}
          </p>
          <div className="w-full h-2 bg-gray-200 rounded-full overflow-hidden">
            <div
              className={`h-full transition-all duration-300 ${
                stockPercentage > 50
                  ? 'bg-gradient-to-r from-green-400 to-green-500'
                  : stockPercentage > 20
                  ? 'bg-gradient-to-r from-yellow-400 to-orange-400'
                  : 'bg-gradient-to-r from-red-400 to-red-600'
              }`}
              style={{ width: `${Math.min(stockPercentage, 100)}%` }}
            />
          </div>
        </div>

        <div className="flex gap-2 mt-auto">
          <button
            onClick={handleAddToCart}
            disabled={isSoldOut || isLoading}
            className="flex-1 border-2 border-gray-300 text-gray-700 font-semibold py-2 rounded-lg hover:bg-gray-50 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-1"
          >
            <ShoppingBag size={16} />
            Thêm giỏ
          </button>
          <button
            onClick={handleViewDetail}
            className="flex-1 bg-blue-600 text-white font-semibold py-2 rounded-lg hover:bg-blue-700 transition-all flex items-center justify-center gap-1"
          >
            <Eye size={16} />
            Xem chi tiết
          </button>
        </div>
      </div>
    </div>
  );
}

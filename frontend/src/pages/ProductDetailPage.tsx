import { useParams, useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { ChevronLeft, Plus, Minus, ShoppingBag } from 'lucide-react';
import { useAppSelector } from '../hooks';
import { addToCart } from '../api/client';
import { showToast } from '../utils/toast';

export function ProductDetailPage() {
  const { productId } = useParams<{ productId: string }>();
  const navigate = useNavigate();
  const products = useAppSelector(state => state.products.items);
  const [quantity, setQuantity] = useState(1);
  const [isLoading, setIsLoading] = useState(false);
  const product = products.find((p: any) => p.id === Number(productId));

  if (!product) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-gray-500">Sản phẩm không tìm thấy</p>
      </div>
    );
  }

  const maxStock = 100;
  const stockPercentage = (product.stock / maxStock) * 100;
  const isSoldOut = product.stock === 0;

  const handleAddToCart = async () => {
    if (quantity < 1) {
      showToast.error('Số lượng phải ≥ 1');
      return;
    }

    if (isSoldOut) {
      showToast.error('Sản phẩm đã hết hàng');
      return;
    }

    setIsLoading(true);
    try {
      await addToCart(product, quantity);
      showToast.success(`Đã thêm ${quantity} sản phẩm vào giỏ`);
      setQuantity(1);
    } catch (error) {
      showToast.error('Không thể thêm sản phẩm vào giỏ');
    } finally {
      setIsLoading(false);
    }
  };

  const handleBuyNow = async () => {
    if (isSoldOut) {
      showToast.error('Sản phẩm đã hết hàng');
      return;
    }

    if (quantity < 1) {
      showToast.error('Số lượng phải ≥ 1');
      return;
    }

    setIsLoading(true);
    try {
      await addToCart(product, quantity);
      showToast.success('Thêm vào giỏ thành công!');
      setTimeout(() => navigate('/cart'), 500);
    } catch (error) {
      showToast.error('Rất tiếc! Sản phẩm đã hết hàng');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-5xl mx-auto px-4 py-6">
        <button
          onClick={() => navigate('/')}
          className="flex items-center gap-2 text-blue-600 hover:text-blue-700 mb-6 font-semibold"
        >
          <ChevronLeft size={20} />
          Quay lại
        </button>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 bg-white rounded-xl shadow-lg p-6">
          {/* Image */}
          <div className="flex items-center justify-center bg-gray-100 rounded-lg h-96">
            <img
              src={product.imageUrl || 'https://via.placeholder.com/400x400?text=No+Image'}
              alt={product.name}
              className="w-full h-full object-cover rounded-lg"
            />
          </div>

          {/* Details */}
          <div className="flex flex-col gap-4">
            <div>
              <h1 className="text-3xl font-bold text-gray-800 mb-2">{product.name}</h1>
              <p className="text-gray-600 text-lg">{product.description}</p>
            </div>

            <div className="py-4 border-y border-gray-200">
              <p className="text-red-600 font-bold text-4xl mb-3">
                {product.price.toLocaleString('vi-VN')} đ
              </p>
              <p className="text-gray-600">Danh mục: {product.category}</p>
            </div>

            {/* Stock Info */}
            <div className="py-4">
              <p className="text-sm text-gray-600 mb-2">
                {isSoldOut ? 'Đã bán hết' : `Tồn kho: ${product.stock} sản phẩm`}
              </p>
              <div className="w-full h-3 bg-gray-200 rounded-full overflow-hidden">
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

            {/* Quantity Selector */}
            <div className="py-4">
              <label className="block text-sm font-semibold text-gray-700 mb-3">
                Số lượng
              </label>
              <div className="flex items-center gap-3 bg-gray-100 w-fit px-4 py-2 rounded-lg">
                <button
                  onClick={() => setQuantity(Math.max(1, quantity - 1))}
                  disabled={isSoldOut || isLoading}
                  className="p-1 hover:bg-gray-200 rounded disabled:opacity-50 transition-colors"
                >
                  <Minus size={20} />
                </button>
                <span className="font-bold text-lg w-12 text-center">{quantity}</span>
                <button
                  onClick={() => setQuantity(quantity + 1)}
                  disabled={isSoldOut || isLoading}
                  className="p-1 hover:bg-gray-200 rounded disabled:opacity-50 transition-colors"
                >
                  <Plus size={20} />
                </button>
              </div>
            </div>

            {/* Action Buttons */}
            <div className="flex gap-3 mt-auto pt-4">
              <button
                onClick={handleAddToCart}
                disabled={isSoldOut || isLoading}
                className="flex-1 border-2 border-gray-300 text-gray-700 font-bold py-3 rounded-lg hover:bg-gray-50 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
              >
                <ShoppingBag size={20} />
                Thêm giỏ
              </button>
              <button
                onClick={handleBuyNow}
                disabled={isSoldOut || isLoading}
                className={`flex-1 font-bold py-3 rounded-lg text-white transition-all flex items-center justify-center gap-2 ${
                  isSoldOut
                    ? 'bg-gray-400 cursor-not-allowed'
                    : 'bg-red-600 hover:bg-red-700'
                } disabled:opacity-50`}
              >
                {isLoading && <span className="animate-spin">⏳</span>}
                {isSoldOut ? 'ĐÃ BÁN HẾT' : 'MUA NGAY'}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

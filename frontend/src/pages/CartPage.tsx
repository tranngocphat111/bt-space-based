import { useNavigate } from 'react-router-dom';
import { ChevronLeft, Trash2, Plus, Minus } from 'lucide-react';
import { useAppDispatch, useAppSelector } from '../hooks';
import { getCart, removeFromCart, updateCartQuantity, clearCartAPI, checkout } from '../api/client';
import { showToast } from '../utils/toast';
import { useEffect, useState } from 'react';
import { setProducts } from '../store/productsSlice';
import { getProducts } from '../api/client';
export function CartPage() {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
    const products = useAppSelector(state => state.products.items);

  const [cartItems, setCartItems] = useState<[]>([]);
  const [loading, setLoading] = useState(false);
  const [isCheckingOut, setIsCheckingOut] = useState(false);
  

  const total = cartItems.reduce((sum: number, item: any) => sum + item.price * item.quantity, 0);
  const itemCount = cartItems.reduce((sum: number, item: any) => sum + item.quantity, 0);


  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      const data = await getProducts();
      dispatch(setProducts(data));
    } catch (error: any) {
    } finally {
    }
  };

  const loadCart = async () => {
    setLoading(true);
    try {
      const response = await getCart();
      const hydratedItems = response.items.map((apiItem: any) => {
        const p = products.find(p => p.id.toString() === apiItem.productId) || {};
        return {
          ...p,
          id: Number(apiItem.productId),
          name: apiItem.productName || (p as any).name,
          price: apiItem.price || (p as any).price,
          quantity: apiItem.quantity,
          imageUrl: (p as any).image_url || (p as any).imageUrl,
        };
      });
      setCartItems(hydratedItems);
      console.log(hydratedItems);
    } catch (error) {
      showToast.error('Không thể tải giỏ hàng');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCart();
  }, [products]);


  const handleRemove = async (id: number) => {
    try {
      setLoading(true);
      await removeFromCart(id);
      await loadCart();
      showToast.success('Đã xóa sản phẩm khỏi giỏ');
    } catch (e) {
      showToast.error('Không thể xóa sản phẩm khỏi giỏ');
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateQuantity = async (id: number, quantity: number) => {
    if (quantity < 1) {
      handleRemove(id);
      return;
    }
    try {
      setLoading(true);
      await updateCartQuantity(id, quantity);
      await loadCart();
    } catch (e) {
      showToast.error('Không thể cập nhật số lượng');
    } finally {
      setLoading(false);
    }
  };

  const handleCheckout = async () => {
    if (cartItems.length === 0) {
      showToast.error('Giỏ hàng trống');
      return;
    }

    setIsCheckingOut(true);
    setLoading(true);

    try {
      const result = await checkout(cartItems);
      if (result) {
        showToast.success('Đặt hàng thành công! Cảm ơn bạn đã mua sắm.');
        await clearCartAPI();
        setCartItems([]);
        setTimeout(() => navigate('/'), 1500);
      } else {
        showToast.error('Đặt hàng thất bại. Vui lòng thử lại.');
      }
    } catch (error: any) {
      showToast.error(error.message|| 'Không thể hoàn tất thanh toán');
    } finally {
      setIsCheckingOut(false);
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-6xl mx-auto px-4 py-6">
        <button
          onClick={() => navigate('/')}
          className="flex items-center gap-2 text-blue-600 hover:text-blue-700 mb-6 font-semibold"
        >
          <ChevronLeft size={20} />
          Tiếp tục mua sắm
        </button>

        <h1 className="text-3xl font-bold text-gray-800 mb-6">Giỏ hàng ({itemCount} sản phẩm)</h1>

        {cartItems.length === 0 ? (
          <div className="bg-white rounded-xl shadow-lg p-12 text-center">
            <p className="text-gray-500 text-xl mb-6">Giỏ hàng của bạn trống</p>
            <button
              onClick={() => navigate('/')}
              className="bg-red-600 text-white font-bold py-3 px-6 rounded-lg hover:bg-red-700 transition-colors"
            >
              Tiếp tục mua sắm
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Items */}
            <div className="lg:col-span-2 space-y-4">
              {cartItems.map((item: any) => (
                <div key={item.id} className="bg-white rounded-xl shadow-md p-4 hover:shadow-lg transition-shadow">
                  <div className="flex gap-4">
                    {/* Image */}
                    <img
                      src={item.imageUrl}
                      alt={item.name}
                      className="w-24 h-24 object-cover rounded-lg"
                    />

                    {/* Info */}
                    <div className="flex-1">
                      <h3 className="font-bold text-gray-800 text-lg line-clamp-2 mb-2">
                        {item.name}
                      </h3>
                      <p className="text-red-600 font-bold text-lg mb-3">
                        {item.price.toLocaleString('vi-VN')} đ
                      </p>
                      <p className="text-gray-600 text-sm mb-3">
                        Tổng: <span className="font-bold text-gray-800">
                          {(item.price * item.quantity).toLocaleString('vi-VN')} đ
                        </span>
                      </p>

                      {/* Quantity & Delete */}
                      <div className="flex items-center gap-3">
                        <div className="flex items-center gap-2 bg-gray-100 px-3 py-1 rounded-lg">
                          <button
                            onClick={() => handleUpdateQuantity(item.id, item.quantity - 1)}
                            className="p-1 hover:bg-gray-200 rounded transition-colors"
                          >
                            <Minus size={16} />
                          </button>
                          <span className="font-bold w-8 text-center">{item.quantity}</span>
                          <button
                            onClick={() => handleUpdateQuantity(item.id, item.quantity + 1)}
                            className="p-1 hover:bg-gray-200 rounded transition-colors"
                          >
                            <Plus size={16} />
                          </button>
                        </div>
                        <button
                          onClick={() => handleRemove(item.id)}
                          className="ml-auto p-2 hover:bg-red-100 text-red-600 rounded transition-colors"
                        >
                          <Trash2 size={20} />
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>

            {/* Summary */}
            <div className="lg:col-span-1">
              <div className="bg-white rounded-xl shadow-lg p-6 sticky top-20">
                <h2 className="text-xl font-bold text-gray-800 mb-6">Tóm tắt đơn hàng</h2>

                <div className="space-y-3 mb-6 pb-6 border-b border-gray-200">
                  <div className="flex justify-between text-gray-600">
                    <span>Tạm tính ({itemCount} sp):</span>
                    <span className="font-semibold">{total.toLocaleString('vi-VN')} đ</span>
                  </div>
                  <div className="flex justify-between text-gray-600">
                    <span>Phí vận chuyển:</span>
                    <span className="font-semibold text-green-600">Miễn phí</span>
                  </div>
                </div>

                <div className="flex justify-between text-2xl font-bold text-red-600 mb-6">
                  <span>Tổng tiền:</span>
                  <span>{total.toLocaleString('vi-VN')} đ</span>
                </div>

                <button
                  onClick={handleCheckout}
                  disabled={isCheckingOut || loading}
                  className="w-full bg-red-600 text-white font-bold py-4 rounded-lg hover:bg-red-700 transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed flex items-center justify-center gap-2 text-lg"
                >
                  {isCheckingOut && <span className="animate-spin">⏳</span>}
                  TIẾN HÀNH THANH TOÁN
                </button>

                <button
                  onClick={() => navigate('/')}
                  className="w-full mt-3 border-2 border-gray-300 text-gray-700 font-bold py-3 rounded-lg hover:bg-gray-50 transition-colors"
                >
                  Tiếp tục mua sắm
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
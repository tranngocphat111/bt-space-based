import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ShoppingCart, Zap } from 'lucide-react';
import { useAppSelector } from '../hooks';

export function Header() {
  const navigate = useNavigate();
  const cartItems = useAppSelector(state => state.cart.items);
  const [countdown, setCountdown] = useState('00:30:00');

  useEffect(() => {
    const interval = setInterval(() => {
      setCountdown(prev => {
        const [hours, minutes, seconds] = prev.split(':').map(Number);
        let totalSeconds = hours * 3600 + minutes * 60 + seconds - 1;

        if (totalSeconds < 0) {
          totalSeconds = 30 * 3600;
        }

        const h = Math.floor(totalSeconds / 3600);
        const m = Math.floor((totalSeconds % 3600) / 60);
        const s = totalSeconds % 60;

        return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
      });
    }, 1000);

    return () => clearInterval(interval);
  }, []);

  const cartCount = cartItems.reduce((sum: number, item: any) => sum + item.quantity, 0);

  return (
    <header className="sticky top-0 z-40 bg-red-600 text-white shadow-lg">
      <div className="max-w-7xl mx-auto px-4 py-3 flex items-center justify-between">
        <button
          onClick={() => navigate('/')}
          className="flex items-center gap-2 font-bold text-lg hover:opacity-80 transition-opacity"
        >
          <Zap size={24} className="text-yellow-300" />
          <span>⚡ FLASH SALE TECH</span>
        </button>

        <div className="text-sm font-semibold">
          Kết thúc trong {countdown}
        </div>

        <button
          onClick={() => navigate('/cart')}
          className="relative hover:opacity-80 transition-opacity"
        >
          <ShoppingCart size={24} />
          {cartCount > 0 && (
            <span className="absolute -top-2 -right-2 bg-yellow-400 text-red-600 text-xs font-bold rounded-full w-5 h-5 flex items-center justify-center">
              {cartCount}
            </span>
          )}
        </button>
      </div>
    </header>
  );
}

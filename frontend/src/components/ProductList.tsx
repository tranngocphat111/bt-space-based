import { useEffect } from 'react';
import { useAppDispatch, useAppSelector } from '../hooks';
import { setProducts, setLoading, setError } from '../store/productsSlice';
import { getProducts } from '../api/client';
import { ProductCard } from './ProductCard';

export function ProductList() {
  const dispatch = useAppDispatch();
  const products = useAppSelector(state => state.products.items);
  const loading = useAppSelector(state => state.products.loading);

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    dispatch(setLoading(true));
    try {
      const data = await getProducts();
      dispatch(setProducts(data));
      dispatch(setError(null));
    } catch (error: any) {
      dispatch(setError(error.message || 'Không thể tải sản phẩm'));
      dispatch(setProducts([]));
    } finally {
      dispatch(setLoading(false));
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-12">
        <div className="animate-spin text-4xl">⏳</div>
      </div>
    );
  }

  if (!products || products.length === 0) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-500 text-lg">Đang tải sản phẩm...</p>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        {products.map((product: any) => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>
    </div>
  );
}

import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Header } from './components';
import { HomePage, ProductDetailPage, CartPage } from './pages';

function App() {
  return (
    <Router>
      <div className="min-h-screen bg-gray-50">
        <Header />
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/product/:productId" element={<ProductDetailPage />} />
          <Route path="/cart" element={<CartPage />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;

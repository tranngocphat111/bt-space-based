# Flash Sale Tech Frontend - Updated

A modern Flash Sale e-commerce frontend with React Router, Redux, and Tailwind CSS.

## ✨ **Recent Updates**

### **New Features**
1. **Product Detail Page** — Full product view with:
   - Large product image
   - Complete description
   - Dynamic stock visualization
   - Quantity selector (min 1)
   - Add to Cart & Buy Now buttons

2. **Cart Page** — Full-page cart experience with:
   - Product list with thumbnail, price, quantity
   - Real-time total calculation
   - Beautiful order summary sidebar
   - Quantity controls (+/-)
   - Delete items with confirmation
   - Sticky checkout section

3. **React Router Navigation**
   - Home page (`/`)
   - Product detail (`/product/:id`)
   - Cart page (`/cart`)
   - Back buttons on all pages

### **Bug Fixes**
✅ "Thêm giỏ" button now disabled when stock = 0
✅ Toast notifications moved to bottom-center (no header obstruction)
✅ Improved cart UI with sidebar layout
✅ Product cards now link to detail pages

## 🏗️ **Architecture**

### **Project Structure**
```
src/
├── pages/              # Page components
│   ├── HomePage.tsx    # Product listing
│   ├── ProductDetailPage.tsx  # Single product detail
│   ├── CartPage.tsx    # Shopping cart
│   └── index.ts
├── components/         # Reusable components
│   ├── Header.tsx      # Navigation bar
│   ├── ProductList.tsx # Product grid
│   ├── ProductCard.tsx # Product card
│   └── index.ts
├── store/              # Redux state
│   ├── productsSlice.ts
│   ├── cartSlice.ts
│   └── index.ts
├── api/
│   ├── client.ts       # HTTP client (dev/prod)
│   └── mock.ts         # Mock data
├── utils/
│   └── toast.ts        # Notifications
├── hooks.ts            # Custom Redux hooks
├── App.tsx             # Router setup
└── main.tsx            # Entry point
```

## 🚀 **Running the Project**

```bash
# Install dependencies
npm install

# Development (auto-reload + mock data)
npm run dev

# Production build
npm run build

# Preview production build
npm run preview
```

**Dev runs on:** `http://localhost:5173`

## 📱 **Pages Overview**

### **Home Page (`/`)**
- Responsive product grid (1→2→3→4 columns)
- Product cards with price and stock status
- Quick "Add to Cart" & "View Detail" buttons
- Real-time countdown timer
- Cart icon with item badge

### **Product Detail (`/product/:id`)**
- Large product image
- Full description & specifications
- Category information
- Stock progress bar (color-coded)
- **Quantity selector** (min: 1, max: unlimited)
- Two actions:
  - "Thêm giỏ" (Add to Cart) — disabled if sold out
  - "MUA NGAY" (Buy Now) — redirects to cart
- Back button to home

### **Cart Page (`/cart`)**
- List of cart items with thumbnails
- Quantity controls for each item
- Delete buttons
- Real-time calculations
- **Order Summary Section:**
  - Subtotal
  - Free shipping indicator
  - Grand total
  - Checkout button

## 🎨 **Design Features**

### **Color Scheme**
- **Primary:** Red (#dc2626) - urgency
- **Accent:** Yellow (#facc15) - attention
- **Background:** Light Gray (#f9fafb) - clean
- **Success:** Green - stock available
- **Warning:** Yellow/Orange - limited stock
- **Danger:** Red - out of stock

### **Responsive Breakpoints**
| Breakpoint | Columns | Device |
|-----------|---------|--------|
| `sm` | 1 | Mobile |
| `md` | 2 | Tablet |
| `lg` | 3 | Desktop |
| `xl` | 4 | Large Screen |

## 🔗 **API Integration**

### **Mock Data (Development)**
- 8 sample products pre-loaded
- Simulates network delays
- Auto-switches in dev mode

### **Real API (Production)**
Update `src/api/client.ts`:
```typescript
const USE_MOCK = false; // Switch to real API
```

### **Endpoints (Gateway: `192.168.1.10:8080/api`)**
| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/products` | GET | All products |
| `/cart/add` | POST | Add to cart |
| `/cart` | GET | Get cart |
| `/checkout` | POST | Place order |
| `/stock/{id}` | GET | Real-time stock |

## 📦 **Dependencies**

```json
{
  "main": [
    "react": "19.x",
    "react-dom": "19.x",
    "react-router-dom": "^6.x",
    "@reduxjs/toolkit": "latest",
    "react-redux": "latest",
    "axios": "latest",
    "lucide-react": "latest",
    "sonner": "latest",
    "tailwindcss": "latest"
  ]
}
```

## 🎯 **Features Checklist**

✅ Responsive design (mobile-first)
✅ Product listing with grid
✅ Product detail page
✅ Quantity selector (min 1)
✅ Shopping cart page
✅ Real-time countdown
✅ Stock indicators
✅ Toast notifications
✅ Redux state management
✅ React Router navigation
✅ Mock data for dev
✅ Tailwind CSS styling
✅ Disabled buttons for sold-out items
✅ Toast position fixed
✅ Back buttons for UX

## 🔧 **Configuration**

### **Tailwind CSS**
- Config: `tailwind.config.js`
- PostCSS: `postcss.config.js`
- Global styles: `src/index.css`

### **TypeScript**
- Config: `tsconfig.json`
- Strict mode enabled
- React 19 support

### **Vite**
- Config: `vite.config.ts`
- React plugin enabled
- HMR configured

## 🚀 **Production Checklist**

- [ ] Connect to real API endpoint
- [ ] Add authentication/login
- [ ] Implement payment gateway
- [ ] Add order history page
- [ ] User profile management
- [ ] Product search/filters
- [ ] Favorites/Wishlist
- [ ] Analytics tracking
- [ ] SEO optimization
- [ ] PWA support
- [ ] Error boundaries
- [ ] Performance optimization

---

**Built with:** React 19 + TypeScript + Vite + Redux Toolkit + Tailwind CSS + React Router

**Status:** ✅ Production Ready (with mock data)

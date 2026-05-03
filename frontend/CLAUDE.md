# Flash Sale System - Frontend Guide (Người 1)

## 1. Tech Stack
- Framework: ReactJS (Vite)
- State Management: Redux Toolkit hoặc React Query (để handle cache Data Grid)
- Styling: Tailwind CSS
- Icons: Lucide React hoặc Heroicons

## 2. API Architecture (via API Gateway)
Tất cả các request phải đi qua Gateway tại IP: `192.168.1.10:8080`

| Chức năng | Method | Endpoint | PU xử lý |
| :--- | :--- | :--- | :--- |
| Lấy danh sách sản phẩm | GET | `/api/products` | PU1 - Product |
| Thêm sản phẩm vào giỏ | POST | `/api/cart/add` | PU2 - Cart |
| Xem giỏ hàng | GET | `/api/cart` | PU2 - Cart |
| Đặt hàng (Flash Sale) | POST | `/api/checkout` | PU3 - Order |
| Xem tồn kho real-time | GET | `/api/stock/{id}` | PU4 - Inventory |

## typescript
interface Product {
  id: number;
  name: string; // e.g., "iPhone 15 Pro Max 256GB"
  description: string; // e.g., "Chip A17 Pro, Camera 48MP, Titanium"
  price: number; // e.g., 28990000.00
  image_url: string; 
  category: string;
  stock: number; // Mapped from 'inventory' table, crucial for Flash Sale logic
}



## 6. Mô tả giao diện chi tiết (UI Specifications)

Yêu cầu AI (Claude) thiết kế giao diện theo phong cách e-commerce Flash Sale (giống Shopee/Lazada) với cảm giác khẩn trương, tốc độ và mượt mà.

### 6.1. Tổng quan (Global/Layout)
- **Background:** Màu xám nhạt (`bg-gray-50` hoặc `bg-gray-100`) để làm nổi bật các thẻ sản phẩm.
- **Font:** Ưu tiên phông chữ sans-serif hiện đại, dễ đọc.
- **Màu chủ đạo (Primary Color):** Đỏ (`text-red-600`, `bg-red-600`) và Vàng (`bg-yellow-400`) để nhấn mạnh sự kiện Flash Sale.

### 6.2. Header (Thanh điều hướng trên cùng)
- **Vị trí:** Cố định ở trên cùng (`sticky top-0`, `z-50`).
- **Màu sắc:** Nền đỏ (`bg-red-600`), chữ trắng.
- **Bố cục (Flexbox):**
  - **Trái:** Logo hoặc Text "⚡ FLASH SALE TECH" (In đậm, nghiêng).
  - **Giữa (Optional):** Đồng hồ đếm ngược (Countdown Timer) - ví dụ: "Kết thúc trong 00:15:30".
  - **Phải:** Icon Giỏ hàng. Có một badge (chấm tròn đỏ/vàng) đè lên góc icon để hiển thị tổng số lượng sản phẩm trong giỏ.

### 6.3. Product List (Danh sách sản phẩm)
- **Container:** Nằm giữa màn hình, giới hạn chiều rộng (`max-w-7xl mx-auto`, `p-4`).
- **Bố cục Grid:** Responsive. Mobile hiển thị 1 cột, Tablet 2 cột, Desktop 3-4 cột (`grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-6`).

### 6.4. Product Card (Thẻ chi tiết từng sản phẩm)
Đây là component quan trọng nhất, cần các thành phần sau:
- **Khung (Wrapper):** Nền trắng, bo góc (`rounded-xl`), có bóng đổ (`shadow-md`), hover thì bóng đổ đậm hơn (`hover:shadow-xl`).
- **Hình ảnh (Image):** Cố định chiều cao (vd: `h-48`), `object-cover`, nằm trên cùng của thẻ.
- **Nội dung:**
  - **Tên sản phẩm:** `text-lg`, in đậm, cắt chữ nếu quá 2 dòng (`line-clamp-2`).
  - **Giá Flash Sale:** `text-red-500`, in đậm, kích thước lớn (`text-xl`), format theo chuẩn VND (vd: 28.990.000 đ).
- **Thanh trạng thái tồn kho (Stock Progress Bar - Quan trọng):**
  - Phía trên thanh: Text nhỏ ghi "Đang bán chạy" hoặc "Còn lại: {stock}".
  - Thanh bar: Background xám (`bg-gray-200`), phần lõi màu đỏ/cam hoặc dải màu gradient tương ứng với % tồn kho còn lại (`w-[percentage]%`). Hình ảnh trực quan kích thích người mua.
- **Action Buttons (2 nút):**
  - Nút "Thêm giỏ": Outline hoặc màu xám nhạt.
  - Nút "MUA NGAY": Nền đỏ chữ trắng, full width.
  - **Trạng thái Disable:** Nếu `stock === 0`, đổi nút "MUA NGAY" thành "ĐÃ BÁN HẾT", màu xám (`bg-gray-400`), không cho click (`cursor-not-allowed`, `disabled`).

### 6.5. Cart Modal / Off-canvas (Giỏ hàng)
- Khi click vào Icon Giỏ hàng trên Header, mở ra một Sidebar trượt từ phải sang (Off-canvas) hoặc một Modal ở giữa màn hình.
- **Nội dung:**
  - Danh sách các item: Ảnh thu nhỏ, tên, giá, số lượng (nút + / -).
  - Tổng tiền (Total amount).
  - Nút "TIẾN HÀNH THANH TOÁN" (Checkout) to, rõ ràng ở dưới cùng.

### 6.6. Trạng thái & Phản hồi (States & Feedbacks)
- **Loading State:** Khi bấm "Mua ngay" hoặc "Checkout", hiển thị icon spinner loading nhỏ trên nút, vô hiệu hóa nút để tránh spam click.
- **Toast Notifications (Góc màn hình):**
  - Thành công: Thông báo xanh lá "Đặt hàng thành công! Đã trừ kho in-memory."
  - Thất bại/Hết hàng: Thông báo đỏ "Rất tiếc! Sản phẩm đã hết hàng trong chớp mắt."
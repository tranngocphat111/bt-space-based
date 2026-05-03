# Flash Sale System - Frontend & Background Workers Guide

> 🛑 **STRICT DIRECTIVE FOR AI:**
> Phạm vi công việc của tôi CHỈ BAO GỒM:
> 1. **Giao diện Frontend (ReactJS)**.
> 2. **Các Background Workers xử lý RabbitMQ (Java/Spring Boot)**.
> 
> Lệnh cấm: TUYỆT ĐỐI KHÔNG generate, không sửa code của các API Gateway, Processing Units (PU1, PU2, PU3, PU4). Đó là công việc của các thành viên khác trong team.

---

## 1. Kiến trúc Hệ thống (Space-Based Architecture + RabbitMQ)
Hệ thống tuân thủ nghiêm ngặt mô hình Buổi 7 kết hợp Async Persistence từ sơ đồ kiến trúc:
- **Nguyên lý:** Hạn chế DB, dữ liệu nằm trong Data Grid (Redis/Hazelcast)[cite: 2].
- **Cổng vào:** API Gateway (192.168.1.10:8080).
- **Processing Units (PUs):** Xử lý nghiệp vụ trực tiếp trên RAM, không gọi Database[cite: 2].
- **Background Workers (Nhiệm vụ của tôi):** 2 service `read` và `write` kết nối với RabbitMQ để đồng bộ dữ liệu giữa Data Grid và MariaDB.

---

## 2. Chi tiết Database (MariaDB `flash_sale`)
Hệ thống sử dụng các bảng sau (Bắt buộc dùng đúng field name khi code RabbitMQ Worker):
- **`products`**: `id`, `name`, `description`, `price`, `image_url`, `category`, `created_at`.
- **`inventory`**: `id`, `product_id`, `stock`, `updated_at`.
- **`orders`**: `id`, `session_id`, `status` (pending/confirmed/cancelled), `total_amount`, `created_at`, `updated_at`.
- **`order_items`**: `id`, `order_id`, `product_id`, `quantity`, `unit_price`.

---

## 3. PHẦN I: YÊU CẦU CHO BACKEND (RABBITMQ WORKERS)
Tôi chịu trách nhiệm viết 2 service màu xanh lá (`read` và `write`) theo đúng sơ đồ luồng. AI hãy dùng Spring Boot, Spring AMQP, Spring Data JPA.

### 3.1. Service: `write` worker (Data Writer)
- **Message Broker:** Lắng nghe liên tục trên queue **`mq`**.
- **Nguồn phát (Producer):** PU3 (Order PU) sẽ ném event vào `mq` ngay khi user checkout thành công trên Data Grid[cite: 2].
- **Nhiệm vụ Xử lý:**
  - Nhận payload JSON từ `mq` (gồm: session_id, product_id, quantity, total_amount).
  - Mở transaction DB: `INSERT` vào bảng `orders`, `INSERT` vào bảng `order_items`.
  - `UPDATE` giảm `stock` tương ứng trong bảng `inventory`.
  - Ghi chú: Việc này giúp bảo vệ DB khỏi 1000+ RPS, mọi thao tác ghi sẽ được worker này xử lý tuần tự/batch.

### 3.2. Service: `read` worker (Data Pump / DataLoader)
- **Nhiệm vụ 1 (Bootstrapping):** Khi khởi động service, quét bảng `products` (join `inventory`) và nạp thẳng toàn bộ danh sách sản phẩm cùng số lượng tồn kho lên **Redis** (cho PU1, PU2 đọc) và **Hazelcast**.
- **Nhiệm vụ 2 (Lắng nghe):** Subscribe queue **`mq-read`**.
- **Xử lý:** Khi PU2 (truy vấn) không tìm thấy data trong Redis (Cache Miss), nó sẽ bắn event vào `mq-read`. Worker này sẽ đọc DB và đẩy ngược lại vào Redis để phục hồi cache.

---

## 4. PHẦN II: YÊU CẦU CHO FRONTEND (REACTJS)
Tôi dùng ReactJS (Vite), Tailwind CSS, Axios. Mọi request gọi qua API Gateway.

### 4.1. Danh sách API (Dựa theo Buổi 7)
- `GET /api/products`: Lấy danh sách sản phẩm từ PU1 (Load từ Data Grid)[cite: 2].
- `GET /api/products/{id}`: Xem chi tiết sản phẩm từ PU1[cite: 2].
- `POST /api/cart/add`: Thêm vào giỏ từ PU2 (Lưu session/cart)[cite: 2].
- `GET /api/cart`: Lấy thông tin giỏ từ PU2[cite: 2].
- `POST /api/checkout`: Đặt hàng từ PU3 (Trừ Data Grid ngay lập tức, trả kết quả ko chờ DB)[cite: 2].
- `GET /api/stock/{productId}`: Kiểm tra kho thực tế từ PU4 (Giảm trực tiếp trên Data Grid)[cite: 2].

### 4.2. Cấu trúc Interface Data
```typescript
interface Product {
  id: number;
  name: string; // VD: "iPhone 15 Pro Max 256GB"
  description: string;
  price: number; // VD: 28990000.00
  image_url: string; 
  category: string;
  stock: number; // Yếu tố quan trọng nhất của Flash Sale
}
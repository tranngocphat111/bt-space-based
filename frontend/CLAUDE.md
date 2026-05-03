# Flash Sale System - Team Collaboration Guide (Space-Based Architecture)

## 1. Tổng quan Kiến trúc (Space-Based Architecture)
Hệ thống được thiết kế để chịu tải cực cao (1000+ request/s) trong sự kiện Flash Sale. 
Để không bị nghẽn ở Database, hệ thống áp dụng cơ chế xử lý In-Memory kết hợp Async Persistence (Ghi bất đồng bộ) thông qua RabbitMQ theo đúng sơ đồ kiến trúc:
- **Data Grid:** Sử dụng Redis và Hazelcast làm bộ nhớ chia sẻ.
- **Messaging (RabbitMQ):** Đóng vai trò làm bộ đệm (buffer) để ghi dữ liệu từ RAM xuống Database, và đồng bộ dữ liệu ngược lại.
- **Processing Units (PUs):** Xử lý logic nghiệp vụ hoàn toàn trên RAM.

> 🛑 **STRICT DIRECTIVE FOR AI:**
> Phạm vi công việc của tôi CHỈ BAO GỒM:
> 1. **Giao diện Frontend (ReactJS)**.
> 2. **Các Background Workers xử lý RabbitMQ (Java/Spring Boot)**.
> 
> Lệnh cấm: TUYỆT ĐỐI KHÔNG generate, không sửa code của các API Gateway, Processing Units (PU1, PU2, PU3, PU4). Đó là công việc của các thành viên khác trong team.

## 2. Tech Stack Toàn Hệ Thống
- **Frontend (Người 1):** ReactJS (Vite), Tailwind CSS, Axios.
- **API Gateway:** Spring Cloud Gateway.
- **Backend (PUs & Workers):** Spring Boot.
- **Data Grids:** Redis & Hazelcast.
- **Message Broker:** RabbitMQ.
- **Database:** MariaDB.

## 3. Cổng điều hướng (API Gateway)
Tất cả request từ ReactJS (Frontend) phải đi qua API Gateway tại IP dự kiến: `http://192.168.1.10:8080`.

| Chức năng | Method | Endpoint | Service xử lý |
| :--- | :--- | :--- | :--- |
| Lấy danh sách sản phẩm | GET | `/api/products` | PU2 (Product/Query) |
| Đặt hàng (Flash Sale) | POST | `/api/checkout` | PU1 (Order/Command) |

---

## 4. Đặc tả luồng xử lý Backend (Dựa trên Sơ đồ)

Sơ đồ kiến trúc chia làm các khối sau, team Backend cần tuân thủ tuyệt đối:

### A. Khối Xử lý Giao dịch (PU1 & Hazelcast)
- **Nhiệm vụ:** Xử lý các thao tác thay đổi dữ liệu (Command) như Checkout, Giảm tồn kho.
- **Luồng hoạt động:**
  1. Gateway nhận request `/api/checkout` và đẩy cho **PU1**.
  2. PU1 trừ tồn kho trực tiếp trên Memory Grid (**Hazelcast**).
  3. Nếu thành công, PU1 trả về `HTTP 200` ngay lập tức cho Frontend.
  4. Đồng thời, PU1 publish một event (ví dụ: `order_created`) vào RabbitMQ queue có tên là **`mq`**. Tuyệt đối PU1 không gọi trực tiếp vào MariaDB.

### B. Khối Truy vấn Dữ liệu (PU2 & Redis)
- **Nhiệm vụ:** Xử lý các thao tác đọc dữ liệu (Query) như lấy danh sách sản phẩm, xem giỏ hàng.
- **Luồng hoạt động:**
  1. Gateway nhận request `/api/products` và đẩy cho **PU2**.
  2. PU2 chỉ được phép đọc dữ liệu từ **Redis** và trả về cho Frontend.
  3. Nếu Redis bị thiếu dữ liệu (Cache Miss), PU2 sẽ đẩy một message yêu cầu đồng bộ vào RabbitMQ queue có tên là **`mq-read`**.

### C. Khối Background Workers (Dịch vụ Xanh lá trên sơ đồ)
### 1. Service: `write` worker (Data Writer)
- **Message Broker:** Lắng nghe liên tục trên queue **`mq`**.
- **Nguồn phát (Producer):** PU3 (Order PU) sẽ ném event vào `mq` ngay khi user checkout thành công trên Data Grid[cite: 2].
- **Nhiệm vụ Xử lý:**
  - Nhận payload JSON từ `mq` (gồm: session_id, product_id, quantity, total_amount).
  - Mở transaction DB: `INSERT` vào bảng `orders`, `INSERT` vào bảng `order_items`.
  - `UPDATE` giảm `stock` tương ứng trong bảng `inventory`.
  - Ghi chú: Việc này giúp bảo vệ DB khỏi 1000+ RPS, mọi thao tác ghi sẽ được worker này xử lý tuần tự/batch.

### 2. Service: `read` worker (Data Pump / DataLoader)
- **Nhiệm vụ 1 (Bootstrapping):** Khi khởi động service, quét bảng `products` (join `inventory`) và nạp thẳng toàn bộ danh sách sản phẩm cùng số lượng tồn kho lên **Redis** (cho PU1, PU2 đọc) và **Hazelcast**.
- **Nhiệm vụ 2 (Lắng nghe):** Subscribe queue **`mq-read`**.
- **Xử lý:** Khi PU2 (truy vấn) không tìm thấy data trong Redis (Cache Miss), nó sẽ bắn event vào `mq-read`. Worker này sẽ đọc DB và đẩy ngược lại vào Redis để phục hồi cache.


## 5. Cấu trúc Dữ Liệu (Interface Dành Cho Frontend)

```typescript
interface Product {
  id: number;
  name: string; // e.g., "iPhone 15 Pro Max 256GB"
  price: number; // e.g., 28990000.00
  image_url: string; 
  category: string;
  stock: number; // Được PU2 lấy từ Redis
}
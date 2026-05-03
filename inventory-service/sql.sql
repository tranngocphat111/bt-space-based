-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               11.6.2-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             12.8.0.6908
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for flash_sale
CREATE DATABASE IF NOT EXISTS `flash_sale` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;
USE `flash_sale`;

-- Dumping structure for table flash_sale.inventory
CREATE TABLE IF NOT EXISTS `inventory` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `product_id` int(10) unsigned NOT NULL,
  `stock` int(10) unsigned NOT NULL DEFAULT 0,
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `fk_inv_product` (`product_id`),
  CONSTRAINT `fk_inv_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table flash_sale.inventory: ~10 rows (approximately)
INSERT INTO `inventory` (`id`, `product_id`, `stock`, `updated_at`) VALUES
	(1, 1, 50, '2026-05-03 15:39:24'),
	(2, 2, 80, '2026-05-03 15:39:24'),
	(3, 3, 40, '2026-05-03 15:39:24'),
	(4, 4, 30, '2026-05-03 15:39:24'),
	(5, 5, 200, '2026-05-03 15:39:24'),
	(6, 6, 150, '2026-05-03 15:39:24'),
	(7, 7, 60, '2026-05-03 15:39:24'),
	(8, 8, 100, '2026-05-03 15:39:24'),
	(9, 9, 45, '2026-05-03 15:39:24'),
	(10, 10, 300, '2026-05-03 15:39:24');

-- Dumping structure for table flash_sale.orders
CREATE TABLE IF NOT EXISTS `orders` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `session_id` varchar(128) NOT NULL,
  `status` enum('pending','confirmed','cancelled') NOT NULL DEFAULT 'pending',
  `total_amount` decimal(14,2) NOT NULL DEFAULT 0.00,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `idx_session` (`session_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table flash_sale.orders: ~3 rows (approximately)
INSERT INTO `orders` (`id`, `session_id`, `status`, `total_amount`, `created_at`, `updated_at`) VALUES
	(1, 'sess_demo_001', 'confirmed', 28990000.00, '2026-05-03 15:39:24', '2026-05-03 15:39:24'),
	(2, 'sess_demo_002', 'confirmed', 33480000.00, '2026-05-03 15:39:24', '2026-05-03 15:39:24'),
	(3, 'sess_demo_003', 'pending', 25990000.00, '2026-05-03 15:39:24', '2026-05-03 15:39:24');

-- Dumping structure for table flash_sale.order_items
CREATE TABLE IF NOT EXISTS `order_items` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `order_id` int(10) unsigned NOT NULL,
  `product_id` int(10) unsigned NOT NULL,
  `quantity` smallint(6) NOT NULL DEFAULT 1,
  `unit_price` decimal(12,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_oi_order` (`order_id`),
  KEY `fk_oi_product` (`product_id`),
  CONSTRAINT `fk_oi_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `fk_oi_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table flash_sale.order_items: ~4 rows (approximately)
INSERT INTO `order_items` (`id`, `order_id`, `product_id`, `quantity`, `unit_price`) VALUES
	(1, 1, 1, 1, 28990000.00),
	(2, 2, 3, 1, 29990000.00),
	(3, 2, 10, 2, 2490000.00),
	(4, 3, 2, 1, 25990000.00);

-- Dumping structure for table flash_sale.products
CREATE TABLE IF NOT EXISTS `products` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `price` decimal(12,2) NOT NULL,
  `image_url` varchar(512) DEFAULT NULL,
  `category` varchar(100) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table flash_sale.products: ~10 rows (approximately)
INSERT INTO `products` (`id`, `name`, `description`, `price`, `image_url`, `category`, `created_at`) VALUES
	(1, 'iPhone 15 Pro Max 256GB', 'Chip A17 Pro, Camera 48MP, Titanium', 28990000.00, 'https://cdn.example.com/iphone15.jpg', 'Điện thoại', '2026-05-03 15:39:24'),
	(2, 'Samsung Galaxy S24 Ultra', 'Snapdragon 8 Gen 3, S-Pen, 200MP', 25990000.00, 'https://cdn.example.com/s24ultra.jpg', 'Điện thoại', '2026-05-03 15:39:24'),
	(3, 'MacBook Air M3 13"', 'Apple M3, 8GB RAM, 256GB SSD, 18h battery', 29990000.00, 'https://cdn.example.com/mba-m3.jpg', 'Laptop', '2026-05-03 15:39:24'),
	(4, 'ASUS ROG Zephyrus G14', 'Ryzen 9 7940HS, RTX 4060, 165Hz OLED', 32990000.00, 'https://cdn.example.com/rog-g14.jpg', 'Laptop', '2026-05-03 15:39:24'),
	(5, 'Sony WH-1000XM5', 'Chống ồn ANC, 30h pin, Hi-Res Audio', 7490000.00, 'https://cdn.example.com/xm5.jpg', 'Tai nghe', '2026-05-03 15:39:24'),
	(6, 'Apple AirPods Pro 2', 'ANC thế hệ 2, Lossless Audio, MagSafe', 6290000.00, 'https://cdn.example.com/airpods.jpg', 'Tai nghe', '2026-05-03 15:39:24'),
	(7, 'iPad Pro M4 11"', 'Chip M4, OLED 120Hz, Wi-Fi 6E', 23990000.00, 'https://cdn.example.com/ipad-pro.jpg', 'Máy tính bảng', '2026-05-03 15:39:24'),
	(8, 'Xiaomi 14 Ultra', 'Leica Camera, Snapdragon 8 Gen 3, 90W', 20990000.00, 'https://cdn.example.com/mi14ultra.jpg', 'Điện thoại', '2026-05-03 15:39:24'),
	(9, 'LG UltraGear 27" 4K', '27" IPS 4K, 144Hz, G-Sync, HDR600', 12490000.00, 'https://cdn.example.com/lg27.jpg', 'Màn hình', '2026-05-03 15:39:24'),
	(10, 'Logitech MX Master 3S', 'Silent click, 8K DPI, USB-C, Bolt receiver', 2490000.00, 'https://cdn.example.com/mxmaster.jpg', 'Phụ kiện', '2026-05-03 15:39:24');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Sep 18, 2026 at 05:24 PM
-- Server version: 8.4.7
-- PHP Version: 8.3.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `fashionflairboutiquedb`
--

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
CREATE TABLE IF NOT EXISTS `categories` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `category_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `status` enum('Active','Inactive') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Active',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `category_name` (`category_name`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`category_id`, `category_name`, `description`, `status`, `created_at`, `updated_at`) VALUES
(1, 'Casual Wear', 'Everyday clothing like T-shirts, jeans, and casual dresses', 'Active', '2026-09-16 10:13:48', '2026-09-16 10:13:48'),
(2, 'Formal Wear', 'Suits, formal dresses, and dress shirts', 'Active', '2026-09-16 10:13:48', '2026-09-16 10:13:48'),
(3, 'Sportswear', 'Athletic clothing and activewear', 'Active', '2026-09-16 10:13:48', '2026-09-16 10:13:48'),
(4, 'Footwear', 'Shoes, boots, and sandals', 'Active', '2026-09-16 10:13:48', '2026-09-16 10:13:48'),
(5, 'Accessories', 'Handbags, jewelry, belts, and scarves', 'Active', '2026-09-16 10:13:48', '2026-09-16 10:13:48'),
(6, 'Perfumes', 'All kinds of perfumes', 'Active', '2026-09-18 12:46:39', '2026-09-18 12:47:54');

-- --------------------------------------------------------

--
-- Table structure for table `inventory_logs`
--

DROP TABLE IF EXISTS `inventory_logs`;
CREATE TABLE IF NOT EXISTS `inventory_logs` (
  `log_id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `change_type` enum('SALE','RESTOCK','ADJUSTMENT','RETURN') COLLATE utf8mb4_unicode_ci NOT NULL,
  `quantity_changed` int NOT NULL,
  `previous_stock` int NOT NULL,
  `new_stock` int NOT NULL,
  `logged_by` int NOT NULL,
  `log_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`log_id`),
  KEY `product_id` (`product_id`),
  KEY `logged_by` (`logged_by`)
) ENGINE=MyISAM AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `inventory_logs`
--

INSERT INTO `inventory_logs` (`log_id`, `product_id`, `change_type`, `quantity_changed`, `previous_stock`, `new_stock`, `logged_by`, `log_date`) VALUES
(1, 1, 'SALE', -1, 25, 24, 1, '2026-09-17 08:17:08'),
(2, 2, 'SALE', -1, 10, 9, 1, '2026-09-17 08:17:08'),
(3, 2, 'SALE', -1, 9, 8, 1, '2026-09-17 08:35:41'),
(4, 3, 'SALE', -1, 15, 14, 1, '2026-09-17 08:35:41'),
(5, 1, 'SALE', -1, 24, 23, 1, '2026-09-17 09:29:01'),
(6, 4, 'SALE', -1, 8, 7, 1, '2026-09-17 09:29:01'),
(7, 1, 'SALE', -1, 23, 22, 1, '2026-09-17 09:31:36'),
(8, 2, 'SALE', -1, 8, 7, 1, '2026-09-17 09:31:36'),
(9, 1, 'SALE', -1, 50, 49, 1, '2026-09-17 09:34:09'),
(10, 1, 'SALE', -1, 49, 48, 1, '2026-09-17 09:34:51'),
(11, 1, 'SALE', -1, 48, 47, 1, '2026-09-17 09:39:25'),
(12, 6, 'SALE', -1, 30, 29, 1, '2026-09-18 08:00:15'),
(13, 6, 'SALE', -1, 29, 28, 1, '2026-09-18 08:19:30'),
(14, 5, 'RESTOCK', 30, 20, 50, 1, '2026-09-18 14:45:15'),
(15, 1, 'RESTOCK', 50, 47, 97, 1, '2026-09-18 14:45:29'),
(16, 1, 'RESTOCK', 3, 97, 100, 1, '2026-09-18 14:47:15'),
(17, 6, 'ADJUSTMENT', -20, 100, 80, 1, '2026-09-18 14:59:07');

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
CREATE TABLE IF NOT EXISTS `payments` (
  `payment_id` int NOT NULL AUTO_INCREMENT,
  `sale_id` int NOT NULL,
  `payment_method` enum('Cash','Credit Card','Debit Card') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Cash',
  `amount_paid` decimal(10,2) NOT NULL,
  `transaction_reference` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `payment_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`payment_id`),
  KEY `sale_id` (`sale_id`)
) ENGINE=MyISAM AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `payments`
--

INSERT INTO `payments` (`payment_id`, `sale_id`, `payment_method`, `amount_paid`, `transaction_reference`, `payment_date`) VALUES
(1, 1, 'Cash', 18506.95, NULL, '2026-09-17 08:17:08'),
(2, 2, 'Cash', 24206.95, NULL, '2026-09-17 08:35:41'),
(3, 3, 'Cash', 18980.00, NULL, '2026-09-17 09:29:01'),
(4, 4, 'Cash', 19091.38, NULL, '2026-09-17 09:31:36'),
(5, 5, 'Cash', 5990.00, NULL, '2026-09-17 09:34:09'),
(6, 6, 'Cash', 5990.00, NULL, '2026-09-17 09:34:51'),
(7, 7, 'Cash', 5990.00, NULL, '2026-09-17 09:39:25'),
(8, 8, 'Cash', 1492.50, NULL, '2026-09-18 08:00:15'),
(9, 9, 'Cash', 1492.50, NULL, '2026-09-18 08:19:30');

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
CREATE TABLE IF NOT EXISTS `products` (
  `product_id` int NOT NULL AUTO_INCREMENT,
  `barcode` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `product_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `brand` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `category_id` int NOT NULL,
  `target_group` enum('Men','Women','Children','Unisex') COLLATE utf8mb4_unicode_ci NOT NULL,
  `size` enum('XS','S','M','L','XL','XXL','Free Size','Custom') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'M',
  `color` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `buying_price` decimal(10,2) NOT NULL,
  `selling_price` decimal(10,2) NOT NULL,
  `discount_percentage` decimal(5,2) DEFAULT '0.00',
  `stock_quantity` int NOT NULL DEFAULT '0',
  `status` enum('Active','Inactive') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Active',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`product_id`),
  UNIQUE KEY `barcode` (`barcode`),
  KEY `category_id` (`category_id`)
) ENGINE=MyISAM AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`product_id`, `barcode`, `product_name`, `brand`, `category_id`, `target_group`, `size`, `color`, `buying_price`, `selling_price`, `discount_percentage`, `stock_quantity`, `status`, `created_at`, `updated_at`) VALUES
(1, 'FFB-0001', 'Classic Denim Jeans', 'Levi\'s', 1, 'Unisex', 'M', 'Blue', 3500.00, 5990.00, 0.00, 100, 'Active', '2026-09-16 10:13:48', '2026-09-18 14:47:15'),
(2, 'FFB-0002', 'Formal Blazer', 'Zara', 2, 'Men', 'L', 'Black', 8000.00, 14990.00, 10.00, 50, 'Active', '2026-09-16 10:13:48', '2026-09-17 09:33:18'),
(3, 'FFB-0003', 'Running Sneakers', 'Nike', 3, 'Unisex', 'M', 'White', 6000.00, 11990.00, 0.00, 50, 'Active', '2026-09-16 10:13:48', '2026-09-18 14:39:05'),
(4, 'FFB-0004', 'Leather Loafers', 'Clarks', 4, 'Men', 'L', 'Brown', 7000.00, 12990.00, 0.00, 50, 'Active', '2026-09-16 10:13:48', '2026-09-17 09:33:24'),
(5, 'FFB-0005', 'Statement Necklace', 'Pandora', 5, 'Women', 'Free Size', 'Gold', 1500.00, 3990.00, 15.00, 50, 'Active', '2026-09-16 10:13:48', '2026-09-18 14:45:15'),
(6, 'FFB-0006', 'Childrens Sport T-Shirt', 'Adidas', 3, 'Children', 'S', 'Red', 900.00, 1990.00, 25.00, 80, 'Active', '2026-09-16 10:13:48', '2026-09-18 14:59:07');

-- --------------------------------------------------------

--
-- Table structure for table `promotions`
--

DROP TABLE IF EXISTS `promotions`;
CREATE TABLE IF NOT EXISTS `promotions` (
  `promotion_id` int NOT NULL AUTO_INCREMENT,
  `promotion_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `discount_percentage` decimal(5,2) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `status` enum('Active','Inactive') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Active',
  `created_by` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`promotion_id`),
  KEY `created_by` (`created_by`)
) ;

--
-- Dumping data for table `promotions`
--

INSERT INTO `promotions` (`promotion_id`, `promotion_name`, `description`, `discount_percentage`, `start_date`, `end_date`, `status`, `created_by`, `created_at`) VALUES
(2, 'Summer Sale', 'This is summer sale', 25.00, '2026-09-18', '2026-09-25', 'Active', 1, '2026-09-17 19:33:55');

-- --------------------------------------------------------

--
-- Table structure for table `promotion_products`
--

DROP TABLE IF EXISTS `promotion_products`;
CREATE TABLE IF NOT EXISTS `promotion_products` (
  `promotion_id` int NOT NULL,
  `product_id` int NOT NULL,
  PRIMARY KEY (`promotion_id`,`product_id`),
  KEY `product_id` (`product_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `promotion_products`
--

INSERT INTO `promotion_products` (`promotion_id`, `product_id`) VALUES
(2, 2),
(2, 3),
(2, 6);

-- --------------------------------------------------------

--
-- Table structure for table `sales`
--

DROP TABLE IF EXISTS `sales`;
CREATE TABLE IF NOT EXISTS `sales` (
  `sale_id` int NOT NULL AUTO_INCREMENT,
  `invoice_number` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` int NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `total_discount` decimal(10,2) DEFAULT '0.00',
  `net_total` decimal(10,2) NOT NULL,
  `sale_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`sale_id`),
  UNIQUE KEY `invoice_number` (`invoice_number`),
  KEY `user_id` (`user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `sales`
--

INSERT INTO `sales` (`sale_id`, `invoice_number`, `user_id`, `subtotal`, `total_discount`, `net_total`, `sale_date`) VALUES
(1, 'INV-1789633028233', 1, 20980.00, 2473.05, 18506.95, '2026-09-17 08:17:08'),
(2, 'INV-1789634141504', 1, 26980.00, 2773.05, 24206.95, '2026-09-17 08:35:41'),
(3, 'INV-1789637341073', 1, 18980.00, 0.00, 18980.00, '2026-09-17 09:29:01'),
(4, 'INV-1789637496496', 1, 20980.00, 1888.62, 19091.38, '2026-09-17 09:31:36'),
(5, 'INV-1789637649895', 1, 5990.00, 0.00, 5990.00, '2026-09-17 09:34:09'),
(6, 'INV-1789637691608', 1, 5990.00, 0.00, 5990.00, '2026-09-17 09:34:51'),
(7, 'INV-1789637965367', 1, 5990.00, 0.00, 5990.00, '2026-09-17 09:39:25'),
(8, 'INV-1789718415919', 1, 1990.00, 497.50, 1492.50, '2026-09-18 08:00:15'),
(9, 'INV-1789719570036', 1, 1990.00, 497.50, 1492.50, '2026-09-18 08:19:30');

-- --------------------------------------------------------

--
-- Table structure for table `sale_items`
--

DROP TABLE IF EXISTS `sale_items`;
CREATE TABLE IF NOT EXISTS `sale_items` (
  `sale_item_id` int NOT NULL AUTO_INCREMENT,
  `sale_id` int NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `discount_applied` decimal(10,2) DEFAULT '0.00',
  `line_total` decimal(10,2) NOT NULL,
  PRIMARY KEY (`sale_item_id`),
  KEY `sale_id` (`sale_id`),
  KEY `product_id` (`product_id`)
) ;

--
-- Dumping data for table `sale_items`
--

INSERT INTO `sale_items` (`sale_item_id`, `sale_id`, `product_id`, `quantity`, `unit_price`, `discount_applied`, `line_total`) VALUES
(1, 1, 1, 1, 5990.00, 5990.00, 0.00),
(2, 1, 2, 1, 14990.00, 13491.00, 1499.00),
(3, 2, 2, 1, 14990.00, 13491.00, 1499.00),
(4, 2, 3, 1, 11990.00, 11990.00, 0.00),
(5, 3, 1, 1, 5990.00, 5990.00, 0.00),
(6, 3, 4, 1, 12990.00, 12990.00, 0.00),
(7, 4, 1, 1, 5990.00, 5990.00, 0.00),
(8, 4, 2, 1, 14990.00, 13491.00, 1499.00),
(9, 5, 1, 1, 5990.00, 5990.00, 0.00),
(10, 6, 1, 1, 5990.00, 5990.00, 0.00),
(11, 7, 1, 1, 5990.00, 5990.00, 0.00),
(12, 8, 6, 1, 1990.00, 1492.50, 497.50),
(13, 9, 6, 1, 1990.00, 1492.50, 497.50);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `full_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` enum('Boutique Manager','Sales Assistant') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Sales Assistant',
  `status` enum('Active','Inactive') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Active',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `password_hash`, `full_name`, `email`, `role`, `status`, `created_at`, `updated_at`) VALUES
(1, 'admin', '0e79cbd52e5bba496d48a0102bc1a5934f4c9f6d6fa2c13ea7b00436ecdb84f2', 'Store Administrator', 'admin@lk', 'Boutique Manager', 'Active', '2026-09-16 10:13:48', '2026-09-16 10:19:53'),
(2, 'Cashier', '0e79cbd52e5bba496d48a0102bc1a5934f4c9f6d6fa2c13ea7b00436ecdb84f2', 'Nadeesha Perera', 'cashier1@lk', 'Sales Assistant', 'Active', '2026-09-16 10:13:48', '2026-09-18 15:23:22');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

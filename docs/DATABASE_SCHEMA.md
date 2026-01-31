# Database Schema Documentation

## Overview
This document describes the database schema for the Online Shopping Backend application.

**Database:** MySQL 8.0+  
**ORM:** Spring Data JPA (Hibernate)

---

## Tables

### 1. users
Stores user account information.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique user identifier |
| username | VARCHAR(50) | UNIQUE, NOT NULL | User's login username |
| email | VARCHAR(100) | UNIQUE, NOT NULL | User's email address |
| password | VARCHAR(255) | NOT NULL | Encrypted password (BCrypt) |
| full_name | VARCHAR(100) | NOT NULL | User's full name |
| role | VARCHAR(20) | NOT NULL | User role (USER, ADMIN) |
| wallet_balance | DECIMAL(10,2) | DEFAULT 0.00 | User's wallet balance |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Account creation timestamp |
| updated_at | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | Last update timestamp |

**Indexes:**
- `idx_username` on `username`
- `idx_email` on `email`

---

### 2. categories
Product categories.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Category identifier |
| name | VARCHAR(100) | UNIQUE, NOT NULL | Category name |
| description | TEXT | | Category description |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation timestamp |

---

### 3. products
Product catalog.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Product identifier |
| name | VARCHAR(200) | NOT NULL | Product name |
| description | TEXT | | Product description |
| price | DECIMAL(10,2) | NOT NULL | Product price |
| stock | INT | NOT NULL, DEFAULT 0 | Available stock quantity |
| category_id | BIGINT | FOREIGN KEY → categories(id) | Product category |
| image_url | VARCHAR(500) | | Product image URL |
| average_rating | DECIMAL(3,2) | DEFAULT 0.00 | Average rating (0-5) |
| review_count | INT | DEFAULT 0 | Number of reviews |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation timestamp |
| updated_at | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | Last update timestamp |

**Indexes:**
- `idx_category` on `category_id`
- `idx_price` on `price`
- `idx_rating` on `average_rating`

**Foreign Keys:**
- `fk_product_category` FOREIGN KEY (category_id) REFERENCES categories(id)

---

### 4. carts
Shopping carts for users.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Cart identifier |
| user_id | BIGINT | UNIQUE, FOREIGN KEY → users(id) | Cart owner |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation timestamp |
| updated_at | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | Last update timestamp |

**Foreign Keys:**
- `fk_cart_user` FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE

---

### 5. cart_items
Items in shopping carts.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Cart item identifier |
| cart_id | BIGINT | FOREIGN KEY → carts(id) | Parent cart |
| product_id | BIGINT | FOREIGN KEY → products(id) | Product reference |
| quantity | INT | NOT NULL, CHECK (quantity > 0) | Item quantity |
| price | DECIMAL(10,2) | NOT NULL | Price at time of adding |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation timestamp |

**Unique Constraint:**
- `uk_cart_product` UNIQUE (cart_id, product_id)

**Foreign Keys:**
- `fk_cartitem_cart` FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE
- `fk_cartitem_product` FOREIGN KEY (product_id) REFERENCES products(id)

---

### 6. orders
Customer orders.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Order identifier |
| order_number | VARCHAR(50) | UNIQUE, NOT NULL | Human-readable order number |
| user_id | BIGINT | FOREIGN KEY → users(id) | Customer |
| total_amount | DECIMAL(10,2) | NOT NULL | Total order amount |
| discount_amount | DECIMAL(10,2) | DEFAULT 0.00 | Discount applied |
| wallet_used | DECIMAL(10,2) | DEFAULT 0.00 | Wallet amount used |
| final_amount | DECIMAL(10,2) | NOT NULL | Final payable amount |
| status | VARCHAR(20) | NOT NULL | Order status |
| payment_method | VARCHAR(50) | | Payment method used |
| payment_id | VARCHAR(100) | | Payment gateway ID |
| shipping_address | TEXT | NOT NULL | Delivery address |
| coupon_code | VARCHAR(50) | | Applied coupon code |
| order_date | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Order placement time |
| updated_at | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | Last update timestamp |

**Order Status Values:**
- PENDING
- CONFIRMED
- PROCESSING
- SHIPPED
- DELIVERED
- CANCELLED
- REFUNDED

**Indexes:**
- `idx_user_orders` on `user_id`
- `idx_order_number` on `order_number`
- `idx_order_status` on `status`
- `idx_order_date` on `order_date`

**Foreign Keys:**
- `fk_order_user` FOREIGN KEY (user_id) REFERENCES users(id)

---

### 7. order_items
Items in orders.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Order item identifier |
| order_id | BIGINT | FOREIGN KEY → orders(id) | Parent order |
| product_id | BIGINT | FOREIGN KEY → products(id) | Product reference |
| quantity | INT | NOT NULL | Item quantity |
| price | DECIMAL(10,2) | NOT NULL | Price at time of order |
| subtotal | DECIMAL(10,2) | NOT NULL | Item subtotal |

**Foreign Keys:**
- `fk_orderitem_order` FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
- `fk_orderitem_product` FOREIGN KEY (product_id) REFERENCES products(id)

---

### 8. reviews
Product reviews and ratings.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Review identifier |
| product_id | BIGINT | FOREIGN KEY → products(id) | Reviewed product |
| user_id | BIGINT | FOREIGN KEY → users(id) | Reviewer |
| rating | INT | NOT NULL, CHECK (rating >= 1 AND rating <= 5) | Rating (1-5) |
| comment | TEXT | | Review comment |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Review timestamp |
| updated_at | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | Last update timestamp |

**Unique Constraint:**
- `uk_user_product_review` UNIQUE (user_id, product_id) - One review per user per product

**Indexes:**
- `idx_product_reviews` on `product_id`
- `idx_user_reviews` on `user_id`

**Foreign Keys:**
- `fk_review_product` FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
- `fk_review_user` FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE

---

### 9. coupons
Discount coupons.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Coupon identifier |
| code | VARCHAR(50) | UNIQUE, NOT NULL | Coupon code |
| discount_type | VARCHAR(20) | NOT NULL | PERCENTAGE or FIXED |
| discount_value | DECIMAL(10,2) | NOT NULL | Discount amount/percentage |
| min_order_amount | DECIMAL(10,2) | DEFAULT 0.00 | Minimum order for coupon |
| max_discount | DECIMAL(10,2) | | Maximum discount cap |
| valid_from | TIMESTAMP | NOT NULL | Coupon valid from |
| valid_until | TIMESTAMP | NOT NULL | Coupon expiry |
| usage_limit | INT | | Maximum usage count |
| used_count | INT | DEFAULT 0 | Current usage count |
| active | BOOLEAN | DEFAULT TRUE | Coupon active status |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation timestamp |

**Indexes:**
- `idx_coupon_code` on `code`
- `idx_coupon_validity` on `valid_from`, `valid_until`

---

## Entity Relationships

```
users (1) ──── (1) carts
users (1) ──── (N) orders
users (1) ──── (N) reviews

categories (1) ──── (N) products

carts (1) ──── (N) cart_items
cart_items (N) ──── (1) products

orders (1) ──── (N) order_items
order_items (N) ──── (1) products

products (1) ──── (N) reviews
```

---

## Database Initialization

The application uses Spring Data JPA with Hibernate for automatic schema generation.

**Configuration in `application.yml`:**
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update  # Use 'create' for fresh DB, 'update' for existing
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQL8Dialect
```

---

## Sample Data

For development, you can use the following SQL to insert sample data:

```sql
-- Insert categories
INSERT INTO categories (name, description) VALUES
('Electronics', 'Electronic devices and gadgets'),
('Clothing', 'Apparel and fashion'),
('Books', 'Books and literature'),
('Home & Kitchen', 'Home and kitchen appliances');

-- Insert admin user (password: admin123)
INSERT INTO users (username, email, password, full_name, role, wallet_balance) VALUES
('admin', 'admin@shop.com', '$2a$10$...', 'Admin User', 'ADMIN', 0.00);

-- Insert sample products
INSERT INTO products (name, description, price, stock, category_id) VALUES
('Laptop', 'High-performance laptop', 75000.00, 10, 1),
('T-Shirt', 'Cotton t-shirt', 499.00, 100, 2),
('Novel', 'Bestselling novel', 299.00, 50, 3);
```

---

## Backup and Maintenance

**Backup Command:**
```bash
mysqldump -u username -p shopping_db > backup_$(date +%Y%m%d).sql
```

**Restore Command:**
```bash
mysql -u username -p shopping_db < backup_20260131.sql
```

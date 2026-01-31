# API Documentation

## Authentication Endpoints

### Register User
- **URL:** `/api/auth/register`
- **Method:** `POST`
- **Request Body:**
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "fullName": "string"
}
```
- **Response:** `200 OK`
```json
{
  "token": "jwt_token_here",
  "username": "string",
  "email": "string"
}
```

### Login
- **URL:** `/api/auth/login`
- **Method:** `POST`
- **Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```
- **Response:** `200 OK`
```json
{
  "token": "jwt_token_here",
  "username": "string",
  "email": "string"
}
```

---

## Product Endpoints

### Get All Products
- **URL:** `/api/products`
- **Method:** `GET`
- **Query Parameters:**
  - `page` (optional): Page number (default: 0)
  - `size` (optional): Page size (default: 10)
  - `category` (optional): Filter by category
- **Response:** `200 OK`
```json
{
  "content": [
    {
      "id": 1,
      "name": "Product Name",
      "description": "Product Description",
      "price": 999.99,
      "stock": 50,
      "category": "Electronics",
      "imageUrl": "url"
    }
  ],
  "totalPages": 10,
  "totalElements": 100
}
```

### Get Product by ID
- **URL:** `/api/products/{id}`
- **Method:** `GET`
- **Response:** `200 OK`
```json
{
  "id": 1,
  "name": "Product Name",
  "description": "Product Description",
  "price": 999.99,
  "stock": 50,
  "category": "Electronics",
  "imageUrl": "url",
  "averageRating": 4.5,
  "reviewCount": 25
}
```

### Create Product (Admin Only)
- **URL:** `/api/products`
- **Method:** `POST`
- **Headers:** `Authorization: Bearer {token}`
- **Request Body:**
```json
{
  "name": "Product Name",
  "description": "Product Description",
  "price": 999.99,
  "stock": 50,
  "categoryId": 1
}
```
- **Response:** `201 Created`

### Update Product (Admin Only)
- **URL:** `/api/products/{id}`
- **Method:** `PUT`
- **Headers:** `Authorization: Bearer {token}`
- **Request Body:** Same as Create Product
- **Response:** `200 OK`

### Delete Product (Admin Only)
- **URL:** `/api/products/{id}`
- **Method:** `DELETE`
- **Headers:** `Authorization: Bearer {token}`
- **Response:** `204 No Content`

---

## Cart Endpoints

### Get User Cart
- **URL:** `/api/cart`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer {token}`
- **Response:** `200 OK`
```json
{
  "id": 1,
  "items": [
    {
      "productId": 1,
      "productName": "Product Name",
      "quantity": 2,
      "price": 999.99,
      "subtotal": 1999.98
    }
  ],
  "total": 1999.98
}
```

### Add Item to Cart
- **URL:** `/api/cart/items`
- **Method:** `POST`
- **Headers:** `Authorization: Bearer {token}`
- **Request Body:**
```json
{
  "productId": 1,
  "quantity": 2
}
```
- **Response:** `200 OK`

### Update Cart Item
- **URL:** `/api/cart/items/{productId}`
- **Method:** `PUT`
- **Headers:** `Authorization: Bearer {token}`
- **Request Body:**
```json
{
  "quantity": 3
}
```
- **Response:** `200 OK`

### Remove Item from Cart
- **URL:** `/api/cart/items/{productId}`
- **Method:** `DELETE`
- **Headers:** `Authorization: Bearer {token}`
- **Response:** `204 No Content`

---

## Order Endpoints

### Create Order
- **URL:** `/api/orders`
- **Method:** `POST`
- **Headers:** `Authorization: Bearer {token}`
- **Request Body:**
```json
{
  "shippingAddress": "123 Main St, City, State 12345",
  "couponCode": "SAVE10",
  "walletAmount": 50.00,
  "paymentMethod": "RAZORPAY"
}
```
- **Response:** `201 Created`
```json
{
  "orderId": "ORD123456",
  "razorpayOrderId": "order_xyz",
  "amount": 949.99,
  "currency": "INR"
}
```

### Get User Orders
- **URL:** `/api/orders`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer {token}`
- **Response:** `200 OK`
```json
[
  {
    "id": 1,
    "orderNumber": "ORD123456",
    "orderDate": "2026-01-31T10:30:00",
    "status": "DELIVERED",
    "total": 949.99,
    "items": [...]
  }
]
```

### Get Order by ID
- **URL:** `/api/orders/{id}`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer {token}`
- **Response:** `200 OK`

---

## Review Endpoints

### Add Review
- **URL:** `/api/products/{productId}/reviews`
- **Method:** `POST`
- **Headers:** `Authorization: Bearer {token}`
- **Request Body:**
```json
{
  "rating": 5,
  "comment": "Great product!"
}
```
- **Response:** `201 Created`

### Get Product Reviews
- **URL:** `/api/products/{productId}/reviews`
- **Method:** `GET`
- **Response:** `200 OK`
```json
[
  {
    "id": 1,
    "username": "user123",
    "rating": 5,
    "comment": "Great product!",
    "createdAt": "2026-01-31T10:30:00"
  }
]
```

---

## Analytics Endpoints (Admin Only)

### Get Dashboard Analytics
- **URL:** `/api/analytics/dashboard`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer {token}`
- **Response:** `200 OK`
```json
{
  "totalRevenue": 50000.00,
  "totalOrders": 250,
  "totalUsers": 150,
  "topProducts": [...]
}
```

---

## Error Responses

All endpoints may return the following error responses:

### 400 Bad Request
```json
{
  "message": "Invalid request",
  "timestamp": "2026-01-31T10:30:00"
}
```

### 401 Unauthorized
```json
{
  "message": "Authentication required",
  "timestamp": "2026-01-31T10:30:00"
}
```

### 404 Not Found
```json
{
  "message": "Resource not found",
  "timestamp": "2026-01-31T10:30:00"
}
```

### 500 Internal Server Error
```json
{
  "message": "Internal server error",
  "timestamp": "2026-01-31T10:30:00"
}
```

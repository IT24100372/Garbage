# ♻️ GreenCollect - Garbage Collection Booking System

A full-stack Garbage Collection Booking System with Payment Gateway built using:
- **Frontend**: HTML5 + CSS3 (no frameworks)
- **Backend**: Java 17 + Spring Boot 3.2
- **Database**: MySQL 8

---

## 📁 Project Structure

```
garbage-booking-system/
├── pom.xml
└── src/
    └── main/
        ├── java/com/garbage/booking/
        │   ├── GarbageBookingApplication.java
        │   ├── controller/
        │   │   ├── UserController.java       → /api/users
        │   │   ├── BookingController.java    → /api/bookings
        │   │   └── PaymentController.java   → /api/payments
        │   ├── model/
        │   │   ├── User.java
        │   │   ├── Booking.java
        │   │   └── Payment.java
        │   ├── repository/
        │   │   ├── UserRepository.java
        │   │   ├── BookingRepository.java
        │   │   └── PaymentRepository.java
        │   └── service/
        │       ├── UserService.java
        │       ├── BookingService.java
        │       └── PaymentService.java
        └── resources/
            ├── application.properties       → DB config
            ├── schema.sql                   → DB schema + seed data
            └── static/                      → Frontend
                ├── index.html               → Landing page
                ├── login.html               → Login page
                ├── register.html            → Register page
                ├── dashboard.html           → User dashboard
                ├── admin-dashboard.html     → Admin dashboard
                ├── booking.html             → New booking (3-step wizard)
                ├── payment.html             → Payment gateway
                ├── payment-success.html     → Success confirmation
                └── css/
                    └── style.css            → All styles
```

---

## ⚙️ Setup & Run

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8+

### 1. Configure Database
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/garbage_booking_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### 2. Run the Database Schema
```sql
-- Run schema.sql in MySQL Workbench or terminal
source src/main/resources/schema.sql;
```

### 3. Build & Run
```bash
cd garbage-booking-system
mvn spring-boot:run
```

The application starts at: **http://localhost:8080**

---

## 🌐 Frontend Pages

| Page | URL | Description |
|------|-----|-------------|
| Landing | `/index.html` | Home page with features and waste types |
| Register | `/register.html` | User registration |
| Login | `/login.html` | User login |
| Dashboard | `/dashboard.html` | Booking stats + history |
| Admin Dashboard | `/admin-dashboard.html` | Admin management dashboard |
| New Booking | `/booking.html` | 3-step booking wizard |
| Payment | `/payment.html` | Secure payment gateway |
| Success | `/payment-success.html` | Payment confirmation |

---

## 🔗 REST API Endpoints

### Users
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users/register` | Register new user |
| POST | `/api/users/login` | User login |
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |

### Bookings
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/bookings/user/{userId}` | Create booking |
| GET | `/api/bookings` | All bookings |
| GET | `/api/bookings/{id}` | Booking by ID |
| GET | `/api/bookings/user/{userId}` | User's bookings |
| PUT | `/api/bookings/{id}` | Update booking |
| PATCH | `/api/bookings/{id}/status` | Update status |
| DELETE | `/api/bookings/{id}/cancel` | Cancel booking |
| GET | `/api/bookings/stats` | Booking statistics |

### Payments
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/payments/booking/{bookingId}` | Process payment |
| GET | `/api/payments` | All payments |
| GET | `/api/payments/{id}` | Payment by ID |
| GET | `/api/payments/booking/{bookingId}` | Payment for booking |
| GET | `/api/payments/user/{userId}` | User's payments |
| POST | `/api/payments/{id}/refund` | Refund payment |

### Admin Dashboard APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/admin/login` | Admin-only login check |
| GET | `/api/admin/auth/{userId}` | Validate admin session |
| GET | `/api/admin/summary` | Dashboard summary (total/completed/pending/cancelled) |
| GET | `/api/admin/bookings` | Filterable bookings list (`status`, `date`, `user`, `payment`) |
| GET | `/api/admin/bookings/completed` | Completed bookings list |
| PUT | `/api/admin/booking/{id}/approve` | Approve booking |
| PUT | `/api/admin/booking/{id}/complete` | Mark booking completed |
| DELETE | `/api/admin/booking/{id}` | Delete booking |
| GET | `/api/admin/payments` | Payment management list |
| GET | `/api/admin/notifications/new-bookings?afterId={id}` | New booking notifications |

### Requested Alias Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/bookings` | Get all bookings (admin DTO) |
| PUT | `/booking/{id}/complete` | Mark booking as completed |
| DELETE | `/booking/{id}` | Delete booking |
| GET | `/payments` | Get payment details |

---

## 💳 Supported Payment Methods
- Credit Card
- Debit Card
- UPI (GPay / PhonePe / Paytm)
- Net Banking
- Cash on Collection

## 🗑️ Waste Types & Pricing
| Waste Type | Price |
|------------|-------|
| General Waste | ₹200 |
| Recyclable Waste | ₹150 |
| Organic Waste | ₹100 |
| Hazardous Waste | ₹500 |
| Electronic Waste | ₹400 |
| Construction Debris | ₹800 |

---

## 👥 Test Credentials
| Role | Email | Password |
|------|-------|----------|
| Admin | admin@greencollect.in | admin123 |
| User | test@greencollect.in | test1234 |

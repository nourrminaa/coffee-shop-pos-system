# Coffee Shop POS - JavaFX + MySQL Application

This repository contains a complete **Point-of-Sale (POS)** system implemented in Java using **JavaFX**, **MySQL**, modular handlers, and clean OOP design patterns.

## Summary
**Project:** Coffee Shop POS  
**Contents:** Role-based authentication, inventory & promotions management, cart & checkout workflow, receipt generation, PDF exporting, and sales reporting.  
**Structure:** Organized into packages: `ui`, `handlers`, `models`, `db`, `factory`, `observers`, `utils`.

# Contents

Below is a grouped overview of the system’s components to match the application's internal architecture and layout.

---

## Part 1 - Database Layer & Configuration

### `db/`
**DBConnectMySQL**  
- Central JDBC connection  
- Loads `.env` using dotenv-java  
- Exposes shared `Statement` for handlers  

### Environment Variables  
Loaded from `.env`:  
`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`

---

## Part 2 - Authentication & User Factory

### Models (`models/`)
- **User**  
- **Admin**  
- **Cashier**  
- **UserRow**  

### Factory (`factory/`)
- **IUserFactory** - user creation contract  
- **DefaultUserFactory** - builds Admin/Cashier based on role  

### Handlers (`handlers/`)
- **LoginButtonHandler**  
- **LogoutButtonHandler**  

### UI (`ui/LoginView`)
- Login form  
- SHA-256 password verification  
- Role-based routing  

---

## Part 3 - Inventory Module

### Models
- **ProductRow**  
- **InventoryRow**  

### Handlers
- **AddProductHandler**  
- **UpdateProductHandler**  
- **DeleteProductHandler**  

### UI (`ui/InventoryView`)
- Product table  
- Stock & pricing management  
- Add/update/delete actions  

---

## Part 4 - Promotions Module

### Models
- **PromotionRow**

### Handlers
- **AddPromotionHandler**  
- **UpdatePromotionHandler**  
- **DeletePromotionHandler**

### UI (`ui/PromotionsView`)
- Promotion list display  
- Activation status  
- Integrated with checkout discount menu  

---

## Part 5 - Orders, Cart, and Checkout

### Models
- **CartItem**

### UI (`ui/OrdersView`)
- Loads products by category  
- Displays items in VBoxes/HBoxes  
- Loads active promotions  
- Handles cart UI updates  

### Handlers
- **AddToCartHandler**  
- **CartPlusHandler**  
- **CartMinusHandler**  
- **CartRemoveHandler**  
- **ApplyDiscountHandler**  
- **CheckoutHandler**  
- **SearchOrdersHandler**  

---

## Part 6 - Receipt System

### Observer (`observers/`)
- **ReceiptObserver**  
  - Updates subtotal  
  - Updates discount  
  - Updates total  
  - Renders receipt item list  

### Utils (`utils/`)
- **ReceiptPDFThread** - builds receipt PDF  
- **ExportPDFHandler** - triggers PDF creation  

---

## Part 7 - Reporting Module

### Models
- **ReportData**  
- **SalesByCashierRow**  
- **TopProductRow**  
- **DateRange**

### Handlers
- **GenerateReportHandler**

### Utils
- **ReportsPDFThread**

### UI (`ui/ReportsView`)
- Date-range filtering  
- Sales & analytics tables  
- PDF summary exporting using a **Thread**

---

## Part 8 - UI Layer (JavaFX)

### Views (`ui/`)
- **LoginView**  
- **InventoryView**  
- **OrdersView**  
- **PromotionsView**  
- **ReportsView**  
- **UsersView**

### Utilities (`utils/`)
- **ThemeUI** - shared styles and formatting  
- **CategoryUtils** - product category filtering  

---

## Part 9 - Security

### SQL Injection Prevention
This project currently uses **string sanitization** to reduce SQL injection risks by escaping single quotes in user input:

```java
String safeInput = input.replace("'", "''");
```

Doubling single quotes prevents attackers from prematurely closing string literals in SQL queries and helps neutralize basic injection attempts.

This method improves safety for simple cases, but it is **not equivalent to prepared statements**.  
Prepared statements remain the recommended long-term solution for robust SQL injection protection. 

### Password Security
- Passwords hashed using **SHA-256**  
- No plaintext storage  

---

## Part 10 - Build & Run

### Using Maven Wrapper
```
./mvnw clean package
./mvnw javafx:run
```

### IDE (IntelliJ)
- Import as Maven project  
- Ensure JavaFX VM options  
- Ensure `.env` path is correct! As well as the pdf creation destination paths.  

---

# Skills Gained

- **JavaFX application structure**  
- **Modular handler-based event logic**  
- **Factory Pattern** (User creation)  
- **Observer Pattern** (receipt updates)  
- **JDBC and secure SQL data handling**  
- **PDF generation in Java**  
- **Threading & background tasks**  
- **UI/UX with JavaFX layouts and components**  

# POS & Inventory Management System

A desktop-based Point of Sale (POS) and Inventory Management application designed for retail store operations. Built with Java Swing and MySQL, this system delivers real-time transaction processing, role-based security, automated promotion handling, and inventory tracking.

---

## 🚀 Key Features

* **Role-Based Access Control (RBAC):** Authenticates users via secure hashed credentials, restricting administrative tools from standard staff.
* **Real-Time POS & Checkout:** Handles product scanning, cart itemization, automated tax calculation, dynamic promotional discounts, and transaction receipts.
* **ACID-Compliant Transactions:** Implements explicit JDBC transaction controls (`setAutoCommit(false)`, `commit()`, and `rollback()`) to prevent partial stock updates or orphaned order records during checkout failure.
* **Inventory Management:** Supports full CRUD operations, live item search, low-stock notifications, and inventory restock tracking.
* **Reporting & Analytics:** Generates sales reports and tracks daily revenue performance.

---

## 🛠 Architecture & Technical Stack

### **Technology Stack**
* **Language/Platform:** Java SE (JDK 17+)
* **GUI Framework:** Java Swing
* **Database:** MySQL Server / MariaDB
* **Database Driver:** JDBC (MySQL Connector/J)
* **Build Tool:** Apache Ant (`build.xml`)
* **Version Control & Management:** Git, GitHub, Atlassian Jira

### **Design Patterns & Standards**
* **DAO (Data Access Object) Pattern:** Encapsulates raw SQL execution away from GUI components for maintainability.
* **Singleton Pattern:** Manages database connection instances efficiently to reduce overhead and avoid connection leaks.
* **Event Dispatch Thread (EDT) Compliance:** Keeps UI operations responsive by executing database calls off the main event thread.

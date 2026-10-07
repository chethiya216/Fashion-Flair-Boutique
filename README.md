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

### **Preview**
* **Login Page**
  
  <img width="501" height="737" alt="Screenshot 2026-10-07 193816" src="https://github.com/user-attachments/assets/d4a01099-4e27-4b8f-9f56-34c07065ef1d" />

* **Reset Password Page**
  
  <img width="446" height="712" alt="Screenshot 2026-10-07 194934" src="https://github.com/user-attachments/assets/7b881416-8a3d-46fc-8b58-fabb006ad1f1" />

* **Dashboard Page**
  <img width="1159" height="811" alt="Screenshot 2026-10-07 194329" src="https://github.com/user-attachments/assets/282c762d-7401-495a-8881-cb6dd9dd61d1" />

* **Manage Users Page**
  
  <img width="1482" height="902" alt="Screenshot 2026-10-07 194400" src="https://github.com/user-attachments/assets/0eb66e5c-5d5f-444f-a7d7-a08e12c25273" />

* **Manage Products Page**
  
  <img width="1758" height="992" alt="Screenshot 2026-10-07 194443" src="https://github.com/user-attachments/assets/c1835c5b-d3ad-4083-a238-804c789324f9" />

* **Product Catalog Page**
  
  <img width="1723" height="819" alt="Screenshot 2026-10-07 194431" src="https://github.com/user-attachments/assets/a11112f8-71a6-4217-9a28-cc49d4657e1c" />

* **POS Page**
  
  <img width="1889" height="979" alt="Screenshot 2026-10-07 194418" src="https://github.com/user-attachments/assets/231a0daf-2c8f-4893-b58f-5f8353b78e5c" />

* **Sales Reports Page**
  
  <img width="1766" height="978" alt="Screenshot 2026-10-07 194503" src="https://github.com/user-attachments/assets/2d570130-05af-419b-874c-682dc30597d4" />

* **Manage Discounts Page**
  
  <img width="1718" height="884" alt="Screenshot 2026-10-07 194519" src="https://github.com/user-attachments/assets/e7797106-def2-47c7-8692-1b5778039b5b" />

* **Manage Catagories Page**
  
  <img width="1576" height="712" alt="Screenshot 2026-10-07 194529" src="https://github.com/user-attachments/assets/083aa1aa-3aac-4923-8d0d-7539b710d0a2" />

# 🚀 FixTrack Pro | Smart Municipal & Community Portal

FixTrack Pro is an advanced municipal issue-tracking and community engagement platform engineered to bridge the gap between citizens and local government authorities. The system features a synchronized Android mobile application, a robust PHP/MySQL backend API, and a fully featured web portal with role-based dashboards.

---

## 👥 Development Team
This project was designed, architected, and built by:
* **TRISTAN**
* **DEVON**
* **KEEYAN**

---

## 🌟 Key Features & Architecture

* **Role-Based Portals:** Secure authentication distinguishing between standard Citizens (`User`) and System Administrators (`Admin`/`Lecturer`).
* **Android & Web Sync:** Seamless database synchronization allowing citizens to log issues and upload photos from mobile or web.
* **Citizen Features:** 
  * Interactive Landing Page with community infrastructure showcase.
  * Dedicated **My Reports** progress tracker (`?view=my_reports`) to monitor issue statuses in real-time.
  * Community Raffles & Winners display (accessible to logged-in users).
  * Direct Municipal Support Contact and Inbox replies.
* **Admin Control Center:** Report resolution management (Pending, In Progress, Resolved), image attachments previewing, monthly raffle winner drawing, and an administrative inbox for user queries.

---

## ⚙️ Local Development Setup (XAMPP & PHP)

To run FixTrack Pro locally on your machine using XAMPP, follow these step-by-speed instructions:

### 1. Prerequisites
* Download and install [XAMPP](https://www.apachefriends.org/index.html) (featuring Apache and MySQL).

### 2. Place Project Files in `htdocs`
1. Navigate to your XAMPP root directory (typically `C:\xampp\`).
2. Open the **`htdocs`** folder (`C:\xampp\htdocs\`).
3. Place your project folder (e.g., `fixtrack_api`) directly inside `htdocs` so the path reads:
   `C:\xampp\htdocs\fixtrack_api\`

### 3. Configure the Database
1. Open the **XAMPP Control Panel** and click **Start** next to **Apache** and **MySQL**.
2. Open your browser and go to `http://localhost/phpmyadmin/`.
3. Create a new database matching your configuration (e.g., `fixtrack_db`).
4. Import your database tables (`users`, `reports`, `contact_messages`) or ensure your `db_connect.php` file points to your local database credentials:
   - **Host:** `localhost`
   - **Username:** `root`
   - **Password:** `` (empty by default)
   - **Database Name:** `fixtrack_db`

### 4. Accessing the Web Portal
Open your web browser and navigate to:
```text
http://localhost/fixtrack_api/index.php

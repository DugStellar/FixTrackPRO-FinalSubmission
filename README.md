# FixTrack Pro — Cape IT Assist Repair & Issue Management System

![CI Pipeline](https://github.com/DugStellar/FixTrackPRO-FinalSubmission/actions/workflows/ci.yml/badge.svg)

**FixTrack Pro** is an integrated hardware/software repair and issue management system developed by **Cybertech Solutions** for **Cape IT Assist** (XISD6329 Portfolio of Evidence — Final Submission). The platform digitizes client issue reporting, ticket tracking, and administrative triage across a native Android application, a responsive web microsite, and a centralized PHP/MySQL RESTful API.

---

## 🔗 Project Repositories, Links & Video Demo
* **GitHub Repository (Primary Source & CI/CD):** [https://github.com/DugStellar/FixTrackPRO-FinalSubmission](https://github.com/DugStellar/FixTrackPRO-FinalSubmission)
* **Azure DevOps Repository & Boards:** [https://dev.azure.com/ST10439194/_git/FixTrack%20Pro%20Group%20Assignment](https://dev.azure.com/ST10439194/_git/FixTrack%20Pro%20Group%20Assignment)
* **Project Demonstration Video (YouTube):** [https://youtu.be/SclorUEk0_I?si=9eMdyMg_U-riteSH](https://youtu.be/SclorUEk0_I?si=9eMdyMg_U-riteSH)
* **Web Microsite URL (XAMPP / Local Network):** `http://10.0.0.4/fixtrack_api/index.php` (or `http://localhost/fixtrack_api/index.php` if browsing locally on your PC)
* **Database Health Check URL:** `http://10.0.0.4/fixtrack_api/db.php`

---

## 👥 Team Roster & Roles (Cybertech Solutions)
| Team Member | Student Number | Primary Role | Key Responsibilities |
| :--- | :--- | :--- | :--- |
| **Devon Adams** | ST10439194 | Project Lead & QA Engineer | Android Kotlin architecture, QA testing, unit/UI validation |
| **Tristan Booysen** | ST10452370 | Lead System Architect & DevOps | Full-stack integration, web microsite, PHP/MySQL API, CI/CD pipeline |
| **Keeyan Bowman** | ST10076256 | Requirements Analyst & Backend Architect | Requirements documentation, database schema, release packaging |

---

## 🏗️ System Architecture & Tech Stack
* **Mobile Frontend:** Native Android application built in **Kotlin** (Android Studio), communicating asynchronously with the PHP backend via HTTP/JSON.
* **Web Microsite & Admin Portal:** Responsive **HTML5, CSS3, and JavaScript** interface providing public issue submission, ticket reference generation, and an authenticated Admin triage dashboard.
* **Backend API:** Modular **PHP 8.x** endpoints hosted under `htdocs/` using **PDO prepared statements** for secure MySQL queries.
* **Database:** **MySQL** (`fixtrack_db`) managed via XAMPP / phpMyAdmin.
* **DevOps & CI/CD:** Agile sprint tracking on **Azure DevOps** alongside automated PHP syntax linting and repository structure verification via **GitHub Actions** (`.github/workflows/ci.yml`).

---

## 📂 Repository Structure
```text
FixTrackPRO-FinalSubmission/
├── .github/
│   └── workflows/
│       └── ci.yml              # Automated GitHub Actions CI pipeline
├── AndroidApp/                 # Complete Android Studio Kotlin source code
├── htdocs/                     # Web microsite (index.php) & PHP RESTful API endpoints
├── fixtrack_db.sql             # MySQL database schema and initial data
└── README.md

___

📖 How to Use FixTrack Pro (Complete End-to-End Guide)
Follow this unified walkthrough to start the local XAMPP server, operate the web portal, and install/run the Android application:

Start XAMPP Services: Open the XAMPP Control Panel on your PC and click Start next to both Apache and MySQL.

Deploy the Backend & Web Files: Copy the contents of this repository's htdocs/ folder into C:\xampp\htdocs\fixtrack_api\.

Import the Database: Open http://localhost/phpmyadmin in your browser, create a new database named fixtrack_db, click the Import tab, and import fixtrack_db.sql from this repository. Confirm the connection works by visiting http://10.0.0.4/fixtrack_api/db.php.

Use the Website (Client Issue Submission): Open http://10.0.0.4/fixtrack_api/index.php (or http://localhost/fixtrack_api/index.php) in your browser. Click Submit New Report, fill in your Issue Title, Category (Hardware, Network, Software), and Description, then click Submit & Get Ticket to save the issue to MySQL and generate your tracking reference code (e.g., FIX-36795).

Use the Website (Admin Triage Dashboard): Log into the Admin Panel view on the web interface to view all submitted tickets. Click Progress or Resolve next to any ticket to update its live status (Pending → In Progress → Resolved) in the database.

Install the Android App (APK or Source):

Pre-Built APK (Recommended): Go to the Releases tab on this repository, download app-release.apk, and either install it on your Android phone (enabling Install from Unknown Sources) or drag-and-drop it directly into BlueStacks on your PC.

Build from Source: Open AndroidApp/ in Android Studio, verify the API base URL is set to http://10.0.0.4/fixtrack_api/ (for BlueStacks/physical devices) or http://10.0.2.2/fixtrack_api/ (for the Android Studio emulator), and click Run ▶.

Use the Android App with XAMPP: With Apache and MySQL running, launch FixTrack Pro on your device or emulator. Register a new user account or log in (your session is stored dynamically via SharedPreferences), submit new repair/issue reports, and view live status updates synced directly with fixtrack_db.

📝 Release Notes (v1.0.0 — Final Submission)
Web & Mobile Issue Reporting: Clients can log hardware/software issues and receive an instant tracking reference code (e.g., FIX-36795).

Admin Triage Dashboard: Real-time ticket management allowing administrators to transition tickets between Pending, In Progress, and Resolved.

Dynamic Session Authentication: Replaced prototype hard-coded user IDs with authenticated session persistence (SharedPreferences).

Dual DevOps Repository & CI Pipeline: Integrated Azure DevOps project tracking with a GitHub Actions workflow (.github/workflows/ci.yml) that runs automated PHP syntax linting (php -l) and project structure verification on every push.

Production Android Binaries: Generated production-signed .apk and .aab release artifacts using a dedicated release keystore.

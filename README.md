# FixTrack Pro — Cape IT Assist Repair & Issue Management System

![CI Pipeline](https://github.com/DugStellar/FixTrackPRO-FinalSubmission/actions/workflows/ci.yml/badge.svg)

**FixTrack Pro** is an integrated hardware/software repair and issue management system developed by **Cybertech Solutions** for **Cape IT Assist** (XISD6329 Portfolio of Evidence — Final Submission). The platform digitizes client issue reporting, ticket tracking, and administrative triage across a native Android application, a responsive web microsite, and a centralized PHP/MySQL RESTful API.

---

## 🎥 Project Demonstration Video
* **Unlisted YouTube Walkthrough:** [Insert Your Unlisted YouTube Link Here][cite: 2]

---

## 📦 Download & Install (Android App)
To bypass Google Play Console developer fees while keeping the production build accessible for assessment and testing, the signed Android binaries are hosted directly under **GitHub Releases**:
1. Navigate to the **[Releases](https://github.com/DugStellar/FixTrackPRO-FinalSubmission/releases)** section on the right sidebar of this repository.
2. Download **`app-release.apk`** for direct installation on any Android device or emulator (such as BlueStacks).
3. *(Optional)* **`app-release.aab`** is also attached as the signed Android App Bundle artifact.

---

## 👥 Team Roster & Roles (Cybertech Solutions)
| Team Member | Student Number | Primary Role | Key Responsibilities |
| :--- | :--- | :--- | :--- |
| **Devon Adams** | ST10439194 | Project Lead & QA Engineer | Android Kotlin architecture, QA testing, unit/UI validation |
| **Tristan Booysen** | ST10452370] | Lead System Architect & DevOps | Full-stack integration, web microsite, PHP/MySQL API, CI/CD pipeline |
| **Keeyan Bowman** | [ST10076256] | Requirements Analyst & Backend Architect | Requirements documentation, database schema, release packaging |

---

## 🏗️ System Architecture & Tech Stack
* **Mobile Frontend:** Native Android application built in **Kotlin** (Android Studio), communicating asynchronously with the backend API using JSON/HTTP requests.
* **Web Microsite & Admin Portal:** Responsive **HTML5, CSS3, and JavaScript** interface providing public issue submission, ticket reference generation, and an authenticated Admin triage dashboard[cite: 2].
* **Backend API:** Modular **PHP 8.x** endpoints hosted under `htdocs/` using **PDO prepared statements** for secure database operations[cite: 2].
* **Database:** **MySQL** (`fixtrack_db`) managed via XAMPP / phpMyAdmin[cite: 2].
* **DevOps & CI:** Automated syntax linting and repository structure verification via **GitHub Actions** (`.github/workflows/ci.yml`)[cite: 2].

---

## 📂 Repository Structure
```text
FixTrackPRO-FinalSubmission/
├── .github/
│   └── workflows/
│       └── ci.yml              # Automated GitHub Actions CI pipeline
├── AndroidApp/                 # Complete Android Studio Kotlin source code
├── htdocs/                     # Web microsite & PHP RESTful API endpoints
├── fixtrack_db.sql             # MySQL database schema and seed structure
└── README.md                   # Project documentation & setup guide

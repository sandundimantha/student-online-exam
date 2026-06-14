# Full-Stack Online Examination Management System

An enterprise-ready, multi-platform Online Examination Management System built to facilitate secure digital testing, grading, and reporting.

## Project Structure

This repository contains three main components:

1. **[Backend (Spring Boot 3)](file:///d:/antigravity/Online%20Examination%20System/backend)**: REST APIs, JWT authentication, MySQL database interactions, and dynamic PDF result sheet generation.
2. **[Android Client (Jetpack Compose)](file:///d:/antigravity/Online%20Examination%20System/android-app)**: Kotlin-based mobile application with 15-second draft auto-saving, countdown timer, and secure screenshot prevention (`FLAG_SECURE`).
3. **[Web Frontend (Vite React)](file:///d:/antigravity/Online%20Examination%20System/frontend)**: TypeScript + Tailwind CSS web interface with specialized dashboards for Administrators, Lecturers, and Students.

---

## Role-Based Feature Matrix

### 👤 Administrator Panel
- Complete Student Management (CRUD)
- Complete Lecturer Management (CRUD)
- Subject Management and associations
- Live Exam analytics dashboard (Active counts, pass rates, etc.)

### 🎓 Lecturer Console
- Exam lifecycle management (Draft, Publish, Archive)
- Multiple question types: Multiple Choice (MCQ), True/False, and Short Answer
- Question Bank builder
- Student result performance reports

### ✍️ Student Portal
- View and attempt active examinations
- Real-time countdown timer with auto-submit on expiration
- Background auto-save (every 15 seconds) to prevent progress loss
- Result dashboard and PDF download of grading sheets

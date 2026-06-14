# Spring Boot 3 Backend - Online Examination System

This module provides the enterprise-level REST API and business logic for the Online Examination Management System.

## Technologies Used
- **Java 21 / JDK 25**
- **Spring Boot 3.3.0**
- **Spring Security & JWT** (Json Web Token authentication)
- **Spring Data JPA & Hibernate**
- **MySQL Database**
- **Apache PDFBox** (for generating high-quality result cards and reports as PDFs)

## Key Features
1. **JWT Authentication**: Secure login, registration, and role-based request filters.
2. **Dynamic Question Bank**: Supports MCQ, True/False, and Short Answer questions.
3. **Exam Management**: Lecturers can create, update, and publish exams.
4. **Auto-Submit & Autosave**: API endpoints support periodic draft saving and automatic submission when the exam timer expires.
5. **PDF Report Generation**: Students and lecturers can retrieve exam performance cards formatted as PDFs.

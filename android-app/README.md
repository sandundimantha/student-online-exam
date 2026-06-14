# Android Client - Online Examination System

This module contains the Jetpack Compose Android client application.

## Technologies Used
- **Kotlin**
- **Jetpack Compose**
- **Material 3**
- **Hilt** (for dependency injection)
- **Room Database** (for local caching, offline buffering, and autosave management)
- **Retrofit & Gson** (for API calls to the Spring Boot backend)
- **Coroutines & Flow** (for responsive reactive states)

## Architecture Highlights
- **Clean Architecture / MVVM Pattern**
- **Local SQLite Caching**: Rooms is used to save progress in real-time, preventing data loss on connectivity dropouts.
- **Auto-save & Background Loop**: A coroutine ticks every 15 seconds to cache state and update the server backend.
- **FLAG_SECURE Integration**: Prevents screenshotting and video recording on the exam-taking layout.

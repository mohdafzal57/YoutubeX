# YouTube Clone Android App (YoutubeX - Jetpack Compose, Clean Architecture)

YoutubeX is a modern **YouTube clone Android app** built using Jetpack Compose, Clean Architecture, and REST APIs.  
It demonstrates how to build a scalable **video streaming app clone** with features like authentication, video upload, subscriptions, and offline caching.

---

## 🔍 Keywords
youtube clone android, youtube clone jetpack compose, video streaming app android, youtube clone github, android video app project, kotlin youtube clone

---

## 🚀 Features

- 🎬 Video Feed: Browse and watch videos with smooth playback experience
- 📱 Social Feed: Community posts with images, likes, and comments
- 🔔 Subscriptions: Real-time subscription updates
- 👤 Channel Profiles: View channel data, subscribers, and uploads
- 🔎 Search: Find videos and channels instantly
- 🔐 Authentication: JWT-based login & signup system
- 🌙 Dark Mode: Material 3 light/dark theme support
- 💾 Offline Caching: Room database for offline posts
- ⬆️ Video Upload: Upload videos directly from the app

---

## 🛠 Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: Clean Architecture (MVVM)
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp
- **Database**: Room (offline caching)
- **Pagination**: Paging 3 + RemoteMediator
- **Image Loading**: Coil
- **Navigation**: Navigation Compose
- **Session Management**: DataStore

---

## 🏗 Architecture

This project follows **Clean Architecture** for scalability and maintainability:

1. **Presentation Layer**
    - Jetpack Compose UI
    - ViewModels using StateFlow & SharedFlow

2. **Domain Layer**
    - Business logic
    - Use cases and repository interfaces

3. **Data Layer**
    - API services (Retrofit)
    - DTOs and repository implementations
    - Room database

---

## 🌐 Live Demo

- 🎥 Watch Demo: https://res.cloudinary.com/dxbwode3q/video/upload/v1775850956/screen-20260411-012459_i2whag.mp4
- 📱 Download APK: https://github.com/mohdafzal57/YoutubeX/releases/download/v1.0.0/app-release.apk

---

## 📌 About This Project

This project is built to demonstrate modern Android development practices using Kotlin and Jetpack Compose.  
It replicates core features of YouTube such as video streaming, authentication, and social interaction while following scalable architecture patterns.

---

## 🚦 Getting Started

### Prerequisites

- Android Studio Ladybug | 2024.2.1 or newer
- JDK 17
- Android SDK 35 (API level 35)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/youtubex.git
    ```
2.  Open the project in Android Studio.
3.  Add your `BASE_URL` to `gradle.properties` or `BuildConfig`.
4.  Sync Gradle and run the app on an emulator or physical device.

## 📸 Screenshots

<p align="center">
  <img src="https://res.cloudinary.com/dxbwode3q/image/upload/v1775843404/scs_1_kaycgh.jpg" width="230" style="margin:10px;"/>
  <img src="https://res.cloudinary.com/dxbwode3q/image/upload/v1775843874/scs_6_ibhjsg.jpg" width="230" style="margin:10px;"/>
  <img src="https://res.cloudinary.com/dxbwode3q/image/upload/v1775843404/scs_2_hfqrzb.jpg" width="230" style="margin:10px;"/>
</p>

<p align="center">
  <img src="https://res.cloudinary.com/dxbwode3q/image/upload/v1775844514/scs_3_eyq2bd.jpg" width="230" style="margin:10px;"/>
  <img src="https://res.cloudinary.com/dxbwode3q/image/upload/v1775843404/scs_5_tnoqt6.jpg" width="230" style="margin:10px;"/>
</p>

---
Developed by [Mohd Afzal](https://github.com/mohdafzal57)

# YoutubeX - A Modern YouTube Clone

YoutubeX is a feature-rich, open-source YouTube clone built with modern Android development tools and best practices. It demonstrates the use of Jetpack Compose, Clean Architecture, and advanced networking/caching strategies.

## 🚀 Features

- **Video Feed**: Browse and watch videos with a seamless player experience.
- **Social Feed**: A community-driven feed supporting posts with text and images, likes, and comments.
- **Subscriptions**: Manage channel subscriptions with real-time updates.
- **Channel Profiles**: View detailed channel information, subscriber counts, and uploaded content.
- **Search**: Robust search functionality for finding videos and channels.
- **Authentication**: Secure Sign-in and Sign-up flow with JWT-based session management.
- **Dark Mode Support**: Fully integrated Material 3 theme with dark and light mode support.
- **Offline Caching**: Room-based caching for social posts to ensure a smooth offline experience.
- **Video Upload**: Support for capturing and uploading videos directly from the app.

## 🛠 Tech Stack

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) for a declarative UI.
- **Architecture**: Clean Architecture (Domain, Data, Presentation layers).
- **Dependency Injection**: [Hilt](https://dagger.dev/hilt/) for robust DI.
- **Networking**: [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/) for REST API communication.
- **Database**: [Room](https://developer.android.com/training/data-storage/room) for local persistence and offline support.
- **Pagination**: [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3) for efficient list loading and RemoteMediator.
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/) for optimized image and video thumbnail rendering.
- **Navigation**: [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation).
- **Session Management**: [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) for secure token storage.

## 🏗 Architecture

The project follows the **Clean Architecture** pattern to ensure scalability and testability:

1.  **Presentation Layer**: Contains Composable screens and ViewModels. Uses `StateFlow` and `SharedFlow` for reactive UI updates.
2.  **Domain Layer**: Contains business logic, entities, and repository interfaces. This layer is platform-independent.
3.  **Data Layer**: Contains repository implementations, API definitions, DTOs (Data Transfer Objects), and Room database logic.

## 🚦 Getting Started

### Prerequisites
- Android Studio Ladybug | 2024.2.1 or newer.
- JDK 17.
- Android SDK 35 (API level 35).

### Installation
1.  Clone the repository:
    ```bash
    git clone https://github.com/yourusername/youtubex.git
    ```
2.  Open the project in Android Studio.
3.  Add your `BASE_URL` to `gradle.properties` or `BuildConfig`.
4.  Sync Gradle and run the app on an emulator or physical device.

## 📸 Screenshots

| Home Feed | Video Player | Social Feed |
| :---: | :---: | :---: |
| ![Home](https://via.placeholder.com/200x400?text=Home+Feed) | ![Player](https://via.placeholder.com/200x400?text=Video+Player) | ![Social](https://via.placeholder.com/200x400?text=Social+Feed) |

---
Developed by [Mohd Umar](https://github.com/yourusername)

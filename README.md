# Melody Music Player

[![Download Melody App](https://img.shields.io/badge/Download-Melody%20APK-blueviolet?style=for-the-badge&logo=android)](https://github.com/heisenricher/Melody-Music-Player/releases/download/v1.0.0/melody-v1.0.0.apk)

Melody is a modern, beautiful, lightweight, privacy-focused, fully offline Android music player designed to handle very large local music libraries efficiently. Built native to Android using Jetpack Compose, Clean Architecture, and Material 3 design guidelines.

## 🚀 Key Features

* **Fully Offline & Privacy Focused**: No internet permission, no trackers, no ads, no registration. 
* **Material You Design**: Supports dark mode, light mode, and dynamic color theme generation derived from system wallpapers.
* **Large Library Scanning**: Incremental scanning through MediaStore and local audio file metadata extraction.
* **Premium Audio Engine**: Integrates Google Media3 ExoPlayer with Audio Focus handling, gapless playback, custom equalizers, bass boost, and speed settings.
* **Tag Editor**: Supports in-app tag modifications (Title, Artist, Album, Genre) and updates raw files using `JAudioTagger`.
* **Synced Lyrics**: Supports local `.lrc` text parsing and embedded lyrics (USLT frames) reading.
* **M3U Playlists**: Create, modify, delete playlists; import and export to standard M3U files.
* **Storage Control**: Folder browsing tree with folder pinning and folder-level exclusion options.

---

## 🛠 Tech Stack & Architecture

Melody is structured following **Clean Architecture** patterns split across 9 modular Gradle layers:

* `:app` — Launcher shell, NavGraph configuration, and Glance AppWidget.
* `:core` — Theme styling tokens, utility functions, and reusable UI components.
* `:domain` — Pure Kotlin models, repository interfaces, and use-case descriptors.
* `:data` — Room SQLite, content scanning, and Settings preference DataStore.
* `:player` — Media3 Service controls and Audio effects interfaces.
* `:feature_library` — Browse catalog lists, folders, player sheet, lyrics view, and tag updates.
* `:feature_playlists` — Playlists list and details.
* `:feature_search` — Instant search query filter.
* `:feature_settings` — Customize equalizers, theme preferences, and storage database backups.

---

## ⚙️ How to Build & Run

1. Clone this repository:
   ```bash
   git clone https://github.com/heisenricher/Melody-Music-Player.git
   ```
2. Open the project in Android Studio (Jellyfish or newer recommended).
3. Connect an Android device running API 29+ (Android 10) or newer.
4. Build and install the application.

---

## 📄 License

This project is open-source friendly and licensed under the MIT License.

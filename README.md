# Melody Music Player

[![Download Melody App](https://img.shields.io/badge/Download-Melody%20App-blueviolet?style=for-the-badge&logo=android)](https://github.com/heisenricher/Melody-Music-Player/releases/tag/v1.2.0)

Melody is a modern, beautiful, lightweight, privacy-focused, fully offline Android music player designed to handle very large local music libraries efficiently. Built native to Android using Jetpack Compose, Clean Architecture, and Material 3 design guidelines.

## 🚀 Key Features

* **Fully Offline & Privacy Focused**: No internet permission, no trackers, no ads, no registration. 
* **Material You Design & Customizable Color Scheme**: Supports dark mode, light mode, dynamic wallpaper-derived color themes, or choosing *any* custom primary color for the app.
* **Large Library Scanning & Most Played Section**: Incremental scanning through MediaStore and local audio file metadata extraction. Includes a dedicated **Most Played** tracks tab to easily access your favorite songs.
* **Notification Controls with 10s Seek**: Playback notification now features a click action to open the app directly, alongside convenient **10-second seek forward** and **10-second seek backward** buttons.
* **Premium Audio Engine**: Integrates Google Media3 ExoPlayer with Audio Focus handling, gapless playback, custom equalizers, bass boost, and speed settings.
* **Tag Editor**: Supports in-app tag modifications (Title, Artist, Album, Genre) and updates raw files using `JAudioTagger`.
* **Synced Lyrics**: Supports local `.lrc` text parsing and embedded lyrics (USLT frames) reading.
* **M3U Playlists**: Create, modify, delete playlists; import and export to standard M3U files.
* **Storage Control**: Folder browsing tree with folder pinning and folder-level exclusion options.
* **Ultra-Lightweight**: Highly optimized with R8 full mode minification and resource shrinking, resulting in a release APK of just **~5.0 MB** (down from 72MB in debug).

---

## 🛠 Tech Stack & Architecture

Melody is structured following **Clean Architecture** patterns split across 9 modular Gradle layers:

* `:app` — Launcher shell and NavGraph configuration.
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

<div align="center">

# 🚀 CGPA Calculator

### *A modern, lightweight Android utility for tracking academic excellence.*

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Language](https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![Build System](https://img.shields.io/badge/Gradle-Kotlin%20DSL-02303A?style=for-the-badge&logo=gradle&logoColor=white)](https://gradle.org/)

[Overview](#-overview) • [Preview](#-preview) • [Features & Tech Stack](#-core-architecture--tech-stack) • [Installation](#-quick-start) • [Contribute](#-contributing)

</div>

---

## ⚡ Overview

**CGPA Calculator** is a distraction-free, native Android application engineered to eliminate the friction of manual grade tracking. Designed specifically for university students, it streamlines academic tracking by providing instantaneous computation for both **Semester Grade Point Average (SGPA)** and **Cumulative Grade Point Average (CGPA)** with minimal memory overhead and zero latency.

---

## 📸 Preview

<p align="center">
<img width="200" height="100%" alt="Screenshot_20261016_214234" src="https://github.com/user-attachments/assets/9e6537c3-9965-443d-ab39-d9c9972162f0" />
<img width="200" height="100%" alt="Screenshot_20261016_214305" src="https://github.com/user-attachments/assets/27372033-4c41-418c-bfcc-f994c277345b" />
<img width="200" height="100%" alt="Screenshot_20261016_214317" src="https://github.com/user-attachments/assets/3a38090a-e7a0-4bb3-9074-0cf18d6969d4" />
</p>

---

## 🛠️ Core Architecture & Tech Stack

- **Core Language:** `100% Java` structured following clean separation of concerns.
- **Build Automation:** Gradle build system configured with modern `Kotlin DSL` (`build.gradle.kts`).
- **UI/UX Paradigm:** Native Android XML layouts optimized for responsiveness across various screen densities.
- **Minimum SDK:** Android 7.0 (API Level 24) and above.

---

## 📁 Repository Anatomy

```text
CGpa-Calculator/
├── 📂 app/                  # Application module source code
│   ├── 📂 src/              # Java source directories & XML UI assets
│   └── 📜 build.gradle.kts  # Module-level dependency management
├── 📂 gradle/               # Gradle wrapper binaries & properties
├── 📱 app-debug.apk         # Pre-compiled debug binary for instant testing
├── 📜 build.gradle.kts      # Project-wide configuration
├── ⚙️ gradle.properties     # JVM tuning & build performance flags
└── 📜 settings.gradle.kts   # Multi-module workspace resolution

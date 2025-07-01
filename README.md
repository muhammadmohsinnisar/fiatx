# FiatX - Currency Converter Android App

FiatX is a lightweight Android app for converting between fiat currencies using real-time exchange rates. It combines online API data with local caching to deliver fast and reliable currency conversions, even offline.

---

## Features

- Fetches a comprehensive list of fiat currencies from public REST APIs.
- Downloads and caches exchange rates for popular currencies, refreshed every 24 hours.
- Local persistence of currencies and rates using Room database for offline use and performance.
- Reactive UI updates powered by Kotlin StateFlow and MVVM architecture.
- Graceful error handling with fallback API and user-friendly messages.
- Simple, clean interface with base/target currency selectors and amount input.

---

## Architecture

- **MVVM**: Clean separation between UI, business logic, and data layers.
- **Repository Pattern**: Single source of truth managing data from remote and local sources.
- **Room Database**: Local persistence with compile-time checked SQL queries.
- **Retrofit + OkHttp**: Networking with coroutine support and logging.
- **Kotlin Coroutines & StateFlow**: Asynchronous operations and reactive UI state management.

---

## How It Works

1. On startup, the app checks local database for currency list; if empty, it fetches from the API and stores it locally.
2. Concurrently, exchange rates for a curated list of base currencies are fetched and cached with a 24-hour expiry policy.
3. When user requests a conversion, the app first attempts to retrieve the rate from the local database; if missing, it fetches from the network.
4. UI reflects conversions and error messages in real-time using reactive data streams.

---

## Setup & Build

- Requires **Android SDK 35**, **minSdk 24**, **Java 11**, Kotlin 2.0+
- APIs used:
    - Primary: `https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/`
    - Fallback: `https://latest.currency-api.pages.dev/v1/`
- Configuration: API base URLs are defined in `build.gradle` as `buildConfigField`s.

---

## Key Code Highlights

- `FiatCurrencyRepository` handles all data operations and caching logic.
- `CurrencyViewModel` exposes state flows for UI and manages coroutine scopes.
- `MainActivity` collects flows and updates UI components accordingly.
- Retrofit client uses a fallback mechanism to improve API resilience.

---

## Possible Improvements

- Graphical representation for the rates.
- Better UI/UX design.
- Support for more currencies or crypto assets.

---

## APK File

- A debug APK of the app is included for quick installation and testing without building the
  project.
  **Location:**
  ``
  cd app/apks/debug
  ``

**Installation:**

- Connect your Android device (with USB debugging enabled) or start an emulator, then run:
  ``
  adb install -r app/apks/debug/app-debug.apk
  ``

---

## License

This project is open source under the MIT License.

---

Thanks for checking out FiatX!


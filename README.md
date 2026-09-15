# Batocera Web Services

A minimal Android app that wraps a self-hosted web service in a native app shell. Enter the URL once, and the app remembers it — every subsequent launch goes straight to your service, no browser, no address bar, no distractions.

## Features

- 🔗 **Enter once, remembered forever** — the URL is saved on-device after the first launch
- 🎯 **Direct launch** — on every following open, the app jumps straight to your saved URL
- 🔄 **Easy reset** — clearing the app's storage (or reinstalling) brings back the setup screen so a new URL can be entered
- 🌐 **Full URL support** — works with `http://` or `https://`, any host, IP address, or port (defaults to `http://` if no scheme is typed)
- 📡 **Cleartext traffic allowed** — supports plain `http://` connections to local/internal servers, not just `https://`
- 🎨 **Custom branded UI** — setup screen styled with the app's own logo and color palette
- ⬅️ **Back button support** — navigates back within the web content before exiting the app

## How It Works

1. **First launch** — a setup screen appears asking for the full URL of your service (e.g. `http://192.168.1.10:1234`)
2. **Tap Connect** — the URL is saved locally and loaded immediately in a full-width WebView
3. **Every launch after that** — the app skips the setup screen entirely and loads the saved URL right away
4. **Starting over** — go to Android Settings → Apps → Batocera Web Services → Storage → Clear Data (or simply uninstall and reinstall the app) to be asked for a URL again

## Project Structure

```
WebApp/
├── app/
│   └── src/main/
│       ├── java/com/example/webapp/
│       │   └── MainActivity.java      # Core app logic
│       ├── res/
│       │   ├── layout/
│       │   │   └── activity_main.xml  # Setup screen + WebView layout
│       │   ├── drawable/              # Gradient background, buttons, logo
│       │   ├── mipmap-*/              # App icon in all densities
│       │   └── values/
│       │       ├── colors.xml
│       │       └── styles.xml
│       └── AndroidManifest.xml
├── .github/workflows/build.yml        # Automated APK build via GitHub Actions
├── gradlew / gradlew.bat              # Gradle wrapper (pinned to Gradle 8.4)
└── build.gradle
```

## Building the APK

### Option 1 — GitHub Actions (no local install required)

This repository includes a ready-to-use workflow at `.github/workflows/build.yml`. On every push, GitHub automatically builds a debug APK for you.

1. Push this project to a GitHub repository (make sure the `.github` folder is included — it's easy to accidentally skip it when drag-and-dropping, since folders starting with a dot are sometimes hidden)
2. Go to the **Actions** tab of your repository
3. Wait for the **Build APK** workflow to finish (2–4 minutes)
4. Open the completed run and download the **barocera-apk** artifact from the bottom of the page
5. Transfer the `.apk` to your Android device and install it (allow "install from unknown sources" if prompted)

### Option 2 — Android Studio

1. Install [Android Studio](https://developer.android.com/studio)
2. Open the `WebApp` folder as a project
3. Let Gradle sync
4. **Build → Build Bundle(s) / APK(s) → Build APK(s)**
5. Find the APK under `app/build/outputs/apk/debug/`

## Customization

| Want to change... | Edit this |
|---|---|
| App name | `android:label` in `AndroidManifest.xml` |
| App icon | Replace the images in `res/mipmap-*/ic_launcher.png` |
| Setup screen text/colors | `res/layout/activity_main.xml` and `res/values/colors.xml` |
| Package / applicationId | `app/build.gradle` |

## Requirements

- Minimum Android version: Android 5.0 (API 21)
- Internet permission is required and requested automatically
- Cleartext (`http://`) traffic is enabled by default to support local network servers

## License

This project is provided as-is for personal use.

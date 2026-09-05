# Barocera Web Services

**Your Batocera Web Services frontend, in one tap.**

A lightweight Android app that connects directly to your Batocera Web Services instance — no browser, no typing full addresses every time, just your emulation console at your fingertips.

[![Platform](https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white)](#)
[![Min SDK](https://img.shields.io/badge/min%20SDK-21%20(Android%205.0)-blue)](#)
[![License](https://img.shields.io/badge/license-personal%20use-lightgrey)](#)

---

## Download

Grab the latest APK from the **[Releases](../../releases)** page and install it on your Android device or tablet.

> **New here?** Enable installs from outside the Play Store first: **Settings → Apps → Special access → Install unknown apps**, and allow it for the app you used to download the APK (browser, file manager, etc.).

---

## Why Barocera Web Services

Batocera Web Services already gives you a browser-based front end for your retro gaming setup — but keeping a browser tab, bookmark, and address bar around every time gets old fast. **Barocera Web Services** turns that same web interface into a real app on your device:

- **One-time setup** — enter your Batocera's IP address once, the app remembers it
- **Instant launch** — every time after that, the app opens straight into your Batocera interface, no typing required
- **No wrong addresses** — the connection format (`http://` + port `1234`) is built in, you only ever type the IP
- **Built for local networks** — works out of the box with the plain `http://` connections Batocera Web Services uses on your home network
- **Switch machines anytime** — reset the app's storage (or reinstall) to connect to a different Batocera

## Getting Started

1. Install the APK (see **Download** above)
2. Open **Barocera Web Services**
3. On first launch, enter the IP address of your Batocera machine — for example `192.168.1.10`
4. Tap **Connect**
5. That's it — the app now opens directly into your Batocera Web Services every time

```
http://  [ 192.168.1.10 ]  :1234
```
*(You only fill in the middle — the rest is already set up for you.)*

## Connecting to a Different Batocera

Want to point the app at a different machine or IP?

- **Android Settings → Apps → Barocera Web Services → Storage → Clear storage**, or
- **Uninstall and reinstall the app**

Either way, you'll be asked for a new IP address the next time you open it.

## Requirements

- Android 5.0 (API 21) or newer
- Your Batocera machine and your Android device on the same local network
- Batocera Web Services running and reachable on port `1234`

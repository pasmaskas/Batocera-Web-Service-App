# Batocera Web Services

**Your Batocera Web Services frontend, in one tap.**

<img width="1220" height="1141" alt="Screenshot_20260905-134949_Photos~2" src="https://github.com/user-attachments/assets/76310ce7-4164-4f0d-bd98-47e8469fdf58" />

A lightweight Android app that connects directly to your Batocera Web Services instance — no browser, no typing full addresses every time, just your emulation console at your fingertips. Now with optional one-tap **Power On** for your Batocera machine.

[![Platform](https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white)](#)
[![Min SDK](https://img.shields.io/badge/min%20SDK-21%20(Android%205.0)-blue)](#)
[![Version](https://img.shields.io/badge/version-1.1-orange)](#)
[![License](https://img.shields.io/badge/license-personal%20use-lightgrey)](#)

---

## Download

Grab the latest APK from the **[Releases](../../releases)** page and install it on your Android device or tablet.

> **New here?** Enable installs from outside the Play Store first: **Settings → Apps → Special access → Install unknown apps**, and allow it for the app you used to download the APK (browser, file manager, etc.).

---

## Why Batocera Web Services

Batocera Web Services already gives you a browser-based front end for your retro gaming setup — but keeping a browser tab, bookmark, and address bar around every time gets old fast. **Batocera Web Services** turns that same web interface into a real app on your device:

- **One-time setup** — enter your Batocera's IP address once, the app remembers it
- **Instant launch** — every time after that, the app opens straight into your Batocera interface, no typing required
- **No wrong addresses** — the connection format (`http://` + port `1234`) is built in, you only ever type the IP
- **Built for local networks** — works out of the box with the plain `http://` connections Batocera Web Services uses on your home network
- **Smart connection check** — on every launch, the app quietly checks if your Batocera is reachable before deciding what to show you
- **Optional Power On (Wake-on-LAN)** — add your Batocera's MAC address once, and the app can turn the machine on for you if it's offline, then load the page automatically once it's ready
- **Fullscreen video support** — HTML5 videos in the web interface can be played fullscreen, just like in a browser
- **Switch machines anytime** — reset the app's storage (or reinstall) to connect to a different Batocera

## Getting Started

1. Install the APK (see **Download** above)
2. Open **Batocera Web Services**
3. On first launch, enter the IP address of your Batocera machine — for example `192.168.1.10`
4. *(Optional)* Enter the MAC address of your Batocera's network card to enable Power On
5. Tap **Connect**
6. That's it — the app now opens directly into your Batocera Web Services every time

```
http://  [ 192.168.1.10 ]  :1234
```
*(You only fill in the IP — the rest is already set up for you.)*

## Power On (Wake-on-LAN)

If you entered a MAC address during setup, the app can turn your Batocera on for you:

1. On launch, the app checks whether your Batocera is already reachable
2. **Already on?** → it loads the web interface immediately, nothing else to do
3. **Offline?** → a **⚡ Power On** button appears. Tap it, and the app sends a Wake-on-LAN signal and keeps checking in the background
4. As soon as the machine responds, the app automatically loads the web interface — no need to tap anything else

**Requirements for Power On to work:**
- Wake-on-LAN must be enabled in the machine's BIOS/UEFI **and** in its OS network settings
- Works most reliably over a **wired (Ethernet)** connection — most WiFi adapters don't support it
- Your phone/tablet must be on the **same local network** as the Batocera machine

Didn't enter a MAC address? No problem — the app just shows a **Check Again** button instead when it can't reach the machine.

## Connecting to a Different Batocera

Want to point the app at a different machine, IP, or MAC address?

- Tap **Change address** on the connection screen, or
- **Android Settings → Apps → Batocera Web Services → Storage → Clear storage**, or
- **Uninstall and reinstall the app**

Either way, you'll be asked for a new IP address (and optional MAC address) the next time you open it.

## Requirements

- Android 5.0 (API 21) or newer
- Your Batocera machine and your Android device on the same local network
- Batocera Web Services running and reachable on port `1234`
- For Power On: Wake-on-LAN enabled on the Batocera machine (see above)

## Changelog

**v1.1**
- Added optional MAC address field and Wake-on-LAN Power On button
- App now checks connection status automatically on every launch
- Added fullscreen support for HTML5 videos
- Status bar now shown with a black background instead of fully hidden

**v1.0**
- Initial release: fixed `http://<ip>:1234` connection, saved on first launch

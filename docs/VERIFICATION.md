# Verification

## Test scope

Host tests cover option mapping, random-selection bounds, settings validation, installer URL trust, release checksum verification, install/update/uninstall arguments, cancellation, and failure cleanup. Java line and branch coverage is enforced for the option resolver and settings validator. Python line and branch coverage is enforced for the standalone installer. Android rendering and DreamService lifecycle are verified separately from host coverage.

## Emulator evidence

| Platform | Device | Result |
| --- | --- | --- |
| Android TV emulator | `sdk_google_atv64_arm64`, Android 12/API 31, 1920x1080 | APK installed. Settings screen and preview activity opened; preview was allowed to animate for 45 seconds and both screenshots were visually inspected. The emulator did not expose a Dream settings picker, so system selection and idle activation were not verified. |
| Physical Fire TV | Not tested | We are looking for a Fire TV owner to test installation, system selection, idle activation, remote settings, and wake/resume. Please report device model, Fire OS/API, resolution, and results in the [issue tracker](https://github.com/jeremykenedy/pipeworks-dream/issues). |
| Physical Google TV | Not tested | We are looking for a Google TV owner to test system selection, idle activation, remote settings, and wake/resume. Please report device model, OS/API, resolution, and results in the [issue tracker](https://github.com/jeremykenedy/pipeworks-dream/issues). |

The emulator's saved screensaver component (`com.google.android.backdrop/.Backdrop`), enabled state (`1`), and screen-off timeout (`1800000`) were recorded before testing and remained unchanged afterward. Only the app was installed. No physical TV was used.

## Captures

The screenshots in `docs/screenshots/` are unmodified captures from the running app on the Android TV emulator. `pipeworks-dream-preview.png` shows the preview activity after the scene was visible and continuously animating. `pipeworks-dream-settings.png` shows the remote-friendly settings screen. These images establish Android app rendering only; they do not establish vendor-specific idle activation, sustained thermal behavior, or native 4K composition.

## Requirements

The APK targets Android 6.0 (API 23) or later. The device must expose Android DreamService screensavers for system selection. Availability and idle activation depend on device firmware and settings.

<p align="center">
  <a href="https://github.com/jeremykenedy/pipeworks-dream#gh-light-mode-only"><picture><source media="(max-width: 600px) and (prefers-reduced-motion: reduce)" srcset="art/banner-mobile-still-light.5e9aba6faf8b.svg"><source media="(prefers-reduced-motion: reduce)" srcset="art/banner-still-light.e1b7763dbc64.svg"><source media="(max-width: 600px)" srcset="art/banner-mobile-light.5d3b42044037.svg"><img src="art/banner-light.6daabe9d82b8.svg#gh-light-mode-only" alt="Pipeworks Dream" width="800"></picture></a>
  <a href="https://github.com/jeremykenedy/pipeworks-dream#gh-dark-mode-only"><picture><source media="(max-width: 600px) and (prefers-reduced-motion: reduce)" srcset="art/banner-mobile-still-dark.382a57e0c1c5.svg"><source media="(prefers-reduced-motion: reduce)" srcset="art/banner-still-dark.501eac98e464.svg"><source media="(max-width: 600px)" srcset="art/banner-mobile-dark.936d321953e0.svg"><img src="art/banner-dark.9c94af75a968.svg#gh-dark-mode-only" alt="Pipeworks Dream" width="800"></picture></a>
</p>

<p align="center">A continuously animated 3D pipe-growth screensaver for Fire TV, Android TV, and Google TV.</p>

<p align="center">
<a href="https://github.com/jeremykenedy/pipeworks-dream/releases"><img src="https://img.shields.io/github/downloads/jeremykenedy/pipeworks-dream/total" alt="Release downloads"></a>
<a href="https://github.com/jeremykenedy/pipeworks-dream/releases"><img src="https://img.shields.io/github/v/release/jeremykenedy/pipeworks-dream?label=latest%20release" alt="Latest release"></a>
<a href="https://github.com/jeremykenedy/pipeworks-dream/actions/workflows/ci.yml"><img src="https://github.com/jeremykenedy/pipeworks-dream/actions/workflows/ci.yml/badge.svg" alt="Build, tests, and coverage"></a>
<a href="https://github.com/jeremykenedy/pipeworks-dream/actions/workflows/style.yml"><img src="https://github.com/jeremykenedy/pipeworks-dream/actions/workflows/style.yml/badge.svg" alt="Code style"></a>
<a href="https://github.com/jeremykenedy/pipeworks-dream/actions/workflows/docs.yml"><img src="https://github.com/jeremykenedy/pipeworks-dream/actions/workflows/docs.yml/badge.svg" alt="Documentation checks"></a>
<a href="https://github.com/jeremykenedy/pipeworks-dream/actions/workflows/security.yml"><img src="https://github.com/jeremykenedy/pipeworks-dream/actions/workflows/security.yml/badge.svg" alt="Privacy and source checks"></a>
<a href="LICENSE"><img src="https://img.shields.io/badge/license-Apache--2.0-blue.svg" alt="Apache License 2.0"></a>
</p>

<p align="center">
<a href="https://github.com/jeremykenedy"><img src="https://img.shields.io/github/followers/jeremykenedy?label=Follow&style=social" alt="Follow jeremykenedy"></a>
<a href="https://github.com/jeremykenedy/pipeworks-dream"><img src="https://img.shields.io/badge/Star-this%20repo-yellow?logo=github&style=social" alt="Star this repository"></a>
<a href="https://github.com/jeremykenedy/pipeworks-dream/stargazers"><img src="https://img.shields.io/github/stars/jeremykenedy/pipeworks-dream?style=social" alt="Repository stars"></a>
<a href="https://github.com/sponsors/jeremykenedy"><img src="https://img.shields.io/badge/Sponsor-jeremykenedy-ea4aaa?logo=githubsponsors" alt="Sponsor jeremykenedy"></a>
</p>

## Table of Contents

- [Overview](#overview)
- [Privacy](#privacy)
- [Features](#features)
- [Device verification](#device-verification)
- [Requirements](#requirements)
- [Installation](#installation)
- [Fire TV Toolkit](#fire-tv-toolkit)
- [Configuration](#configuration)
- [Screenshots](#screenshots)
- [Building and testing](#building-and-testing)
- [Documentation](#documentation)
- [Release notes](#release-notes)
- [License](#license)

## Overview

Pipeworks Dream grows a changing network of polished tubes with rounded bends in a slowly moving perspective view. It renders locally with OpenGL ES 2.0 and needs no media downloads or service connection.

## Privacy

The app has no ads, analytics, tracking, telemetry, crash reporting, or runtime network access. It does not request the Android internet permission. The optional installer only contacts GitHub when you run it to fetch the latest APK and its checksum; it does not send device or usage information.

## Features

| Feature | What it does | Guide |
| --- | --- | --- |
| Growing 3D pipes | Empty startup, timed growth, rounded bends, depth and moving perspective | [Architecture](docs/ARCHITECTURE.md) |
| Shapes and finishes | Round, square or hexagonal tubes, three thicknesses and individual colors | [Configuration](docs/CONFIGURATION.md) |
| Following tails | Keep every trail or let old sections recede after 5, 15, 30 or 60 seconds | [Controls](docs/CONFIGURATION.md) |
| Remote settings | Choose density, growth, light and background, or randomize them | [Settings provider](docs/CONFIGURATION.md) |
| Offline operation | Local rendering without ads, analytics, tracking or internet permission | [Verification](docs/VERIFICATION.md) |
| Guided installer | Verified signed downloads, updates and removal | [Installation](docs/INSTALLATION.md) |

## Device verification

The table distinguishes emulator checks from physical-device validation. A working emulator preview does not establish vendor-specific idle activation or display performance.

| Platform | Device and OS | Result |
| --- | --- | --- |
| Google TV emulator | ARM64, Android API 34, 1920x1080 | Exact 1.1.1/code3 upgrade retained all 11 settings. Fresh Preview and two system-preview DreamService sessions start empty and grow; Preview pause/resume retains its scene. Automatic idle activation is unverified. |
| Fire TV, historical 1.1.0 | AFTDEC012E, Fire OS/Android API 30, 1920x1080 capture | Physical 3D preview, all three shapes, DreamService and Back checked. Dense 28-pipe samples measured 27.35 and 24.27 presents/second after 40 and 75 seconds. See the verification guide for exact artifact and limits. Native 4K and long-duration power/thermal behavior remain unverified. |
| Google TV | No physical device available | Looking for a Google TV owner to test system selection, idle activation, remote behavior, and wake/resume, then report the model, Android TV OS version, resolution, and results in [Issues](https://github.com/jeremykenedy/pipeworks-dream/issues). |

## Requirements

The APK targets Android 6.0 (API 23) or later and OpenGL ES 2.0. The system must expose Android DreamService screensavers for selection. Actual availability and idle activation depend on device firmware and settings.

## Installation

Connect the TV or emulator over ADB, then run:

```bash
python3 install.py --serial TV_IP:5555
```

The installer verifies the latest release APK against the published SHA-256 before installation. Open Pipeworks Dream from the launcher to configure it, then select it in the system screensaver or ambient-display settings. The installer does not change the selected saver, timeout, power, sleep, or update settings.

Use `python3 install.py --help` for options. A local APK can be installed with `--apk PATH`; removal requires an interactive confirmation or the explicit `--uninstall --yes --force` flags. See [installation](docs/INSTALLATION.md) for the complete flow and safety behavior.

## Fire TV Toolkit

[Fire TV Toolkit](https://github.com/jeremykenedy/fire-tv-toolkit) provides a guided terminal setup and verified screensaver install, update, and removal commands for Fire TV. Run:

```bash
git clone https://github.com/jeremykenedy/fire-tv-toolkit.git
cd fire-tv-toolkit
node setup.js

firetv-screensavers --install=pipeworks-dream
screensaver --set=pipeworks-dream
```

To update Pipeworks Dream, run `firetv-screensavers --install=pipeworks-dream` again; the Toolkit downloads the latest release and verifies its SHA-256. To remove it, run `firetv-screensavers --uninstall=pipeworks-dream --force`. Toolkit commands also manage Fire TV screensaver optimization and timeouts, the Alexa deep-sleep fix, its on-TV picker, optional AT4K launcher, and saved-setting reversion. Its optional `guard` restores selected screensaver, timeout, and Alexa settings after certain platform or app changes. Some protected system updater packages cannot be disabled, and an update may still proceed; the Toolkit documents those limits and provides `guard --check` to reconcile settings afterward. Changes are explained before confirmation. These Toolkit controls apply to supported Fire TV devices; they do not establish physical-device validation for this app or apply to Android TV and Google TV. See Toolkit [installation](https://github.com/jeremykenedy/fire-tv-toolkit#installation), [commands](https://github.com/jeremykenedy/fire-tv-toolkit/blob/main/docs/COMMANDS.md), [guard behavior](https://github.com/jeremykenedy/fire-tv-toolkit/blob/main/docs/GUARD.md), and [revert guidance](https://github.com/jeremykenedy/fire-tv-toolkit/blob/main/docs/COMMANDS.md#reverting-and-removing).

## Configuration

Open Pipeworks Dream from the TV launcher. Settings take effect when the saver starts and are stored locally. See [configuration](docs/CONFIGURATION.md) for the available choices and the settings-provider contract.

| Setting | Choices | Default |
| --- | --- | --- |
| Pipe count | 4, 8, 12, 20, 28, Random | 8 |
| Growth speed | Slow, Natural, Quick, Random | Natural |
| Pipe color | Copper, Cool steel, Neon mix, Monochrome, Red, Green, Blue, Purple, Gold, Random | Copper |
| Background | Midnight, Deep ocean, Graphite, Random | Midnight |
| Lighting | Subtle, Cinematic, Bright, Random | Cinematic |
| Pipe glow | On, Off, Random | On |
| Tube shape | Round, Square, Hexagonal, Random | Round |
| Pipe thickness | Thin, Medium, Thick, Random | Medium |
| Tail behavior | Keep the full trail, Follow the growing end, Random | Keep |
| Tail delay | 5, 15, 30 or 60 seconds, Random | 15 seconds |
| Randomize all | On or Off | Off |

## Screenshots

This frame from the exact 1.1.1/code3 native recording shows pipes growing after an empty start on the Google TV API34 emulator. The [complete gallery](docs/SCREENSHOTS.md) shows the startup sequence, system-preview DreamService sessions, and separately labeled historical physical captures.

<img src="art/screenshots/pipeworks111-preview-grown-google-tv-api34.png" alt="Pipeworks 1.1.1 copper pipes grown from an empty scene on the Google TV API34 emulator" width="800">

## Building and testing

Build requirements are JDK 17 or later, Android SDK platform 36, build-tools 36.0.0, Python 3, and ADB for emulator/device checks. Run `bash build.sh` to create a locally signed APK. Run the following checks before contributing:

```bash
bash test.sh
bash scripts/test-coverage.sh
bash scripts/test-python-coverage.sh
bash scripts/check-style.sh
python3 scripts/check-docs.py
python3 scripts/check-privacy.py
```

CI enforces 100% line and branch coverage for the app's pure Java settings, pipe-growth and mesh logic, and for the standalone installer. Android framework lifecycle code is verified through emulator checks and APK manifest inspection. See [building](docs/BUILDING.md), [testing](docs/TESTING.md), and [CI](docs/CI.md).

## Documentation

- [Architecture](docs/ARCHITECTURE.md)
- [Building](docs/BUILDING.md)
- [CI](docs/CI.md)
- [Configuration and provider](docs/CONFIGURATION.md)
- [Installation and uninstall](docs/INSTALLATION.md)
- [Host integration contract](docs/INTEGRATION.md)
- [Release process](docs/RELEASING.md)
- [Testing](docs/TESTING.md)
- [Troubleshooting](docs/TROUBLESHOOTING.md)
- [Device verification](docs/VERIFICATION.md)
- [Screenshot gallery](docs/SCREENSHOTS.md)
- [Security policy](SECURITY.md)

Show some love by starring this repository on GitHub. The Star button links to the repository; GitHub requires each visitor to click it while signed in.

## Release notes

- [Pipeworks Dream 1.1.1](docs/releases/v1.1.1.md)
- [Pipeworks Dream 1.1.0](docs/releases/v1.1.0.md)
- [Pipeworks Dream 1.0.0](docs/releases/v1.0.0.md)

## License

Pipeworks Dream is licensed under the [Apache License, Version 2.0](LICENSE). See [NOTICE](NOTICE) for project notices.

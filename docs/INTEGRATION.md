# Host integration

Pipeworks Dream preserves its package, production signing certificate and settings-provider keys across the 1.1 update. Hosts should verify the published signed APK before installation and preserve the user's selected DreamService and timers.

| Field | Value |
| --- | --- |
| Toolkit catalog ID | `pipeworks-dream` |
| Repository | `jeremykenedy/pipeworks-dream` |
| Package | `com.jeremykenedy.pipeworksdream` |
| DreamService | `com.jeremykenedy.pipeworksdream/.PipeworksDreamService` |
| Settings | `com.jeremykenedy.pipeworksdream/.SettingsActivity` |
| Preview | `com.jeremykenedy.pipeworksdream/.PreviewActivity` |
| Version | `1.1.0`, code 2 |
| Android requirements | API 23 or later, OpenGL ES 2.0 |
| Production signer SHA-256 | `d496ee9655c19cd60169675f806718bc3094bf8dd4c20a7cf8a74cabb93d0c86` |
| APK SHA-256 | `a9642a20efe37fd3b39974d04f59b17eb4940935dbaca60c4b6cbf655eef03d0` |
| APK bytes | 62,276 |
| License | Apache 2.0; bundled NOTICE and full LICENSE |

The intended release assets are [pipeworks-dream.apk](https://github.com/jeremykenedy/pipeworks-dream/releases/download/v1.1.0/pipeworks-dream.apk) and [its SHA-256 file](https://github.com/jeremykenedy/pipeworks-dream/releases/download/v1.1.0/pipeworks-dream.apk.sha256). They become available when 1.1.0 is published; this source candidate is not yet a released artifact. The build is frozen while final credits-route checks finish.

The existing Toolkit registry entry resolves this repository's latest release and verifies its checksum. Install or update with `firetv-screensavers --install=pipeworks-dream`, select separately with `screensaver --set=pipeworks-dream`, and remove with `firetv-screensavers --uninstall=pipeworks-dream --force`. Do not add a second entry or silently select the app during installation.

The exported provider schema is `content://com.jeremykenedy.pipeworksdream.settings/schema`; current values are at `content://com.jeremykenedy.pipeworksdream.settings/settings`. The [configuration guide](CONFIGURATION.md) defines all 11 keys, allowed values, defaults and update semantics. Hosts must reject unsupported values and preserve unknown values when reading future app versions.

The [native gallery](SCREENSHOTS.md) provides unmodified physical renderer captures and provenance. A host may downscale one for its thumbnail without retouching the scene. Fire TV UI names bundled previews from the normalized fully-qualified Dream component's SHA-256; use its existing helper and index rather than inventing another filename rule. The small APK preview remains unchanged from the verified renderer baseline.

Before publishing a host update, test verified download, package/signer/version, same-package update, native discovery, thumbnail loading, app settings, actual Dream binding and Back. Installation, selection, app settings and automatic idle activation are separate checks. Restore all settings used in a test. Source coverage and checksum checks do not establish physical playback or automatic idle startup.

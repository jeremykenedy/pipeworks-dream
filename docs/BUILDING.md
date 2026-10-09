# Building

## Requirements

- JDK 17 or later.
- Android SDK platform 36 and build-tools 36.0.0.
- `aapt2`, `javac`, `d8`, `zipalign`, `apksigner`, `keytool`, and OpenSSL.

Set `ANDROID_HOME` if the SDK is not at `~/Library/Android/sdk`, then run:

```bash
bash build.sh
```

The APK and SHA-256 file are written to `build/`. The first local build creates a signing key and password under `~/.android/`. Back up both and retain the same key for future updates. Never commit them. Set `PIPEWORKS_KEYSTORE` and `PIPEWORKS_KEYPASS` to use an existing protected key. CI builds use temporary keys and are not production releases.

Use `VERSION_NAME` and `VERSION_CODE` to set release metadata. Validate the signed APK and checksum together before publishing. See [release process](RELEASING.md).

Install a local development build with `python3 install.py --serial TV_IP:5555 --apk build/pipeworks-dream.apk`. Without `--apk`, the installer downloads the latest published release and checks its SHA-256.

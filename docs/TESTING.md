# Testing

Run the host tests and coverage gates:

```bash
bash test.sh
bash scripts/test-coverage.sh
bash scripts/test-python-coverage.sh
bash scripts/check-style.sh
python3 scripts/check-docs.py
python3 scripts/check-privacy.py
```

The Java gate measures project-owned settings resolution and validation logic; the Python gate measures installer code. Android framework rendering and DreamService lifecycle are not included in host line coverage. Test the actual APK on TV emulators and hardware when available.

On a device, verify screensaver discovery, activation, visible animation, remote focus and selection in settings, preview, Back exit, and unchanged device screensaver settings after app removal. Installer checks cover release URL allowlists, checksum mismatch, interrupted downloads, install/update flags, serial selection, confirmation, and uninstall guardrails.

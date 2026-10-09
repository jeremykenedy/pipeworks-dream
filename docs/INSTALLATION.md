# Installation

## Install or update from the latest release

Enable ADB debugging on the TV, connect it on the same network, and run:

```bash
adb connect TV_IP:5555
python3 install.py --serial TV_IP:5555
```

The guided installer downloads the latest release APK and matching SHA-256 file from this repository, verifies the checksum, and installs or updates Pipeworks Dream. It contacts GitHub only after the command is run. It does not collect or send device or usage data. The app itself has no network permission.

## Install through Fire TV Toolkit

On Fire TV, [Fire TV Toolkit](https://github.com/jeremykenedy/fire-tv-toolkit) can install, update, activate, and remove listed screensavers. After Pipeworks Dream appears in its catalog, set up the Toolkit and install the saver:

```bash
git clone https://github.com/jeremykenedy/fire-tv-toolkit.git
cd fire-tv-toolkit
node setup.js

firetv-screensavers --install=pipeworks
screensaver --set=pipeworks
```

Run `firetv-screensavers --install=pipeworks` again to fetch and install the latest release. Remove only Pipeworks Dream with `firetv-screensavers --uninstall=pipeworks --force`; the force flag is required for removal. Toolkit also manages Fire TV screensaver optimization, timeouts, the Alexa deep-sleep fix, the on-TV screensaver picker, the optional AT4K launcher, and a guided restore of saved device settings. Its optional Guard restores selected settings after certain platform or app changes, but Fire OS can still install protected updates; see the Toolkit's [Guard limits](https://github.com/jeremykenedy/fire-tv-toolkit/blob/main/docs/GUARD.md). Its settings controls apply to supported Fire TV models only. They do not establish app activation support on Fire TV, Android TV, or Google TV hardware. Review each proposed device change before confirming. See the Toolkit [commands](https://github.com/jeremykenedy/fire-tv-toolkit/blob/main/docs/COMMANDS.md) for options and warnings.

## Install a local build

```bash
bash build.sh
python3 install.py --serial TV_IP:5555 --apk build/pipeworks-dream.apk
```

After installation, select Pipeworks Dream in the TV's screensaver or ambient-mode settings. Menu names vary by device. The installer does not change the selected screensaver, power settings, or system update behavior.

## Remove

```bash
python3 install.py --serial TV_IP:5555 --uninstall
```

For unattended scripts use `--yes`; unattended removal also requires `--force`. Removing the app deletes its local settings. The installer does not change unrelated apps or device preferences.

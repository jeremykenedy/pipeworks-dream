# Troubleshooting

## Pipeworks Dream is not listed

Confirm installation with `adb -s TV_IP:5555 shell pm list packages | grep pipeworksdream`. Then reopen the device screensaver settings. Some TV vendors hide Android's DreamService picker or use a separate ambient-mode menu.

## The animation does not start

Use the app launcher to open Pipeworks Dream and select Preview animation. If preview works, check the device's selected screensaver and idle timeout. The installer does not change either setting.

## ADB cannot connect

Enable developer options and network debugging on the TV, verify that the TV and computer share a network, and connect using the TV's current IP address. Specify `--serial` when more than one device is connected.

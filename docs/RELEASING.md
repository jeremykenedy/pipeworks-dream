# Releasing

Update the version in the release notes and build metadata. Run the complete local test and validation suite, build with the preserved release signing key, inspect the APK manifest and permissions, verify the APK signature, and compute the SHA-256 from that exact APK. Commit and push the release candidate, wait for all required Actions workflows, and make the final read-only source and artifact review before tagging.

Create a SemVer tag and GitHub release with the signed `pipeworks-dream.apk`, matching `pipeworks-dream.apk.sha256`, and the matching `docs/releases/vVERSION.md`. Download both published assets and verify the checksum. Do not replace assets under an existing tag; release any correction under a new patch version.

## 1.1.1 verification record

The [1.1.1 upgrade guide](releases/v1.1.1.md) covers the empty-start correction.
The signed code3 APK passed fresh Preview, two successive system-preview
DreamService sessions, persistent/following tails, ordinary Preview pause/resume,
and preserved-settings checks on the Google TV API34 emulator. Current native
frames and exact artifact hashes are in the [gallery](SCREENSHOTS.md).
DreamService preview-mode checks do not establish automatic idle activation.

Publish only after essential CI passes at the final commit, the authorized PR
merges, and the checked-out release commit matches remote main. Tag that exact
primary commit as v1.1.1 and publish the native-tested signed APK and checksum;
do not substitute a newly signed CI artifact for the verified bytes.

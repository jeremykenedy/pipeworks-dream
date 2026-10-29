# Releasing

Update the version in the release notes and build metadata. Run the complete local test and validation suite, build with the preserved release signing key, inspect the APK manifest and permissions, verify the APK signature, and compute the SHA-256 from that exact APK. Commit and push the release candidate, wait for all required Actions workflows, and make the final read-only source and artifact review before tagging.

Create a SemVer tag and GitHub release with the signed `pipeworks-dream.apk`, matching `pipeworks-dream.apk.sha256`, and the matching `docs/releases/vVERSION.md`. Download both published assets and verify the checksum. Do not replace assets under an existing tag; release any correction under a new patch version.

## 1.1.1 release candidate

The [1.1.1 upgrade guide](releases/v1.1.1.md) covers the empty-start correction.
Before publication, verify a fresh Preview and a new Dream session begin empty
and grow normally, preserve ordinary Preview pause/resume, exercise persistent
and following tails, and confirm settings are retained. Capture the final native
startup sequence and update maintained screenshots from that build. This candidate
remains unpublished until those native checks and essential CI pass.

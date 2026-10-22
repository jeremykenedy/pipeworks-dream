# Releasing

Update the version in the release notes and build metadata. Run the complete local test and validation suite, build with the preserved release signing key, inspect the APK manifest and permissions, verify the APK signature, and compute the SHA-256 from that exact APK. Commit and push the release candidate, wait for all required Actions workflows, and make the final read-only source and artifact review before tagging.

Create a SemVer tag and GitHub release with the signed `pipeworks-dream.apk`, matching `pipeworks-dream.apk.sha256`, and the matching `docs/releases/vVERSION.md`. Download both published assets and verify the checksum. Do not replace assets under an existing tag; release any correction under a new patch version.

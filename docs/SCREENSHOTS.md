# Native screenshot gallery

The first three rows are unmodified 1920x1080 physical Fire TV AFTDEC012E/API30 captures from October 22, 2026. The first row uses the exact final signed credits APK a964; the next two rows exercise the unchanged renderer baseline. That renderer baseline APK is `bbe734c33c32409a52bb2179ecd1d3f712395477b31b3906d02582131ba2e6a1`. Its unchanged rendering classes and resources are recorded in the verification guide.

<table>
<tr>
<td><img src="../art/screenshots/pipeworks-final-preview-fire-tv.png" alt="Exact final signed Pipeworks 1.1 APK running on physical Fire TV"><br>Preview</td>
<td><img src="../art/screenshots/pipeworks-final-credits-fire-tv.png" alt="Final offline Jeremy Kenedy project credits and Apache license on physical Fire TV"><br>Credits</td>
<td><img src="../art/screenshots/pipeworks-final-actual-dream-fire-tv.png" alt="Exact final Pipeworks APK bound as the actual physical DreamService"><br>Dream</td>
</tr>
<tr>
<td><img src="../art/screenshots/pipeworks-fire-tv-sparse-round.png" alt="Blue round tubes with glossy curved elbows"><br>Round</td>
<td><img src="../art/screenshots/pipeworks-fire-tv-sparse-square.png" alt="Purple square ducts with flat faces and rounded paths"><br>Square</td>
<td><img src="../art/screenshots/pipeworks-fire-tv-sparse-hexagonal.png" alt="Gold hexagonal pipes with visible facets"><br>Hex</td>
</tr>
<tr>
<td><img src="../art/screenshots/pipeworks-fire-tv-dense-40s.png" alt="Dense metallic network after 40 seconds of growth"><br>Dense at 40 seconds</td>
<td><img src="../art/screenshots/pipeworks-fire-tv-dense-75s.png" alt="Later dense network with growing elbows and perspective depth"><br>Dense at 75 seconds</td>
<td><img src="../art/screenshots/pipeworks-fire-tv-actual-dream.png" alt="Gold hexagonal pipes in the actual bound DreamService"><br>Dream</td>
</tr>
<tr>
<td><img src="../art/screenshots/pipeworks-final-settings-google-tv-api34.png" alt="Final credits APK settings with focused Preview on API34 Google TV emulator"><br>Settings</td>
<td><img src="../art/screenshots/pipeworks-final-credits-google-tv-api34.png" alt="Offline project copyright and Apache license in final Pipeworks APK"><br>Credits</td>
<td><img src="../art/screenshots/pipeworks-final-actual-dream-google-tv-api34.png" alt="Actual bound final Pipeworks DreamService on API34 emulator"><br>Dream</td>
</tr>
</table>

The last row shows the exact credits APK `a9642a20efe37fd3b39974d04f59b17eb4940935dbaca60c4b6cbf655eef03d0` on the API34 ARM64 Google TV emulator. Remote Preview/Back, Credits scrolling, bound DreamService/Back and exact 11-preference/five-system-value restoration passed. These short route captures do not replace the physical renderer measurements.

[Capture provenance](../art/screenshots/physical-provenance.json) records artifact and image hashes. [Device verification](VERIFICATION.md) separates short physical measurements, emulator controls and unverified idle/thermal behavior.

[Final physical credits/update provenance](../art/screenshots/final-physical-provenance.json) records installed-byte verification, remote routes and complete restoration. Moving review remains pending before release.

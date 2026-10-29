# Native screenshot gallery

## Pipeworks 1.1.1 startup

These unaltered frames come from native 1920x1080 recordings of the signed
1.1.1/code3 APK on the Google TV ARM64 API34 emulator on October 29, 2026.
The dark first frames are the deliberately empty rendered background, followed
by the first short segments and then a developed pipe network.

The second and third rows show two successive actual bound DreamService sessions
started through the system's preview mode (`isPreview=true`, `mBound=true`).
They verify fresh service startup and growth with persistent and following tails.
They do not establish normal or automatic idle activation. Back detached each
session; ordinary Preview Home/resume retained its existing activity and scene.

<table>
<tr>
<td><img src="../art/screenshots/pipeworks111-preview-empty-google-tv-api34.png" alt="Pipeworks 1.1.1 fresh preview: empty scene"><br>Fresh Preview: Empty scene</td>
<td><img src="../art/screenshots/pipeworks111-preview-first-segments-google-tv-api34.png" alt="Pipeworks 1.1.1 fresh preview: first segments"><br>Fresh Preview: First segments</td>
<td><img src="../art/screenshots/pipeworks111-preview-grown-google-tv-api34.png" alt="Pipeworks 1.1.1 fresh preview: growing pipes"><br>Fresh Preview: Growing pipes</td>
</tr>
<tr>
<td><img src="../art/screenshots/pipeworks111-dream-empty-google-tv-api34.png" alt="Pipeworks 1.1.1 first dreamservice session: empty scene"><br>First DreamService session: Empty scene</td>
<td><img src="../art/screenshots/pipeworks111-dream-first-segments-google-tv-api34.png" alt="Pipeworks 1.1.1 first dreamservice session: first segments"><br>First DreamService session: First segments</td>
<td><img src="../art/screenshots/pipeworks111-dream-grown-google-tv-api34.png" alt="Pipeworks 1.1.1 first dreamservice session: growing pipes"><br>First DreamService session: Growing pipes</td>
</tr>
<tr>
<td><img src="../art/screenshots/pipeworks111-following-dream-empty-google-tv-api34.png" alt="Pipeworks 1.1.1 second dreamservice session, following tails: empty scene"><br>Second DreamService session, following tails: Empty scene</td>
<td><img src="../art/screenshots/pipeworks111-following-dream-first-segments-google-tv-api34.png" alt="Pipeworks 1.1.1 second dreamservice session, following tails: first segments"><br>Second DreamService session, following tails: First segments</td>
<td><img src="../art/screenshots/pipeworks111-following-dream-grown-google-tv-api34.png" alt="Pipeworks 1.1.1 second dreamservice session, following tails: growing pipes"><br>Second DreamService session, following tails: Growing pipes</td>
</tr>
</table>

[1.1.1 capture provenance](../art/screenshots/startup111-native-provenance.json)
records the tested APK, production certificate, recording and image hashes, and
verification limits. All 11 app options, 14 UI preferences, and 19 sampled system
values were restored exactly. The following captures document the earlier
1.1.0 release and are not physical validation of 1.1.1.

## Historical 1.1.0 captures

The first three historical rows are unmodified 1920x1080 physical Fire TV AFTDEC012E/API30 captures from October 22, 2026. The first row uses the exact final signed credits APK a964; the next two rows exercise the unchanged renderer baseline. That renderer baseline APK is `bbe734c33c32409a52bb2179ecd1d3f712395477b31b3906d02582131ba2e6a1`. Its unchanged rendering classes and resources are recorded in the verification guide.

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

[Final physical credits/update provenance](../art/screenshots/final-physical-provenance.json) records installed-byte verification, remote routes and complete restoration. Independent review of the exact final physical movie accepted its continuous motion. The public gallery was verified at 1280 and 320-pixel browser widths.

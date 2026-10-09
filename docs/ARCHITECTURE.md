# Architecture

Pipeworks Dream is a native Android app with a `DreamService`, a remote-friendly settings activity, and a small exported settings provider. The scene is drawn by a hardware-accelerated `View` with Android Canvas; the app bundles no media assets or runtime libraries.

`PipeEngine` grows independent axis-aligned paths through a bounded three-dimensional grid. It prevents overlapping occupied cells, turns when a path reaches a collision or edge, and restarts a path after its trail reaches the configured limit. `PipeworksSceneView` projects those coordinates onto the screen and draws a dark grid, metal-like layered strokes, elbow highlights, and an optional soft bloom. Animation and scheduled invalidation stop with the DreamService lifecycle.

The settings resolver applies local preferences once when a scene is created. Pipe count, speed, palette, background, lighting, and glow can each be randomized; a separate option randomizes every control at saver start. `SettingsProvider` exports only the app's setting schema and current values. No app component makes network requests.

The optional standalone Python installer makes a direct user-initiated GitHub request to download the latest APK and checksum. That installer is separate from app runtime.

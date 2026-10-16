# Configuration

Open Pipeworks Dream from the Android TV launcher and use the remote to set each option. Values are saved locally and applied the next time the DreamService starts. Each choice can be set to Random. The global randomize option chooses a new value for each setting every time the saver starts.

| Setting key | Choices | Default | Effect |
| --- | --- | --- | --- |
| `density` | `few`, `handful`, `many`, `ton`, `schools`, `random` | `handful` | Controls the number of independent growing pipe runs. |
| `speed` | `slow`, `natural`, `quick`, `random` | `natural` | Sets the interval between growth steps. |
| `palette` | `copper`, `cool`, `neon`, `monochrome`, `red`, `green`, `blue`, `purple`, `gold`, `random` | `copper` | Changes pipe metal color and highlight. |
| `background` | `midnight`, `ocean`, `graphite`, `random` | `midnight` | Changes the dark backdrop and ambient tint. |
| `lighting` | `subtle`, `cinematic`, `bright`, `random` | `cinematic` | Adjusts pipe reflections and scene brightness. |
| `glow` | `on`, `off`, `random` | `on` | Enables or disables the colored rim-light accent. |
| `shape` | `round`, `square`, `hexagonal`, `random` | `round` | Round tubes, flat-sided square ducts or hexagonal tubes. |
| `size` | `thin`, `medium`, `thick`, `random` | `medium` | Changes the cross-section radius to 0.10, 0.17 or 0.24 grid units. |
| `tail` | `keep`, `follow`, `random` | `keep` | Retain full pipe trails or let the oldest end follow the growing head. |
| `tail_delay` | `5`, `15`, `30`, `60`, `random` | `15` | Following-tail delay in seconds; ignored in keep mode. |
| `randomize_all` | `true`, `false` | `false` | Selects all scene settings randomly at saver start. |

In following mode each pipe grows for the selected delay before its oldest end starts moving. Its trail length is derived from growth speed so a faster pipe retains more segments for the same time delay. Both head and tail move continuously between grid steps. Delay is rounded up to the next growth step; collisions can cause a pipe to restart earlier. Full-trail mode keeps the existing bounded trail and restarts after 84 points or a blocked head.

## Settings provider

The app exports a local provider at `com.jeremykenedy.pipeworksdream.settings` for host-app discovery and editing:

- `content://com.jeremykenedy.pipeworksdream.settings/schema` returns `key`, `title`, `type`, `default`, `choices`, and `randomAllowed` columns.
- `content://com.jeremykenedy.pipeworksdream.settings/settings` returns the current `key` and `value` pairs.
- Update one value by calling `ContentResolver.update()` on the settings URI with `key` and `value` text fields. The provider rejects unknown keys and values.

The provider is part of the app and does not make network requests. Hosts should discover the schema at runtime rather than duplicating option lists. Fire TV UI opens this app's remote-friendly settings screen from the screensaver picker. The app owns and persists these settings.

# Configuration

Open Pipeworks Dream from the Android TV launcher and use the remote to set each option. Values are saved locally and applied the next time the DreamService starts. Each choice can be set to Random. The global randomize option chooses a new value for each setting every time the saver starts.

| Setting key | Choices | Default | Effect |
| --- | --- | --- | --- |
| `density` | `few`, `handful`, `many`, `ton`, `schools`, `random` | `handful` | Controls the number of independent growing pipe runs. |
| `speed` | `slow`, `natural`, `quick`, `random` | `natural` | Sets the interval between growth steps. |
| `palette` | `copper`, `cool`, `neon`, `monochrome`, `random` | `copper` | Changes pipe metal color and highlight. |
| `background` | `midnight`, `ocean`, `graphite`, `random` | `midnight` | Changes the dark backdrop and ambient tint. |
| `lighting` | `subtle`, `cinematic`, `bright`, `random` | `cinematic` | Adjusts pipe reflections and scene brightness. |
| `glow` | `on`, `off`, `random` | `on` | Enables or disables the soft pipe bloom. |
| `randomize_all` | `true`, `false` | `false` | Selects all scene settings randomly at saver start. |

## Settings provider

The app exports a local provider at `com.jeremykenedy.pipeworksdream.settings` for host-app discovery and editing:

- `content://com.jeremykenedy.pipeworksdream.settings/schema` returns `key`, `title`, `type`, `default`, `choices`, and `randomAllowed` columns.
- `content://com.jeremykenedy.pipeworksdream.settings/settings` returns the current `key` and `value` pairs.
- Update one value by calling `ContentResolver.update()` on the settings URI with `key` and `value` text fields. The provider rejects unknown keys and values.

The provider is part of the app and does not make network requests. Hosts should discover the schema at runtime rather than duplicating option lists. The Fire TV UI currently selects installed DreamServices; it does not yet provide a generic editor for settings from other apps.

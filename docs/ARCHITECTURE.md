# Architecture

Pipeworks Dream uses a native Android DreamService and OpenGL ES 2.0. The scene has no media assets or runtime dependencies.

`PipeEngine` grows independent axis-aligned paths through a bounded three-dimensional grid. Occupied cells cannot overlap. Paths turn at collisions and restart after a bounded trail length.

`PipeMesh` turns each path into a closed cylindrical surface with rounded quadratic bends, smoothly transported normals, and independent palette colors. Each vertex includes the final-step growth coordinate. The fragment shader reveals the newest segment continuously between engine ticks. The network is pre-grown at startup, so the first visible frame contains pipes.

`PipeworksSceneView` stores the triangle mesh in a GPU vertex buffer, uploading only when the engine advances. A single background worker prepares the next mesh before its update is due, so mesh construction does not block rendering. Pause closes that worker; generation checks discard work from a stopped or replaced surface. Perspective projection, a bounded slow camera orbit, depth testing, directional light, specular reflections, rim light, and subtle depth fog give the network its shape. The old flat grid and layered Canvas strokes are removed. The render loop requests frames at approximately 30 fps, stops when paused, and preserves its GL context when supported. A lost GL context rebuilds resources and the scene.

Preferences are resolved locally when a scene is created. Settings and provider keys remain compatible with 1.0.0. No component makes network requests. The optional Python installer contacts GitHub only when explicitly run by the user.

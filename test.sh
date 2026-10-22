#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
OUT="$ROOT/build/tests"
mkdir -p "$OUT"
javac -source 8 -target 8 -d "$OUT" \
  "$ROOT/src/com/jeremykenedy/pipeworksdream/PipeworksOptions.java" \
  "$ROOT/src/com/jeremykenedy/pipeworksdream/PipeEngine.java" \
  "$ROOT/src/com/jeremykenedy/pipeworksdream/PipeMesh.java" \
  "$ROOT/src/com/jeremykenedy/pipeworksdream/SettingsValues.java" \
  "$ROOT/tests/PipeworksOptionsTest.java" \
  "$ROOT/tests/PipeMeshTest.java"
java -ea -cp "$OUT" com.jeremykenedy.pipeworksdream.PipeworksOptionsTest
java -ea -cp "$OUT" com.jeremykenedy.pipeworksdream.PipeMeshTest
python3 -m unittest -v tests.test_installer

#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p build
if [ ! -f build/json.jar ]; then
    curl -fLsS \
        https://repo.maven.apache.org/maven2/org/json/json/20260814/json-20260814.jar \
        -o build/json.jar.tmp
    mv build/json.jar.tmp build/json.jar
fi
kotlinc src/*.kt tests/*.kt -cp build/json.jar -include-runtime -d build/app.jar
if [ "${1:-}" = "--check" ]; then
    java -cp "build/app.jar:build/json.jar" TestsKt
else
    java -cp "build/app.jar:build/json.jar" MainKt "$@"
fi

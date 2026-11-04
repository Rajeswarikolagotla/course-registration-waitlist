#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p out
javac -d out src/main/java/com/example/waitlist/CourseService.java Demo.java
java -cp out Demo

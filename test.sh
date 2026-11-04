#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p out
javac -d out src/main/java/com/example/waitlist/*.java src/test/java/com/example/waitlist/*.java
java -cp out com.example.waitlist.CourseServiceTest

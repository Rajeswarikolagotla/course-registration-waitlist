#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p out
javac -d out src/main/java/com/example/waitlist/*.java
java -cp out com.example.waitlist.Server "${1:-8080}"

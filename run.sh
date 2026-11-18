#!/bin/sh
# Compile and run GameHub (needs JDK 17+). Open http://localhost:8080
set -e
mkdir -p bin
javac -d bin $(find src -name '*.java')
exec java -cp bin gamehub.Main

#!/usr/bin/env bash
# Connect X - Compilation and Launch Script (Linux / macOS)

set -e
mkdir -p bin
echo "Compiling Connect X Java files..."
javac -d bin -sourcepath src src/com/connectx/Main.java

echo "Launching Connect X..."
java -cp bin com.connectx.Main

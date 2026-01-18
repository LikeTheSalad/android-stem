#!/bin/sh -e
./gradlew check
./gradlew -p "demo-app" testDebugUnitTest

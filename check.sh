#!/bin/sh -e
./gradlew check
./gradlew -p "sample-app" testDebugUnitTest

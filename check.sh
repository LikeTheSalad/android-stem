#!/bin/sh -e
./gradlew check
./gradlew -p "stem-test" testDebugUnitTest

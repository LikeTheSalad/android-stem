#!/bin/sh
./gradlew check
./gradlew -p "stem-test" testDebugUnitTest

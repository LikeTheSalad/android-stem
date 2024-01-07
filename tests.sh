set -e
./gradlew test
./gradlew -p "stem-test" testDebugUnitTest

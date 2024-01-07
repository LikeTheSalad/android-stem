set -e
./gradlew test
./gradlew functionalTest
./gradlew -p "stem-test" testDebugUnitTest

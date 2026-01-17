plugins {
    alias(libs.plugins.artifactPublisher)
    kotlin("jvm") version "2.2.20" apply false
}

artifactPublisher {
    displayName = "Android Stem"
    url = "https://github.com/LikeTheSalad/android-stem"
    vcsUrl = "https://github.com/LikeTheSalad/android-stem"
    issueTrackerUrl = "https://github.com/LikeTheSalad/android-stem/issues"
    tags.addAll("android", "stem", "xml", "strings")
}

description = "Resolves Android app's XML string references in other XML strings at compile time."

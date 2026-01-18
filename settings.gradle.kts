pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
    resolutionStrategy {
        eachPlugin {
            val pluginId = requested.id.id
            if (pluginId == "com.likethesalad.artifact-publisher") {
                useModule("com.likethesalad.tools:artifact-publisher:${requested.version}")
            }
            if (pluginId == "plugin-metadata-producer") {
                useModule("com.likethesalad.tools:plugin-metadata-producer:1.0.0")
            }
            if (requested.id.namespace == "com.likethesalad.tools") {
                useModule("com.likethesalad.tools:plugin-tools:${requested.version}")
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}
rootProject.name = "stem"
include(":stem-plugin")

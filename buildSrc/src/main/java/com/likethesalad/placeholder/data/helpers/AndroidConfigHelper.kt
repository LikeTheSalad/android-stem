package com.likethesalad.placeholder.data.helpers

import org.gradle.api.artifacts.ArtifactCollection
import org.gradle.api.artifacts.Configuration

@Suppress("UnstableApiUsage")
class AndroidConfigHelper(
    private val configuration: Configuration,
    private val androidArtifactViewActionProvider: AndroidArtifactViewActionProvider = AndroidArtifactViewActionProvider()
) {

    fun getResArtifactCollection(): ArtifactCollection {
        return configuration.incoming
            .artifactView(androidArtifactViewActionProvider.getResArtifactViewAction())
            .artifacts
    }
}
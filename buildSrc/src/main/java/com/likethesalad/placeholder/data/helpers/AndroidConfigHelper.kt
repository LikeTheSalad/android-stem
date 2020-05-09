package com.likethesalad.placeholder.data.helpers

import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.FileCollection

@Suppress("UnstableApiUsage")
class AndroidConfigHelper(
    private val configuration: Configuration,
    private val androidArtifactViewActionProvider: AndroidArtifactViewActionProvider = AndroidArtifactViewActionProvider()
) {

    val librariesResDirs: FileCollection by lazy {
        configuration.incoming
            .artifactView(androidArtifactViewActionProvider.getResArtifactViewAction())
            .artifacts
            .artifactFiles
    }
}
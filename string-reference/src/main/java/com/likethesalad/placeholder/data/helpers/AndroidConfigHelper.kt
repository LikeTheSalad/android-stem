package com.likethesalad.placeholder.data.helpers

import com.likethesalad.placeholder.utils.AutoFactory
import com.likethesalad.placeholder.utils.Provided
import com.likethesalad.placeholder.utils.VariantDataExtractor
import org.gradle.api.file.FileCollection

@Suppress("UnstableApiUsage")
@AutoFactory
class AndroidConfigHelper(
    private val variantDataExtractor: VariantDataExtractor,
    @Provided private val androidArtifactViewActionProvider: AndroidArtifactViewActionProvider
) {

    private val configuration by lazy {
        variantDataExtractor.getRuntimeConfiguration()
    }

    val librariesResDirs: FileCollection by lazy {
        configuration.incoming
            .artifactView(androidArtifactViewActionProvider.getResArtifactViewAction())
            .artifacts
            .artifactFiles
    }
}
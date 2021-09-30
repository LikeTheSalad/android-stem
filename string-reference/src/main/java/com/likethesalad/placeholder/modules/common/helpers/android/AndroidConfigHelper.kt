package com.likethesalad.placeholder.modules.common.helpers.android

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.tools.android.plugin.AndroidVariantData
import org.gradle.api.file.FileCollection

@Suppress("UnstableApiUsage")
@AutoFactory
class AndroidConfigHelper(
    private val androidVariantData: AndroidVariantData,
    @Provided private val androidArtifactViewActionProvider: AndroidArtifactViewActionProvider
) {

    private val configuration by lazy {
        androidVariantData.getRuntimeConfiguration()
    }

    val librariesResDirs: FileCollection by lazy {
        configuration.incoming
            .artifactView(androidArtifactViewActionProvider.getResArtifactViewAction())
            .artifacts
            .artifactFiles
    }
}
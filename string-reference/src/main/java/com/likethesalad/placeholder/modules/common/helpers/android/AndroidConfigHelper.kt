package com.likethesalad.placeholder.modules.common.helpers.android

import com.likethesalad.tools.android.plugin.data.AndroidVariantData
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.gradle.api.file.FileCollection

@Suppress("UnstableApiUsage")
class AndroidConfigHelper @AssistedInject constructor(
    @Assisted androidVariantData: AndroidVariantData,
    private val androidArtifactViewActionProvider: AndroidArtifactViewActionProvider
) {

    @AssistedFactory
    interface Factory {
        fun create(androidVariantData: AndroidVariantData): AndroidConfigHelper
    }

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
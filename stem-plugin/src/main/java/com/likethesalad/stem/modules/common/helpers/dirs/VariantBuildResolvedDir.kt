package com.likethesalad.stem.modules.common.helpers.dirs

import com.likethesalad.stem.providers.ProjectDirsProvider
import com.likethesalad.tools.android.plugin.data.AndroidVariantData
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.File

class VariantBuildResolvedDir @AssistedInject constructor(
    projectDirsProvider: ProjectDirsProvider,
    sourceSetsHandler: SourceSetsHandler,
    @Assisted androidVariantData: AndroidVariantData
) {
    @AssistedFactory
    interface Factory {
        fun create(androidVariantData: AndroidVariantData): VariantBuildResolvedDir
    }

    companion object {
        val RESOLVED_DIR_BUILD_RELATIVE_PATH = "generated${File.separator}resolved"

        fun getBuildRelativeResolvedDir(variantName: String): String {
            return "$RESOLVED_DIR_BUILD_RELATIVE_PATH${File.separator}$variantName"
        }
    }

    private val variantName by lazy { androidVariantData.getVariantName() }

    val resolvedDir: File by lazy {
        val dir = File(projectDirsProvider.getBuildDir(), getBuildRelativeResolvedDir(variantName))
        sourceSetsHandler.addToSourceSets(dir, variantName)
        dir
    }
}
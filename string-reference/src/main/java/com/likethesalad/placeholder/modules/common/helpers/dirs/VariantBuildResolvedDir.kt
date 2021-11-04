package com.likethesalad.placeholder.modules.common.helpers.dirs

import com.likethesalad.placeholder.providers.AndroidExtensionProvider
import com.likethesalad.placeholder.providers.BuildDirProvider
import com.likethesalad.placeholder.utils.ConfigurationProvider
import com.likethesalad.tools.android.plugin.data.AndroidVariantData
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.File

class VariantBuildResolvedDir @AssistedInject constructor(
    buildDirProvider: BuildDirProvider,
    configurationProvider: ConfigurationProvider,
    androidExtensionProvider: AndroidExtensionProvider,
    @Assisted androidVariantData: AndroidVariantData
) {
    @AssistedFactory
    interface Factory {
        fun create(androidVariantData: AndroidVariantData): VariantBuildResolvedDir
    }

    private val androidExtension by lazy { androidExtensionProvider.getExtension() }
    private val variantName by lazy { androidVariantData.getVariantName() }

    val resolvedDir: File = if (configurationProvider.keepResolvedFiles()) {
        androidExtension.getVariantSrcDirs(variantName).first()
    } else {
        val dir = File(buildDirProvider.getBuildDir(), "generated/resolved/$variantName")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        addResolvedDirToSourceSets(dir)
        dir
    }


    private fun addResolvedDirToSourceSets(resolvedDir: File) {
        val variantSrcDirs = androidExtension.getVariantSrcDirs(variantName)
        androidExtension.setVariantSrcDirs(variantName, variantSrcDirs + resolvedDir)
    }
}

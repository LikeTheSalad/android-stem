package com.likethesalad.placeholder.modules.common.helpers.dirs

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.placeholder.providers.BuildDirProvider
import com.likethesalad.placeholder.utils.ConfigurationProvider
import com.likethesalad.tools.android.plugin.AndroidExtension
import com.likethesalad.tools.android.plugin.AndroidVariantData
import java.io.File
import javax.inject.Inject

@AutoFactory
class VariantBuildResolvedDir @Inject constructor(
    androidVariantData: AndroidVariantData,
    @Provided buildDirProvider: BuildDirProvider,
    @Provided configurationProvider: ConfigurationProvider,
    @Provided private val androidExtension: AndroidExtension
) {

    private val variantName by lazy { androidVariantData.getVariantName() }

    val resolvedDir: File = if (configurationProvider.keepResolvedFiles()) {
        androidExtension.getVariantSrcDirs(variantName).first()
    } else {
        val dir = File(buildDirProvider.getBuildDir(), "generated/resolved/$variantName")
        addResolvedDirToSourceSets(dir)
        dir
    }


    private fun addResolvedDirToSourceSets(resolvedDir: File) {
        val variantSrcDirs = androidExtension.getVariantSrcDirs(variantName)
        androidExtension.setVariantSrcDirs(variantName, variantSrcDirs + resolvedDir)
    }
}

package com.likethesalad.placeholder.data.storage

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.placeholder.providers.BuildDirProvider
import com.likethesalad.placeholder.utils.AndroidExtensionHelper
import com.likethesalad.placeholder.utils.AppVariantHelper
import com.likethesalad.placeholder.utils.ConfigurationProvider
import java.io.File
import javax.inject.Inject

@AutoFactory
class VariantBuildResolvedDir @Inject constructor(
    private val appVariantHelper: AppVariantHelper,
    @Provided buildDirProvider: BuildDirProvider,
    @Provided private val androidExtensionHelper: AndroidExtensionHelper,
    @Provided configurationProvider: ConfigurationProvider
) {

    private val variantName by lazy { appVariantHelper.getVariantName() }

    val resolvedDir: File = if (configurationProvider.keepResolvedFiles()) {
        androidExtensionHelper.getVariantSrcDirs(variantName).first()
    } else {
        val dir = File(buildDirProvider.getBuildDir(), "generated/resolved/$variantName")
        addResolvedDirToSourceSets(dir)
        dir
    }


    private fun addResolvedDirToSourceSets(resolvedDir: File) {
        val variantSrcDirs = androidExtensionHelper.getVariantSrcDirs(variantName)
        androidExtensionHelper.setVariantSrcDirs(variantName, variantSrcDirs + resolvedDir)
    }
}

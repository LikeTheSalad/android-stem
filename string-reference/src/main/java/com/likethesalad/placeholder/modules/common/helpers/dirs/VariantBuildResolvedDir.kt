package com.likethesalad.placeholder.modules.common.helpers.dirs

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.placeholder.providers.BuildDirProvider
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidExtensionHelper
import com.likethesalad.placeholder.modules.common.helpers.android.AppVariantHelper
import com.likethesalad.placeholder.utils.ConfigurationProvider
import java.io.File
import javax.inject.Inject

@AutoFactory
class VariantBuildResolvedDir @Inject constructor(
    appVariantHelper: AppVariantHelper,
    @Provided buildDirProvider: BuildDirProvider,
    @Provided configurationProvider: ConfigurationProvider,
    @Provided private val androidExtensionHelper: AndroidExtensionHelper
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

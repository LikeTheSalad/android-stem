package com.likethesalad.placeholder.data.storage

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.placeholder.providers.AndroidExtensionProvider
import com.likethesalad.placeholder.providers.BuildDirProvider
import com.likethesalad.placeholder.utils.ConfigurationProvider
import com.likethesalad.placeholder.utils.VariantDataExtractor
import java.io.File
import javax.inject.Inject

@AutoFactory
class VariantBuildResolvedDir @Inject constructor(
    private val variantDataExtractor: VariantDataExtractor,
    @Provided buildDirProvider: BuildDirProvider,
    @Provided private val androidExtensionProvider: AndroidExtensionProvider,
    @Provided configurationProvider: ConfigurationProvider
) {

    private val variantName by lazy { variantDataExtractor.getVariantName() }

    private val androidExtensionWrapper by lazy {
        androidExtensionProvider.getExtension()
    }

    val resolvedDir: File = if (configurationProvider.keepResolvedFiles()) {
        androidExtensionWrapper.getSourceSets().getValue(variantName).getRes().getSrcDirs().first()
    } else {
        val dir = File(buildDirProvider.getBuildDir(), "generated/resolved/$variantName")
        addResolvedDirToSourceSets(dir)
        dir
    }


    private fun addResolvedDirToSourceSets(resolvedDir: File) {
        val variantSourceDirSets = androidExtensionWrapper.getSourceSets().getValue(variantName).getRes()
        val srcDirs = variantSourceDirSets.getSrcDirs()
        variantSourceDirSets.setSrcDirs(srcDirs + resolvedDir)
    }
}

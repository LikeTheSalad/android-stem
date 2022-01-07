package com.likethesalad.placeholder.modules.common.helpers.dirs

import com.likethesalad.placeholder.providers.AndroidExtensionProvider
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SourceSetsHandler @Inject constructor(androidExtensionProvider: AndroidExtensionProvider) {

    private val androidExtension by lazy { androidExtensionProvider.getExtension() }

    fun addToSourceSets(dir: File, variantName: String) {
        val variantSrcDirs = androidExtension.getVariantSrcDirs(variantName)
        androidExtension.setVariantSrcDirs(variantName, variantSrcDirs + dir)
    }
}
package com.likethesalad.stem.modules.common.helpers.dirs

import com.likethesalad.stem.providers.AndroidExtensionProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SourceSetsHandler @Inject constructor(androidExtensionProvider: AndroidExtensionProvider) {

    private val androidExtension by lazy { androidExtensionProvider.getExtension() }

    fun addToSourceSets(dir: Any, variantName: String) {
        androidExtension.addVariantSrcDir(variantName, dir)
    }
}
package com.likethesalad.placeholder.providers

import com.likethesalad.tools.resource.locator.android.extension.LanguageResourceFinder
import com.likethesalad.tools.resource.locator.android.extension.ResourceLocatorExtension
import org.gradle.api.file.FileCollection

class LanguageResourceFinderProvider(
    val directory: FileCollection,
    private val resourceLocatorExtension: ResourceLocatorExtension
) {

    private val languageResourceFinder by lazy {
        resourceLocatorExtension.getResourcesFromDir(directory.singleFile)
    }

    fun get(): LanguageResourceFinder = languageResourceFinder

}
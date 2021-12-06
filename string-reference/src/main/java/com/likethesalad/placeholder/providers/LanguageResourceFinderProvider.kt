package com.likethesalad.placeholder.providers

import com.likethesalad.tools.resource.locator.android.extension.LanguageResourceFinder
import com.likethesalad.tools.resource.locator.android.extension.ResourceLocatorExtension
import org.gradle.api.file.DirectoryProperty

class LanguageResourceFinderProvider(
    val directory: DirectoryProperty,
    private val resourceLocatorExtension: ResourceLocatorExtension
) {

    private val languageResourceFinder by lazy {
        resourceLocatorExtension.getResourcesFromDir(directory.get().asFile)
    }

    fun get(): LanguageResourceFinder = languageResourceFinder

}
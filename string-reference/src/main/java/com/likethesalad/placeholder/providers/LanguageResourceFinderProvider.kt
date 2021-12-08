package com.likethesalad.placeholder.providers

import com.likethesalad.tools.resource.locator.android.extension.LanguageResourceFinder
import com.likethesalad.tools.resource.locator.android.extension.ResourceLocatorExtension
import com.likethesalad.tools.resource.locator.android.extension.observer.data.OutputDirProvider

class LanguageResourceFinderProvider(
    val directoryProvider: OutputDirProvider,
    private val resourceLocatorExtension: ResourceLocatorExtension
) {

    private val languageResourceFinder by lazy {
        resourceLocatorExtension.getResourcesFromDir(directoryProvider.getOutputDirProperty().get().asFile)
    }

    fun get(): LanguageResourceFinder = languageResourceFinder

}
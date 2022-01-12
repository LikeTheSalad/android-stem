package com.likethesalad.placeholder.providers

import com.likethesalad.tools.resource.locator.android.extension.ResourceLocatorExtension
import com.likethesalad.tools.resource.locator.android.extension.observer.data.OutputDirProvider
import com.likethesalad.tools.resource.locator.android.extension.resources.LanguageResourcesHandler

class LanguageResourcesHandlerProvider(
    val directoryProvider: OutputDirProvider,
    private val resourceLocatorExtension: ResourceLocatorExtension
) {

    private val languageResourcesHandler by lazy {
        resourceLocatorExtension.getResourcesFromDir(directoryProvider.getOutputDirProperty().get().asFile)
    }

    fun get(): LanguageResourcesHandler = languageResourcesHandler

}
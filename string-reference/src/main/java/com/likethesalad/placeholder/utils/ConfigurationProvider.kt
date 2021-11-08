package com.likethesalad.placeholder.utils

import com.likethesalad.placeholder.providers.PlaceholderExtensionProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigurationProvider @Inject constructor(private val extensionProvider: PlaceholderExtensionProvider) {

    private val extension by lazy { extensionProvider.getPlaceholderExtension() }

    fun useDependenciesRes(): Boolean {
        return extension.useDependenciesRes
    }
}
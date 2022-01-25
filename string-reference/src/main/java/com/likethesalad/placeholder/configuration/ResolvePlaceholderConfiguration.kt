package com.likethesalad.placeholder.configuration

import com.likethesalad.placeholder.providers.PluginExtensionProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResolvePlaceholderConfiguration @Inject constructor(private val extensionProvider: PluginExtensionProvider) {
    private val extension by lazy { extensionProvider.getPluginExtension() }

    fun resolveOnBuild(): Boolean {
        return extension.resolveOnBuild.get()
    }
}
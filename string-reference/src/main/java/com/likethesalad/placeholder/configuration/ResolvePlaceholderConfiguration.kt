package com.likethesalad.placeholder.configuration

import com.likethesalad.placeholder.providers.PluginExtensionProvider

class ResolvePlaceholderConfiguration(private val extensionProvider: PluginExtensionProvider) {
    private val extension by lazy { extensionProvider.getPluginExtension() }

    fun resolveOnBuild(): Boolean {
        return extension.resolveOnBuild.get()
    }
}
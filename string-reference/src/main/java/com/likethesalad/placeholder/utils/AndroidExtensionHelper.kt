package com.likethesalad.placeholder.utils

import com.likethesalad.placeholder.data.helpers.wrappers.AndroidSourceSetWrapper
import com.likethesalad.placeholder.providers.AndroidExtensionProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidExtensionHelper @Inject constructor(private val androidExtensionProvider: AndroidExtensionProvider) {

    private val extension by lazy { androidExtensionProvider.getExtension() }

    fun getSourceSets(): Map<String, AndroidSourceSetWrapper> {
        return extension.getSourceSets()
    }
}
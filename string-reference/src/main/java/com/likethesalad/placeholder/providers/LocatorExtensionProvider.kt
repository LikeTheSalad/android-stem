package com.likethesalad.placeholder.providers

import com.likethesalad.tools.resource.locator.android.extension.AndroidResourceLocatorExtension

interface LocatorExtensionProvider {
    fun getLocatorExtension(): AndroidResourceLocatorExtension
}
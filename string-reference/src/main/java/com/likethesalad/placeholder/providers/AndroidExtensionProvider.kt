package com.likethesalad.placeholder.providers

import com.likethesalad.placeholder.data.helpers.wrappers.AndroidExtensionWrapper

interface AndroidExtensionProvider {

    fun getExtension(): AndroidExtensionWrapper
}
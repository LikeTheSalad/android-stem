package com.likethesalad.stem.providers

import com.likethesalad.tools.agpcompat.api.bridges.AndroidExtension

interface AndroidExtensionProvider {

    fun getExtension(): AndroidExtension
}
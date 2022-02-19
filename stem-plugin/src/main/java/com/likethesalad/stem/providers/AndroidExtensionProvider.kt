package com.likethesalad.stem.providers

import com.likethesalad.tools.android.plugin.data.AndroidExtension

interface AndroidExtensionProvider {

    fun getExtension(): AndroidExtension
}
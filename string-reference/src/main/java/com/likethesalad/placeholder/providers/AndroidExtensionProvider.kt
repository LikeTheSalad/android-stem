package com.likethesalad.placeholder.providers

import com.likethesalad.tools.android.plugin.AndroidExtension

interface AndroidExtensionProvider {

    fun getExtension(): AndroidExtension
}
package com.likethesalad.placeholder.providers

import com.likethesalad.tools.android.plugin.data.AndroidExtension

interface AndroidExtensionProvider {

    fun getExtension(): AndroidExtension
}
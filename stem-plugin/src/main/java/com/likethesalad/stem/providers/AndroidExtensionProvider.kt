package com.likethesalad.stem.providers

import com.android.build.api.dsl.ApplicationExtension
import com.likethesalad.tools.agpcompat.api.bridges.AndroidExtension

interface AndroidExtensionProvider {

    fun getExtension(): AndroidExtension
    fun getApplicationExtension(): ApplicationExtension
}
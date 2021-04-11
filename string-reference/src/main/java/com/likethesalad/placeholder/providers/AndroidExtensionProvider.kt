package com.likethesalad.placeholder.providers

import com.android.build.gradle.AppExtension

interface AndroidExtensionProvider {

    fun getExtension(): AppExtension
}
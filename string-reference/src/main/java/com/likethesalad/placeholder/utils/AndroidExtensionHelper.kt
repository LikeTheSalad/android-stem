package com.likethesalad.placeholder.utils

import com.android.build.gradle.api.AndroidSourceDirectorySet
import com.likethesalad.placeholder.providers.AndroidExtensionProvider
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidExtensionHelper @Inject constructor(private val androidExtensionProvider: AndroidExtensionProvider) {

    private val extension by lazy { androidExtensionProvider.getExtension() }

    fun getVariantSrcDirs(variantName: String): Set<File> {
        return getVariantRes(variantName).srcDirs
    }

    fun setVariantSrcDirs(variantName: String, dirs: Set<File>) {
        getVariantRes(variantName).setSrcDirs(dirs)
    }

    private fun getVariantRes(variantName: String): AndroidSourceDirectorySet {
        return extension.sourceSets.getByName(variantName).res
    }
}
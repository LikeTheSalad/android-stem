package com.likethesalad.placeholder.data.storage

import com.likethesalad.placeholder.data.helpers.wrappers.AndroidExtensionWrapper
import java.io.File

class VariantBuildResolvedDir(
    private val variantName: String,
    buildDir: File,
    private val androidExtensionWrapper: AndroidExtensionWrapper,
    keepResolvedFiles: Boolean
) {

    val resolvedDir: File = if (keepResolvedFiles) {
        androidExtensionWrapper.getSourceSets().getValue(variantName).getRes().getSrcDirs().first()
    } else {
        val dir = File(buildDir, "generated/resolved/$variantName")
        addResolvedDirToSourceSets(dir)
        dir
    }


    private fun addResolvedDirToSourceSets(resolvedDir: File) {
        val variantSourceDirSets = androidExtensionWrapper.getSourceSets().getValue(variantName).getRes()
        val srcDirs = variantSourceDirSets.getSrcDirs()
        variantSourceDirSets.setSrcDirs(srcDirs + resolvedDir)
    }
}

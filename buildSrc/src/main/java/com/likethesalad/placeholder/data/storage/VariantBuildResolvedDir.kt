package com.likethesalad.placeholder.data.storage

import com.likethesalad.placeholder.data.helpers.wrappers.AndroidExtensionWrapper
import java.io.File

class VariantBuildResolvedDir(
    variantName: String,
    buildDir: File,
    androidExtensionWrapper: AndroidExtensionWrapper,
    keepResolvedFiles: Boolean
) {

    val resolvedDir: File by lazy {
        if (keepResolvedFiles) {
            androidExtensionWrapper.getSourceSets().getValue(variantName).getRes().getSrcDirs().first()
        } else {
            File(buildDir, "generated/resolved/$variantName")
        }
    }
}

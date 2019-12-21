package com.likethesalad.placeholder.data

import com.likethesalad.placeholder.data.helpers.wrappers.AndroidExtensionWrapper
import com.likethesalad.placeholder.data.helpers.wrappers.AndroidSourceSetWrapper
import com.likethesalad.placeholder.data.storage.IncrementalDirsProvider
import com.likethesalad.placeholder.models.PathIdentity
import java.io.File

class PathIdentityResolver(
    androidExtensionWrapper: AndroidExtensionWrapper,
    private val incrementalDirsProvider: IncrementalDirsProvider
) {

    companion object {
        private const val RESOLVED_FILE_NAME = "resolved.xml"
    }

    private val sourceSets: Map<String, AndroidSourceSetWrapper> by lazy { androidExtensionWrapper.getSourceSets() }

    fun getResolvedStringsFile(pathIdentity: PathIdentity): File {
        val srcDir = sourceSets.getValue(pathIdentity.variantName).getRes().getSrcDirs().first()

        return File(srcDir, "${pathIdentity.valuesFolderName}/$RESOLVED_FILE_NAME")
    }

    fun getRawStringsFile(pathIdentity: PathIdentity): File {
        return File(incrementalDirsProvider.getRawStringsDir(), "strings${pathIdentity.suffix}.json")
    }

    fun getTemplateStringsFile(pathIdentity: PathIdentity): File {
        return File(incrementalDirsProvider.getTemplateStringsDir(), "templates${pathIdentity.suffix}.json")
    }
}

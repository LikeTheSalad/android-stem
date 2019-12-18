package com.likethesalad.placeholder.data

import com.likethesalad.placeholder.data.helpers.wrappers.AndroidExtensionWrapper
import com.likethesalad.placeholder.data.helpers.wrappers.AndroidSourceSetWrapper
import com.likethesalad.placeholder.models.PathIdentity
import java.io.File

class PathIdentityResolver(androidExtensionWrapper: AndroidExtensionWrapper) {

    companion object {
        private const val RESOLVED_FILE_NAME = "resolved.xml"
    }

    private val sourceSets: Map<String, AndroidSourceSetWrapper> by lazy { androidExtensionWrapper.getSourceSets() }

    fun getRawStringsFile(pathIdentity: PathIdentity): File {
        val srcDir = sourceSets.getValue(pathIdentity.variantName).getRes().getSrcDirs().first()

        return File(srcDir, "${pathIdentity.valuesFolderName}/$RESOLVED_FILE_NAME")
    }
}

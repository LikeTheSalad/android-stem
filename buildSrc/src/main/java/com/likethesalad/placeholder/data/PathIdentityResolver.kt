package com.likethesalad.placeholder.data

import com.likethesalad.placeholder.data.helpers.wrappers.AndroidExtensionWrapper
import com.likethesalad.placeholder.models.PathIdentity
import java.io.File

class PathIdentityResolver(private val androidExtensionWrapper: AndroidExtensionWrapper) {

    companion object {
        private const val RESOLVED_FILE_NAME = "resolved.xml"
    }

    fun getRawStringsFile(pathIdentity: PathIdentity): File {
        val srcDir = androidExtensionWrapper.getSourceSets()
            .getValue(pathIdentity.variantName).getRes().getSrcDirs().first()

        return File(srcDir, "${pathIdentity.valuesFolderName}/$RESOLVED_FILE_NAME")
    }
}

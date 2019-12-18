package com.likethesalad.placeholder.data

import com.likethesalad.placeholder.data.helpers.AndroidVariantHelper
import com.likethesalad.placeholder.data.helpers.wrappers.AndroidExtensionWrapper
import com.likethesalad.placeholder.data.helpers.wrappers.AndroidSourceSetWrapper
import com.likethesalad.placeholder.models.PathIdentity
import java.io.File

class PathIdentityResolver(
    androidExtensionWrapper: AndroidExtensionWrapper,
    androidVariantHelper: AndroidVariantHelper
) {

    companion object {
        private const val RESOLVED_FILE_NAME = "resolved.xml"
    }

    private val sourceSets: Map<String, AndroidSourceSetWrapper> by lazy { androidExtensionWrapper.getSourceSets() }
    private val incrementalDir: File by lazy { File(androidVariantHelper.incrementalDir) }

    fun getResolvedStringsFile(pathIdentity: PathIdentity): File {
        val srcDir = sourceSets.getValue(pathIdentity.variantName).getRes().getSrcDirs().first()

        return File(srcDir, "${pathIdentity.valuesFolderName}/$RESOLVED_FILE_NAME")
    }

    fun getRawStringsFile(pathIdentity: PathIdentity): File {
        return File(incrementalDir, "strings/strings${pathIdentity.suffix}.json")
    }
}

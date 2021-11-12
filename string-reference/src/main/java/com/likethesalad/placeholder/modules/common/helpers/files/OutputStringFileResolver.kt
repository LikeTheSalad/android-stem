package com.likethesalad.placeholder.modules.common.helpers.files

import com.likethesalad.placeholder.modules.common.Constants.Companion.RESOLVED_FILE_NAME
import com.likethesalad.placeholder.modules.common.Constants.Companion.TEMPLATE_BASE_FILE_NAME
import com.likethesalad.placeholder.modules.common.helpers.dirs.IncrementalDirsProvider
import com.likethesalad.placeholder.modules.common.helpers.dirs.VariantBuildResolvedDir
import java.io.File

class OutputStringFileResolver(
    private val variantBuildResolvedDir: VariantBuildResolvedDir,
    private val incrementalDirsProvider: IncrementalDirsProvider
) {

    fun getResolvedStringsFile(valuesFolderName: String): File {
        val valuesFolder = File(variantBuildResolvedDir.resolvedDir, valuesFolderName)
        if (!valuesFolder.exists()) {
            valuesFolder.mkdirs()
        }
        return File(valuesFolder, RESOLVED_FILE_NAME)
    }

    fun getTemplateStringsFile(suffix: String): File {
        return File(
            incrementalDirsProvider.getTemplateStringsDir(),
            "$TEMPLATE_BASE_FILE_NAME$suffix.json"
        )
    }
}

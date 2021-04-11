package com.likethesalad.placeholder.modules.common.helpers.files

import com.likethesalad.placeholder.modules.common.Constants.Companion.RAW_STRING_BASE_FILE_NAME
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
        return File(variantBuildResolvedDir.resolvedDir, "$valuesFolderName/$RESOLVED_FILE_NAME")
    }

    fun getRawStringsFile(suffix: String): File {
        return File(
            incrementalDirsProvider.getRawStringsDir(),
            "$RAW_STRING_BASE_FILE_NAME$suffix.json"
        )
    }

    fun getTemplateStringsFile(suffix: String): File {
        return File(
            incrementalDirsProvider.getTemplateStringsDir(),
            "$TEMPLATE_BASE_FILE_NAME$suffix.json"
        )
    }
}

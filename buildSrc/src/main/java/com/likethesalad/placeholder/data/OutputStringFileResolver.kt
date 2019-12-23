package com.likethesalad.placeholder.data

import com.likethesalad.placeholder.data.Constants.Companion.RAW_STRING_BASE_FILE_NAME
import com.likethesalad.placeholder.data.Constants.Companion.RESOLVED_FILE_NAME
import com.likethesalad.placeholder.data.Constants.Companion.TEMPLATE_BASE_FILE_NAME
import com.likethesalad.placeholder.data.helpers.wrappers.AndroidExtensionWrapper
import com.likethesalad.placeholder.data.storage.IncrementalDirsProvider
import java.io.File

class OutputStringFileResolver(
    buildVariantName: String,
    androidExtensionWrapper: AndroidExtensionWrapper,
    private val incrementalDirsProvider: IncrementalDirsProvider
) {

    private val variantResDir: File by lazy {
        androidExtensionWrapper.getSourceSets().getValue(buildVariantName).getRes().getSrcDirs().first()
    }

    fun getResolvedStringsFile(valuesFolderName: String): File {
        return File(variantResDir, "$valuesFolderName/$RESOLVED_FILE_NAME")
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

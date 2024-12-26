package com.likethesalad.stem.modules.common.helpers.files

import com.likethesalad.stem.modules.common.Constants.Companion.RESOLVED_FILE_NAME
import com.likethesalad.stem.modules.common.Constants.Companion.TEMPLATE_BASE_FILE_NAME
import java.io.File

class OutputStringFileResolver {

    fun getResolvedStringsFile(dir: File, valuesFolderName: String): File {
        val valuesFolder = File(dir, valuesFolderName)
        if (!valuesFolder.exists()) {
            valuesFolder.mkdirs()
        }
        return File(valuesFolder, RESOLVED_FILE_NAME)
    }

    fun getTemplateStringsFile(dir: File, suffix: String): File {
        return File(dir, "$TEMPLATE_BASE_FILE_NAME-$suffix.json")
    }
}

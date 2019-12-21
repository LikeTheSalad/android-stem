package com.likethesalad.placeholder.data.storage

import com.likethesalad.placeholder.data.helpers.AndroidVariantHelper
import java.io.File

class IncrementalDirsProvider(androidVariantHelper: AndroidVariantHelper) {
    private val incrementalDir = File(androidVariantHelper.incrementalDir)

    fun getRawStringsDir(): File {
        return File(incrementalDir, "strings")
    }

    fun getTemplateStringsDir(): File {
        return File(incrementalDir, "templates")
    }

}

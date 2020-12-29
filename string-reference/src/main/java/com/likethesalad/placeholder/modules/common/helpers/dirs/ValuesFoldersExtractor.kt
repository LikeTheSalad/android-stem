package com.likethesalad.placeholder.modules.common.helpers.dirs

import com.likethesalad.placeholder.modules.common.helpers.dirs.utils.ValuesNameUtils
import java.io.File

class ValuesFoldersExtractor(private val resDirs: Set<File>) {

    fun getValuesFolders(): List<File> {
        return resDirs.map {
            it.listFiles { _, name ->
                ValuesNameUtils.isValueName(name)
            }?.toList() ?: emptyList()
        }.flatten()
    }
}
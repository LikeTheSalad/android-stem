package com.likethesalad.placeholder.data.storage

import com.likethesalad.placeholder.utils.ValuesNameUtils
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
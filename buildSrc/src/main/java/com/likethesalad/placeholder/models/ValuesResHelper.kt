package com.likethesalad.placeholder.models

import com.likethesalad.placeholder.utils.ValuesNameUtils
import java.io.File

class ValuesResHelper {

    companion object {
        private const val BASE_VALUES_NAME = "values"
    }

    fun getUniqueLanguageValuesDirName(resDirs: List<File>): List<String> {
        return getValuesFolders(resDirs).map {
            it.name
        }.toSet().filter { it != BASE_VALUES_NAME }
    }

    private fun getValuesFolders(resDirs: List<File>): List<File> {
        return resDirs.map {
            it.listFiles { _, name ->
                ValuesNameUtils.isValueName(name)
            }?.toList() ?: emptyList()
        }.flatten()
    }

}

package com.likethesalad.placeholder.utils

import java.io.File

class ValuesNameUtils {
    companion object {
        private val VALUES_FOLDER_NAME_REGEX = Regex("values(-[a-z]{2}(-r[A-Z]{2})*)*")

        fun isValueName(name: String): Boolean {
            return VALUES_FOLDER_NAME_REGEX.matches(name)
        }

        fun getUniqueValuesDirName(resDirs: Set<File>): Set<String> {
            return getValuesFolders(resDirs).map {
                it.name
            }.toSet()
        }

        private fun getValuesFolders(resDirs: Set<File>): List<File> {
            return resDirs.map {
                it.listFiles { _, name ->
                    ValuesNameUtils.isValueName(name)
                }?.toList() ?: emptyList()
            }.flatten()
        }
    }
}

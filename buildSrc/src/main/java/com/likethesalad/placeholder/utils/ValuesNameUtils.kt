package com.likethesalad.placeholder.utils

import java.io.File

class ValuesNameUtils {
    companion object {
        private val VALUES_FOLDER_NAME_REGEX = Regex("values(-[a-z]{2}(-r[A-Z]{2})*)*")

        fun isValueName(name: String): Boolean {
            return VALUES_FOLDER_NAME_REGEX.matches(name)
        }

        fun getValuesFolders(resDirs: Set<File>): List<File> {
            return resDirs.map {
                it.listFiles { _, name ->
                    isValueName(name)
                }?.toList() ?: emptyList()
            }.flatten()
        }

        fun getValuesNameSuffix(valuesFolderName: String): String {
            return VALUES_FOLDER_NAME_REGEX.find(valuesFolderName)!!.groupValues[1]
        }
    }
}

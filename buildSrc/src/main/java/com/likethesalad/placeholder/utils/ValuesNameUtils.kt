package com.likethesalad.placeholder.utils

class ValuesNameUtils {
    companion object {
        private val VALUES_FOLDER_NAME_REGEX = Regex("values(-[a-z]{2}(-r[A-Z]{2})*)*")

        fun isValueName(name: String): Boolean {
            return VALUES_FOLDER_NAME_REGEX.matches(name)
        }
    }
}

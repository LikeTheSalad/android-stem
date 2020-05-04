package com.likethesalad.placeholder.utils

object DependenciesExtensionCleaner {
    fun cleanUpDependencies(param: Any): List<String> {
        if (param is String) {
            return listOf(param)
        }
        try {
            val list = param as List<*>
            if (list.filterNotNull().any { !it::class.java.isAssignableFrom(String::class.java) }) {
                throw getException()
            }

            return list as List<String>
        } catch (e: ClassCastException) {
            throw getException()
        }
    }

    private fun getException(): Exception {
        return IllegalArgumentException(
            "The type of the parameter 'useStringsFromDependencies'" +
                    " can only be either String or List of Strings"
        )
    }
}
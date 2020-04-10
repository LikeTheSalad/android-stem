package com.likethesalad.placeholder.data.storage.libraries

class LibrariesNameValidator(private val allowedNames: Set<String>) {

    private val fullNames: Set<String>
    private val startOfNames: Set<String>

    companion object {
        private const val TYPE_FULL_NAME = 1
        private const val TYPE_START_OF_NAME = 2
    }

    init {
        val namesByType = allowedNames.groupBy {
            when (it.contains("*")) {
                true -> TYPE_START_OF_NAME
                false -> TYPE_FULL_NAME
            }
        }

        fullNames = namesByType.getOrDefault(TYPE_FULL_NAME, emptyList()).toSet()
        startOfNames = namesByType.getOrDefault(TYPE_START_OF_NAME, emptyList())
            .map { it.replace("*", "") }.toSet()
    }

    fun isNameValid(name: String): Boolean {
        return name in allowedNames || startOfNames.any { name.startsWith(it) }
    }
}
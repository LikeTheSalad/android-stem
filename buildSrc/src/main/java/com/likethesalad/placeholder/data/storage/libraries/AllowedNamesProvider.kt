package com.likethesalad.placeholder.data.storage.libraries

class AllowedNamesProvider(allowedNames: List<String>) {

    val fullNames: Set<String>
    val startOfNames: Set<String>

    companion object {
        private const val TYPE_FULL_NAME = 1
        private const val TYPE_START_OF_NAME = 2
        private const val ANY_CHARS_WILDCARD = "*"
    }

    init {
        val curatedAllowedNames = allowedNames.map { it.trim() }
            .filter { it.isNotEmpty() }
            .toSet()
        val namesByType = curatedAllowedNames.groupBy {
            when (it.contains(ANY_CHARS_WILDCARD)) {
                true -> TYPE_START_OF_NAME
                false -> TYPE_FULL_NAME
            }
        }

        fullNames = namesByType.getOrDefault(TYPE_FULL_NAME, emptyList()).toSet()

        val curatedStartOfNames = mutableListOf<String>()
        for (item in namesByType.getOrDefault(TYPE_START_OF_NAME, emptyList())) {
            if (item.count { it.toString() == ANY_CHARS_WILDCARD } > 1) {
                throw IllegalArgumentException(
                    "Misuse of wildcard in: '$item' - There can only be one asterisk wildcard per item"
                )
            }
            if (item.last().toString() != ANY_CHARS_WILDCARD) {
                throw IllegalArgumentException(
                    "Misuse of wildcard in: '$item' - Asterisk wildcard should be the last thing on a String"
                )
            }
            curatedStartOfNames.add(item)
        }

        startOfNames = curatedStartOfNames.map { it.replace(ANY_CHARS_WILDCARD, "") }.toSet()
    }
}
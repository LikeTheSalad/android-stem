package com.likethesalad.placeholder.data.storage.libraries.helpers

class LibrariesNameValidator(private val allowedNamesProvider: AllowedNamesProvider) {

    fun isNameValid(name: String): Boolean {
        if (allowedNamesProvider.allowAll) {
            return true
        }
        if (allowedNamesProvider.allowNone) {
            return false
        }
        return name in allowedNamesProvider.fullNames ||
                allowedNamesProvider.startOfNames.any { name.startsWith(it) }
    }
}
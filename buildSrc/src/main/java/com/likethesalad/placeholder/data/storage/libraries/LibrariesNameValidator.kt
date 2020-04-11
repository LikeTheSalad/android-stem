package com.likethesalad.placeholder.data.storage.libraries

class LibrariesNameValidator(private val allowedNamesProvider: AllowedNamesProvider) {
    
    fun isNameValid(name: String): Boolean {
        return name in allowedNamesProvider.fullNames ||
                allowedNamesProvider.startOfNames.any { name.startsWith(it) }
    }
}
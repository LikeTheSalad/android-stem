package com.likethesalad.placeholder.data.storage.libraries.helpers

import com.likethesalad.placeholder.data.helpers.AndroidConfigHelper
import com.likethesalad.placeholder.models.AndroidLibrary

class AndroidLibrariesProvider(
    private val androidConfigHelper: AndroidConfigHelper,
    private val librariesNameValidator: LibrariesNameValidator
) {

    fun getAndroidLibraries(): Set<AndroidLibrary> {
        val artifacts = androidConfigHelper.getResArtifactCollection().artifacts
        return artifacts.map { AndroidLibrary(it.id.displayName, it.file) }
            .filter { librariesNameValidator.isNameValid(it.name) }
            .toSet()
    }
}
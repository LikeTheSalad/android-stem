package com.likethesalad.placeholder.data.storage.libraries

import com.likethesalad.placeholder.data.helpers.AndroidConfigHelper
import com.likethesalad.placeholder.models.AndroidLibrary

class AndroidLibrariesProvider(private val androidConfigHelper: AndroidConfigHelper) {

    fun getAndroidLibraries(): Set<AndroidLibrary> {
        val artifacts = androidConfigHelper.getResArtifactCollection().artifacts
        return artifacts.map { AndroidLibrary(it.id.displayName, it.file) }.toSet()
    }
}
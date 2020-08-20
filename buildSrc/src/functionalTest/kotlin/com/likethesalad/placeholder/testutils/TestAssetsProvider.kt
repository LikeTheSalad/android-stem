package com.likethesalad.placeholder.testutils

import java.io.File
import java.nio.file.Paths

class TestAssetsProvider(val assetsFolderName: String) {

    private val functionalAssetsDir: File by lazy {
        Paths.get("src", "functionalTest", "assets").toFile()
    }

    fun getAssetFile(relativePath: String): File {
        val asset = File(functionalAssetsDir, relativePath)
        if (!asset.exists()) {
            throw IllegalArgumentException("Asset '$relativePath' not found in '$assetsFolderName'")
        }

        return asset
    }
}
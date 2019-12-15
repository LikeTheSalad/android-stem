package com.likethesalad.placeholder.testutils

import java.io.File
import java.nio.file.Paths

class TestResourcesHandler(clazz: Class<*>) {
    private val packageResourcesPath: File by lazy {
        val resourceDirectory = Paths.get("src", "test", "resources").toFile().absolutePath
        File("$resourceDirectory/${clazz.`package`.name.replace(".", "/")}")
    }

    fun getResourceFile(relativePath: String): File {
        return File(packageResourcesPath, relativePath)
    }
}
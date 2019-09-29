package com.likethesalad.placeholder.data.helpers.wrappers.testutils

import java.io.File

class TestAndroidSourceDirectorySet(private val files: Set<File>) {

    fun getSrcDirs(): Set<File> {
        return files
    }
}
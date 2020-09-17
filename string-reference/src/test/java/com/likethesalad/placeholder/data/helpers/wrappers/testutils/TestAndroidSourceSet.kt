package com.likethesalad.placeholder.data.helpers.wrappers.testutils

import java.io.File

class TestAndroidSourceSet(private val theName: String, private val files: Set<File>) {

    fun getRes(): TestAndroidSourceDirectorySet {
        return TestAndroidSourceDirectorySet(files)
    }

    fun getName(): String {
        return theName
    }
}
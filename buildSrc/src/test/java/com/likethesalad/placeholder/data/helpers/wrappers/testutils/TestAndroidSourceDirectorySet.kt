package com.likethesalad.placeholder.data.helpers.wrappers.testutils

import java.io.File

class TestAndroidSourceDirectorySet(files: Set<File>) {

    private var localFiles: Iterable<File> = files

    fun getSrcDirs(): Set<File> {
        return localFiles.toSet()
    }

    fun setSrcDirs(srcDirs: Iterable<File>) {
        localFiles = srcDirs
    }
}
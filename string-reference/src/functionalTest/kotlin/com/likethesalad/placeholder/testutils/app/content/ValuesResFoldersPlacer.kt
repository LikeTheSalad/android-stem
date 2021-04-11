package com.likethesalad.placeholder.testutils.app.content

import com.likethesalad.placeholder.testutils.base.content.ProjectDirContentPlacer
import java.io.File

class ValuesResFoldersPlacer(
    private val flavorInputDir: File
) : ProjectDirContentPlacer() {

    override fun getRelativePath(): String = "src"

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onDirReady(dir: File) {
        for (it in flavorInputDir.listFiles()) {
            it.copyRecursively(File(dir, it.name))
        }
    }
}
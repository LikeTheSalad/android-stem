package com.likethesalad.placeholder.testutils.app.content

import com.likethesalad.placeholder.testutils.base.content.ProjectDirContentPlacer
import java.io.File

class ValuesResFilesPlacer(
    val flavorName: String,
    val xmlFiles: List<File>
) : ProjectDirContentPlacer() {

    override fun getRelativePath(): String = "src/$flavorName/res/values"

    override fun onDirReady(dir: File) {
        for (it in xmlFiles) {
            it.copyTo(dir)
        }
    }
}
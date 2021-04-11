package com.likethesalad.placeholder.testutils.base.layout

import com.likethesalad.placeholder.testutils.base.content.ProjectDirContentPlacer
import java.io.File

class ProjectDirectoryBuilder {

    private val contentPlacers = mutableListOf<ProjectDirContentPlacer>()

    fun register(projectDirContentPlacer: ProjectDirContentPlacer) {
        if (contentPlacers.contains(projectDirContentPlacer)) {
            throw IllegalArgumentException("ProjectDirContentPlacer already exists")
        }

        contentPlacers.add(projectDirContentPlacer)
    }

    fun buildDirectory(projectDir: File) {
        for (it in contentPlacers) {
            it.setProjectDir(projectDir)
        }
    }
}
package com.likethesalad.placeholder.testutils.base.layout

abstract class ProjectDescriptor {

    val projectDirectoryBuilder = ProjectDirectoryBuilder()

    abstract fun getBuildGradleContents(): String

    abstract fun getProjectName(): String
}
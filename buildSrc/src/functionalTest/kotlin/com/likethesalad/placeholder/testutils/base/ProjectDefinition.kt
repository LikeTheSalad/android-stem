package com.likethesalad.placeholder.testutils.base

interface ProjectDefinition {
    fun getBuildGradleContents(): String

    fun projectName(): String
}
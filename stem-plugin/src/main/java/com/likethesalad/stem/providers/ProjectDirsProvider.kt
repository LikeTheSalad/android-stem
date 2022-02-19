package com.likethesalad.stem.providers

import java.io.File

interface ProjectDirsProvider {
    fun getProjectDir(): File
    fun getRootProjectDir(): File
    fun getBuildDir(): File
}
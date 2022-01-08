package com.likethesalad.placeholder.providers

import java.io.File

interface ProjectDirsProvider {
    fun getProjectDir(): File
    fun getBuildDir(): File
}
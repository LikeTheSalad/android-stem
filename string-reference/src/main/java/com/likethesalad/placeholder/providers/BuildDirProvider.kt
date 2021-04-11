package com.likethesalad.placeholder.providers

import java.io.File

interface BuildDirProvider {

    fun getBuildDir(): File
}
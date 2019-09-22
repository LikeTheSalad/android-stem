package com.example.placeholder.models

import java.io.File

data class ResDirs(
    val mainDirs: Set<File>,
    val flavorDirs: Set<File>
) {
    val hasFlavorDirs = flavorDirs.isNotEmpty()
}
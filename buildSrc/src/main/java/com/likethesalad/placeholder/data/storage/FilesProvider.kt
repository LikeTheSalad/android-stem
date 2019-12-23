package com.likethesalad.placeholder.data.storage

import java.io.File

interface FilesProvider {

    fun getAllExpectedResolvedFiles(): List<File>

    fun getAllGatheredStringsFiles(): List<File>

    fun getAllTemplatesFiles(): List<File>

    fun getAllExpectedTemplatesFiles(): List<File>
}
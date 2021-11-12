package com.likethesalad.placeholder.modules.common.helpers.files.storage

import java.io.File

interface FilesProvider {

    fun getAllExpectedResolvedFiles(): List<File>

    fun getAllTemplatesFiles(): List<File>

    fun getAllExpectedTemplatesFiles(): List<File>
}
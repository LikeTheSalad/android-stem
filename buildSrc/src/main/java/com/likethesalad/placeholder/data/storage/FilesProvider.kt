package com.likethesalad.placeholder.data.storage

import com.likethesalad.placeholder.models.raw.RawFiles
import java.io.File

interface FilesProvider {

    /**
     * Returns the final file where all of the resolved strings
     * will go to, this will be into the project's sourceSets resources.
     * @param suffix -  The values folder name suffix.
     */
    fun getAllExpectedResolvedFiles(): List<File>

    /**
     * Internal incremental file of all strings gathered from one values folder.
     * @param suffix - The suffix of the values folder name.
     */
    fun getGatheredStringsFile(suffix: String = ""): File

    fun getAllGatheredStringsFiles(): List<File>

    /**
     * Internal incremental file that contains only template strings and their values.
     * @param suffix - The string file name suffix.
     */
    fun getTemplateFile(suffix: String = ""): File

    fun getAllTemplatesFiles(): List<File>

    fun getAllExpectedTemplatesFiles(): List<File>

    fun getAllFoldersRawResourcesFiles(): List<RawFiles>
}
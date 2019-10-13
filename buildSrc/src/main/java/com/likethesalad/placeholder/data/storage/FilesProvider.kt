package com.likethesalad.placeholder.data.storage

import com.likethesalad.placeholder.models.raw.RawFiles
import java.io.File

interface FilesProvider {

    /**
     * Returns the final file where all of the resolved strings
     * will go to, this will be into the project's sourceSets resources.
     * @param valuesFolderName -  The values folder name the resolves strings are going to.
     */
    fun getResolvedFile(suffix: String): File

    fun getAllExpectedResolvedFiles(): List<File>

    /**
     * Internal incremental file of all strings gathered from one values folder.
     * @param valuesFolderName - The name of the values folder these strings will be for.
     */
    fun getGatheredStringsFile(suffix: String = ""): File

    fun getAllGatheredStringsFiles(): List<File>

    /**
     * Internal incremental file that contains only template strings and their values.
     * @param stringFile -  The string file where the gathered raw strings are in.
     */
    fun getTemplateFileForStringFile(stringFile: File): File

    fun getAllTemplatesFiles(): List<File>

    fun getAllExpectedTemplatesFiles(): List<File>

    /**
     * Returns a list of lists for all of the files within the values folder of the application/flavor except for the
     * 'resolved.xml' file which is this plugin's output.
     * @param valuesFolderName -  The name of the folder where the placeholders are being resolved. This is usually
     * `values` but it could change for other languages.
     */
    fun getRawResourcesFilesForFolder(valuesFolderName: String): RawFiles

    fun getAllFoldersRawResourcesFiles(): List<RawFiles>
}
package com.likethesalad.placeholder.data

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.storage.ValuesFoldersExtractor
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class VariantValuesFoldersTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `Get all values files per values folder`() {
        val variantName = "main"
        val res1 = getResDirWithFolders(
            "res1", mapOf(
                "values" to listOf("strings.xml", "strings2.xml"),
                "values-es" to listOf("strings_es1.xml"),
                "values-pt" to emptyList()
            )
        )
        val res2 = getResDirWithFolders(
            "res2", mapOf(
                "values" to listOf("strings.xml", "strings3.xml"),
                "values-it" to listOf("strings_it.xml", "strings_it2.xml")
            )
        )
        val resDirs = setOf(res1, res2)
        val resValuesFiles = VariantValuesFolders(variantName, ValuesFoldersExtractor(resDirs))

        val valuesFiles = resValuesFiles.valuesStringFiles
        Truth.assertThat(valuesFiles.size).isEqualTo(3)
        assertThatValuesFilesContainsFiles(
            valuesFiles.getValue("values"),
            "${res1.absolutePath}/values/strings.xml",
            "${res1.absolutePath}/values/strings2.xml",
            "${res2.absolutePath}/values/strings.xml",
            "${res2.absolutePath}/values/strings3.xml"
        )
        assertThatValuesFilesContainsFiles(
            valuesFiles.getValue("values-es"),
            "${res1.absolutePath}/values-es/strings_es1.xml"
        )
        assertThatValuesFilesContainsFiles(
            valuesFiles.getValue("values-it"),
            "${res2.absolutePath}/values-it/strings_it.xml",
            "${res2.absolutePath}/values-it/strings_it2.xml"
        )
    }

    @Test
    fun `Filter out generated file and non xml files`() {
        val variantName = "client"
        val res1 = getResDirWithFolders(
            "res1", mapOf(
                "values" to listOf("strings.xml", "strings2.xml", "something.xml2"),
                "values-es" to listOf("strings_es1.xml", "strings_es.cml", "resolved.xml"),
                "values-pt" to emptyList()
            )
        )
        val resDirs = setOf(res1)
        val resValuesFiles = VariantValuesFolders(variantName, ValuesFoldersExtractor(resDirs))

        val valuesFiles = resValuesFiles.valuesStringFiles
        Truth.assertThat(valuesFiles.size).isEqualTo(2)

        assertThatValuesFilesContainsFiles(
            valuesFiles.getValue("values"),
            "${res1.absolutePath}/values/strings.xml",
            "${res1.absolutePath}/values/strings2.xml"
        )
        assertThatValuesFilesContainsFiles(
            valuesFiles.getValue("values-es"),
            "${res1.absolutePath}/values-es/strings_es1.xml"
        )
    }

    private fun assertThatValuesFilesContainsFiles(valuesStringFiles: ValuesStringFiles, vararg filePaths: String) {
        Truth.assertThat(valuesStringFiles.filesSet.map { it.absolutePath }).containsExactlyElementsIn(filePaths)
    }

    private fun getResDirWithFolders(resDirName: String, valuesFolders: Map<String, List<String>>): File {
        val resFolder = temporaryFolder.newFolder(resDirName)
        for ((valuesName, files) in valuesFolders) {
            temporaryFolder.newFolder(resDirName, valuesName)
            for (fileName in files) {
                temporaryFolder.newFile("$resDirName/$valuesName/$fileName")
            }
        }
        return resFolder
    }
}
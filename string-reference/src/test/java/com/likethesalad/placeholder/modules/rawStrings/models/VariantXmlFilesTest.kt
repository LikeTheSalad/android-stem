package com.likethesalad.placeholder.modules.rawStrings.models

import com.google.common.truth.Truth
import com.likethesalad.placeholder.modules.rawStrings.data.helpers.dirs.VariantValuesFolders
import com.likethesalad.placeholder.modules.rawStrings.data.helpers.files.ValuesFolderXmlFiles
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class VariantXmlFilesTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `Get all values files per values folder`() {
        val res1 = getValuesDirsWithFiles(
            "res1", mapOf(
                "values" to listOf("strings.xml", "strings2.xml"),
                "values-es" to listOf("strings_es1.xml"),
                "values-pt" to emptyList()
            )
        )
        val res2 = getValuesDirsWithFiles(
            "res2", mapOf(
                "values" to listOf("strings.xml", "strings3.xml"),
                "values-it" to listOf("strings_it.xml", "strings_it2.xml")
            )
        )

        val variantXmlFiles = createVariantXmlFiles("main", res1.valuesDirs + res2.valuesDirs)

        assertThatValuesFoldersContainsFiles(
            variantXmlFiles.findValuesFolderXmlFilesByName("values")!!,
            "${res1.resDir.absolutePath}/values/strings.xml",
            "${res1.resDir.absolutePath}/values/strings2.xml",
            "${res2.resDir.absolutePath}/values/strings.xml",
            "${res2.resDir.absolutePath}/values/strings3.xml"
        )
        assertThatValuesFoldersContainsFiles(
            variantXmlFiles.findValuesFolderXmlFilesByName("values-es")!!,
            "${res1.resDir.absolutePath}/values-es/strings_es1.xml"
        )
        assertThatValuesFoldersContainsFiles(
            variantXmlFiles.findValuesFolderXmlFilesByName("values-it")!!,
            "${res2.resDir.absolutePath}/values-it/strings_it.xml",
            "${res2.resDir.absolutePath}/values-it/strings_it2.xml"
        )
    }

    @Test
    fun `Filter out generated file and non xml files`() {
        val res1 = getValuesDirsWithFiles(
            "res1", mapOf(
                "values" to listOf("strings.xml", "strings2.xml", "something.xml2"),
                "values-es" to listOf("strings_es1.xml", "strings_es.cml", "resolved.xml"),
                "elsedir" to emptyList()
            )
        )

        val variantXmlFiles = createVariantXmlFiles("client", res1.valuesDirs)

        assertThatValuesFoldersContainsFiles(
            variantXmlFiles.findValuesFolderXmlFilesByName("values")!!,
            "${res1.resDir.absolutePath}/values/strings.xml",
            "${res1.resDir.absolutePath}/values/strings2.xml"
        )
        assertThatValuesFoldersContainsFiles(
            variantXmlFiles.findValuesFolderXmlFilesByName("values-es")!!,
            "${res1.resDir.absolutePath}/values-es/strings_es1.xml"
        )
    }

    private fun createVariantXmlFiles(variantName: String, valuesFolders: List<File>): VariantXmlFiles {
        val variantValuesFolders = mockk<VariantValuesFolders>()
        every { variantValuesFolders.variantName }.returns(variantName)
        every { variantValuesFolders.valuesFolders }.returns(valuesFolders)

        return VariantXmlFiles(variantValuesFolders)
    }

    private fun assertThatValuesFoldersContainsFiles(
        valuesFolderXmlFiles: ValuesFolderXmlFiles,
        vararg filePaths: String
    ) {
        Truth.assertThat(valuesFolderXmlFiles.xmlFiles.map { it.absolutePath }).containsExactlyElementsIn(filePaths)
    }

    private fun getValuesDirsWithFiles(
        resDirName: String,
        valuesDirsSchema: Map<String, List<String>>
    ): ResDirWithValues {
        val valuesDirs = mutableListOf<File>()
        val resDir = temporaryFolder.newFolder(resDirName)
        for ((valuesName, files) in valuesDirsSchema) {
            valuesDirs.add(temporaryFolder.newFolder(resDirName, valuesName))
            for (fileName in files) {
                temporaryFolder.newFile("$resDirName/$valuesName/$fileName")
            }
        }
        return ResDirWithValues(resDir, valuesDirs)
    }

    data class ResDirWithValues(val resDir: File, val valuesDirs: List<File>)
}
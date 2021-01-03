package com.likethesalad.placeholder.data

import com.google.common.truth.Truth
import com.likethesalad.placeholder.modules.common.models.VariantResPaths
import com.likethesalad.placeholder.modules.rawStrings.data.helpers.dirs.VariantValuesFolders
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class VariantValuesFoldersTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `Get all values folders`() {
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
        val variantResPaths = VariantResPaths(variantName, resDirs)
        val resValuesFiles = VariantValuesFolders(variantResPaths)

        Truth.assertThat(resValuesFiles.variantName).isEqualTo(variantName)
        Truth.assertThat(resValuesFiles.valuesFolders.map { it.name }.toSet()).containsExactly(
            "values", "values-es",
            "values-pt", "values-it"
        )
    }

    @Test
    fun `Get only values folders`() {
        val variantName = "client"
        val res1 = getResDirWithFolders(
            "res1", mapOf(
                "values" to listOf("strings.xml", "strings2.xml", "something.xml2"),
                "values-es" to listOf("strings_es1.xml", "strings_es.cml", "resolved.xml"),
                "elsedir" to emptyList()
            )
        )
        val resDirs = setOf(res1)
        val variantResPaths = VariantResPaths(variantName, resDirs)
        val resValuesFiles = VariantValuesFolders(variantResPaths)

        Truth.assertThat(resValuesFiles.variantName).isEqualTo(variantName)
        Truth.assertThat(resValuesFiles.valuesFolders.map { it.name }.toSet()).containsExactly(
            "values", "values-es"
        )
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
package com.likethesalad.placeholder.models

import com.google.common.truth.Truth
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ValuesResHelperTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `Get all unique language values folder names`() {
        val resDirs = listOf(
            getDirWithValuesFolders("src", "values", "values-es", "values-it"),
            getDirWithValuesFolders("src2", "values", "values-en", "values-pt"),
            getDirWithValuesFolders("src3", "values", "values-es", "values-fr")
        )
        val valuesResHelper = ValuesResHelper()

        val result = valuesResHelper.getUniqueLanguageValuesDirName(resDirs)

        Truth.assertThat(result).containsExactly(
            "values-es", "values-en", "values-it", "values-pt", "values-fr"
        )
    }

    private fun getDirWithValuesFolders(dirName: String, vararg valuesFolderNames: String): File {
        val dir = temporaryFolder.newFolder(dirName)
        for (valuesFolderName in valuesFolderNames) {
            temporaryFolder.newFolder(dirName, valuesFolderName)
        }
        return dir
    }
}
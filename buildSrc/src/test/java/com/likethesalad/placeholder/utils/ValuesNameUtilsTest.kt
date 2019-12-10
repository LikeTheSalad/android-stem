package com.likethesalad.placeholder.utils

import com.google.common.truth.Truth
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ValuesNameUtilsTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `Has value folder name format`() {
        verifyValidName("values", true)
        verifyValidName("vallues", false)
        verifyValidName("values-es", true)
        verifyValidName("values-es-rES", true)
        verifyValidName("values-it", true)
    }

    @Test
    fun `Get all unique language values folder names`() {
        val resDirs = setOf(
            getDirWithValuesFolders("src", "values", "values-es", "values-it"),
            getDirWithValuesFolders("src2", "values", "values-en", "values-pt"),
            getDirWithValuesFolders("src3", "values", "values-es", "values-fr")
        )
        val result = ValuesNameUtils.getUniqueValuesDirName(resDirs)

        Truth.assertThat(result).containsExactly(
            "values", "values-es", "values-en", "values-it", "values-pt", "values-fr"
        )
    }

    private fun getDirWithValuesFolders(dirName: String, vararg valuesFolderNames: String): File {
        val dir = temporaryFolder.newFolder(dirName)
        for (valuesFolderName in valuesFolderNames) {
            temporaryFolder.newFolder(dirName, valuesFolderName)
        }
        return dir
    }

    private fun verifyValidName(valuesFolderName: String, shouldBeValid: Boolean) {
        Truth.assertThat(ValuesNameUtils.isValueName(valuesFolderName)).isEqualTo(shouldBeValid)
    }
}
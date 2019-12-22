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
    fun `Get all valid values folders`() {
        val resDirs = setOf(
            getDirWithValuesFolders(
                "src", "values", "values-es", "values-it",
                "novalues"
            ),
            getDirWithValuesFolders("src2", "values", "values-en", "values-pt"),
            getDirWithValuesFolders(
                "src3", "values", "values-es", "something",
                "values-fr"
            )
        )
        val result = ValuesNameUtils.getValuesFolders(resDirs)

        assertContainsExactlyValuesFolder(
            result,
            "src/values",
            "src/values-es",
            "src/values-it",
            "src2/values",
            "src2/values-en",
            "src2/values-pt",
            "src3/values",
            "src3/values-es",
            "src3/values-fr"
        )
    }

    @Test
    fun `Get suffix from values folder name`() {
        verifyValidSuffix("values", "")
        verifyValidSuffix("values-es", "-es")
        verifyValidSuffix("values-es-rES", "-es-rES")
    }

    private fun assertContainsExactlyValuesFolder(
        folders: List<File>,
        vararg valuesFolderPaths: String
    ) {
        val fullValuesFolderPaths = valuesFolderPaths.map { "${temporaryFolder.root.absolutePath}/$it" }
        Truth.assertThat(folders.map { it.absolutePath }).containsExactlyElementsIn(fullValuesFolderPaths)
    }

    private fun verifyValidSuffix(valuesFolderName: String, expectedSuffix: String) {
        Truth.assertThat(ValuesNameUtils.getValuesNameSuffix(valuesFolderName)).isEqualTo(expectedSuffix)
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
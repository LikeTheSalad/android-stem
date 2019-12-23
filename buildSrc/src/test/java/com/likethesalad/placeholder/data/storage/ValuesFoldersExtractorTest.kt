package com.likethesalad.placeholder.data.storage

import com.google.common.truth.Truth
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ValuesFoldersExtractorTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

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
        val extractor = ValuesFoldersExtractor(resDirs)

        assertContainsExactlyValuesFolder(
            extractor.getValuesFolders(),
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

    private fun assertContainsExactlyValuesFolder(
        folders: List<File>,
        vararg valuesFolderPaths: String
    ) {
        val fullValuesFolderPaths = valuesFolderPaths.map { "${temporaryFolder.root.absolutePath}/$it" }
        Truth.assertThat(folders.map { it.absolutePath }).containsExactlyElementsIn(fullValuesFolderPaths)
    }

    private fun getDirWithValuesFolders(dirName: String, vararg valuesFolderNames: String): File {
        val dir = temporaryFolder.newFolder(dirName)
        for (valuesFolderName in valuesFolderNames) {
            temporaryFolder.newFolder(dirName, valuesFolderName)
        }
        return dir
    }

}
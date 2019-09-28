package com.likethesalad.placeholder.models.raw

import com.google.common.truth.Truth
import io.mockk.mockk
import org.junit.Test
import java.io.File

class FlavorValuesRawFilesTest {

    @Test
    fun check_valuesFolderName_and_flavorName_setup() {
        // Given:
        val valuesName = "values"
        val flavorName = "demo"

        // When:
        val flavorValuesRawFiles = FlavorValuesRawFiles(flavorName, valuesName, mockk(), mockk())

        // Then:
        Truth.assertThat(flavorValuesRawFiles.flavorName).isEqualTo(flavorName)
        Truth.assertThat(flavorValuesRawFiles.valuesFolderName).isEqualTo(valuesName)
    }

    @Test
    fun check_getRawFilesMetaList() {
        // Given:
        val mainFiles = listOf<File>(mockk(), mockk(), mockk())
        val complementaryFiles = listOf<File>(mockk(), mockk())

        // When:
        val flavorValuesRawFiles = FlavorValuesRawFiles(
            "demo", "values",
            complementaryFiles, mainFiles
        )

        // Then:
        Truth.assertThat(flavorValuesRawFiles.getRawFilesMetaList()).containsExactly(complementaryFiles, mainFiles)
            .inOrder()
    }
}
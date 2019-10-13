package com.likethesalad.placeholder.models.raw

import com.google.common.truth.Truth
import io.mockk.mockk
import org.junit.Test
import java.io.File

class FlavorValuesRawFilesTest {

    @Test
    fun check_valuesFolderName_and_flavorName_setup() {
        // Given:
        val flavorName = "demo"

        // When:
        val flavorValuesRawFiles = FlavorValuesRawFiles(flavorName, "", mockk(), mockk())

        // Then:
        Truth.assertThat(flavorValuesRawFiles.flavorName).isEqualTo(flavorName)
        Truth.assertThat(flavorValuesRawFiles.suffix).isEmpty()
    }

    @Test
    fun check_getRawFilesMetaList() {
        // Given:
        val mainFiles = listOf<File>(mockk(), mockk(), mockk())
        val complementaryFiles = listOf<File>(mockk(), mockk())

        // When:
        val flavorValuesRawFiles = FlavorValuesRawFiles(
            "demo", "",
            complementaryFiles, mainFiles
        )

        // Then:
        Truth.assertThat(flavorValuesRawFiles.getRawFilesMetaList()).containsExactly(complementaryFiles, mainFiles)
            .inOrder()
    }
}
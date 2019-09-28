package com.likethesalad.placeholder.models.raw

import com.google.common.truth.Truth
import io.mockk.mockk
import org.junit.Test
import java.io.File

class MainValuesRawFilesTest {

    @Test
    fun check_valuesFolderName_and_flavorName_setup() {
        // Given:
        val valuesName = "values"

        // When:
        val mainValuesRawFiles = MainValuesRawFiles(valuesName, mockk())

        // Then:
        Truth.assertThat(mainValuesRawFiles.valuesFolderName).isEqualTo(valuesName)
    }

    @Test
    fun check_getRawFilesMetaList() {
        // Given:
        val mainFiles = listOf<File>(mockk(), mockk(), mockk())

        // When:
        val mainValuesRawFiles = MainValuesRawFiles("values", mainFiles)

        // Then:
        Truth.assertThat(mainValuesRawFiles.getRawFilesMetaList()).containsExactly(mainFiles)
    }
}
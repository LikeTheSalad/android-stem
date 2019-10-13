package com.likethesalad.placeholder.models.raw

import com.google.common.truth.Truth
import io.mockk.mockk
import org.junit.Test
import java.io.File

class MainValuesRawFilesTest {

    @Test
    fun check_valuesFolderName_and_flavorName_setup() {
        // When:
        val mainValuesRawFiles = MainValuesRawFiles("", mockk())

        // Then:
        Truth.assertThat(mainValuesRawFiles.suffix).isEmpty()
    }

    @Test
    fun check_getRawFilesMetaList() {
        // Given:
        val mainFiles = listOf<File>(mockk(), mockk(), mockk())

        // When:
        val mainValuesRawFiles = MainValuesRawFiles("", mainFiles)

        // Then:
        Truth.assertThat(mainValuesRawFiles.getRawFilesMetaList()).containsExactly(mainFiles)
    }
}
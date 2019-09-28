package com.likethesalad.placeholder.models

import com.google.common.truth.Truth
import io.mockk.mockk
import org.junit.Test
import java.io.File

class ResDirsTest {

    @Test
    fun check_hasFlavorDirs_false() {
        // Given
        val mainDirs = setOf<File>(mockk())
        val flavorDirs = setOf<File>()

        // When
        val resDirs = ResDirs(mainDirs, flavorDirs)

        // Then
        Truth.assertThat(resDirs.hasFlavorDirs).isFalse()
    }

    @Test
    fun check_hasFlavorDirs_true() {
        // Given
        val mainDirs = setOf<File>(mockk())
        val flavorDirs = setOf<File>(mockk())

        // When
        val resDirs = ResDirs(mainDirs, flavorDirs)

        // Then
        Truth.assertThat(resDirs.hasFlavorDirs).isTrue()
    }
}
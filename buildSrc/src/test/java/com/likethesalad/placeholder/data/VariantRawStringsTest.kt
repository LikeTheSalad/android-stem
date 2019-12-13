package com.likethesalad.placeholder.data

import com.google.common.truth.Truth
import io.mockk.mockk
import org.junit.Test

class VariantRawStringsTest {

    @Test
    fun `Get raw strings for unflavored variant`() {
        val resolvedPath = listOf("main", "debug")
        val variantDirsPathFinder = mockk<VariantDirsPathFinder>()
//        variantDirsPathFinder.getExistingPathsResDirs()

        val variantRawStrings = VariantRawStrings()

        Truth.assertThat(variantRawStrings.getValuesStrings())
    }
}
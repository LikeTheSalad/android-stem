package com.likethesalad.placeholder.models

import com.google.common.truth.Truth
import org.junit.Test

class StringsGatheredModelTest {
    @Test
    fun check_getMergedStrings_override_complementary() {
        // Given
        val complementaryStr1 = StringResourceModel("name1", "complementary content")
        val complementaryStr2 = StringResourceModel("name2", "complementary content2")
        val mainStr1 = StringResourceModel("name1", "main content")
        val mainStr2 = StringResourceModel("name3", "main content3")

        // When
        val stringsGatheredModel = StringsGatheredModel(
            "values", listOf(mainStr1, mainStr2),
            listOf(listOf(complementaryStr1), listOf(complementaryStr2))
        )

        // Then
        Truth.assertThat(stringsGatheredModel.getMergedStrings()).containsExactly(
            "name1", mainStr1,
            "name2", complementaryStr2,
            "name3", mainStr2
        )
    }
}
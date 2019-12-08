package com.likethesalad.placeholder.utils

import com.google.common.truth.Truth
import org.junit.Test

class ValuesNameUtilsTest {

    @Test
    fun `Has value folder name format`() {
        verifyValidName("values", true)
        verifyValidName("vallues", false)
        verifyValidName("values-es", true)
        verifyValidName("values-es-rES", true)
        verifyValidName("values-it", true)
    }

    private fun verifyValidName(valuesFolderName: String, shouldBeValid: Boolean) {
        Truth.assertThat(ValuesNameUtils.isValueName(valuesFolderName)).isEqualTo(shouldBeValid)
    }
}
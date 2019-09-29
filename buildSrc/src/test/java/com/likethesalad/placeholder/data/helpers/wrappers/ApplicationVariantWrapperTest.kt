package com.likethesalad.placeholder.data.helpers.wrappers

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.helpers.wrappers.testutils.TestApplicationVariant
import org.junit.Before
import org.junit.Test

class ApplicationVariantWrapperTest {

    private lateinit var applicationVariantWrapper: ApplicationVariantWrapper
    private val name = "theName"
    private val flavor = "the"

    @Before
    fun setUp() {
        val testApplicationVariant = TestApplicationVariant(name, flavor)
        applicationVariantWrapper = ApplicationVariantWrapper(testApplicationVariant)
    }

    @Test
    fun check_getName() {
        // When:
        val result = applicationVariantWrapper.getName()

        // Then:
        Truth.assertThat(result).isEqualTo(name)
    }

    @Test
    fun check_getFlavorName() {
        // When:
        val result = applicationVariantWrapper.getFlavorName()

        // Then:
        Truth.assertThat(result).isEqualTo(flavor)
    }
}
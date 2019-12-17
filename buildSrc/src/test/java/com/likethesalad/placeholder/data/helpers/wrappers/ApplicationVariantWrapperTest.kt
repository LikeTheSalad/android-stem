package com.likethesalad.placeholder.data.helpers.wrappers

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.helpers.wrappers.testutils.TestAndroidBuildType
import com.likethesalad.placeholder.data.helpers.wrappers.testutils.TestApplicationVariant
import com.likethesalad.placeholder.data.helpers.wrappers.testutils.TestProductFlavor
import org.junit.Before
import org.junit.Test

class ApplicationVariantWrapperTest {

    private lateinit var applicationVariantWrapper: ApplicationVariantWrapper
    private val name = "theName"
    private val flavor = "the"
    private val productFlavors = listOf(
        TestProductFlavor("one"),
        TestProductFlavor("two")
    )

    @Before
    fun setUp() {
        val testApplicationVariant = TestApplicationVariant(
            name, flavor, productFlavors,
            TestAndroidBuildType("debug")
        )
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

    @Test
    fun check_getProductFlavors() {
        // When:
        val result = applicationVariantWrapper.getProductFlavors()

        // Then:
        Truth.assertThat(result).isEqualTo(productFlavors.map { ProductFlavorWrapper(it) })
    }

    @Test
    fun check_getBuildType() {
        // When:
        val result = applicationVariantWrapper.getBuildType()

        // Then:
        Truth.assertThat(applicationVariantWrapper.getBuildType().getName()).isEqualTo("debug")
    }
}
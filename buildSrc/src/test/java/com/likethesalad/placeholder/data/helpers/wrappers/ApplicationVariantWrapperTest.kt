package com.likethesalad.placeholder.data.helpers.wrappers

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.helpers.wrappers.testutils.TestAndroidBuildType
import com.likethesalad.placeholder.data.helpers.wrappers.testutils.TestApplicationVariant
import com.likethesalad.placeholder.data.helpers.wrappers.testutils.TestProductFlavor
import io.mockk.mockk
import org.gradle.api.artifacts.Configuration
import org.junit.Before
import org.junit.Test

class ApplicationVariantWrapperTest {

    private lateinit var applicationVariantWrapper: ApplicationVariantWrapper
    private lateinit var runtimeConfiguration: Configuration
    private val name = "theName"
    private val flavor = "the"
    private val productFlavors = listOf(
        TestProductFlavor("one"),
        TestProductFlavor("two")
    )

    @Before
    fun setUp() {
        runtimeConfiguration = mockk()
        val testApplicationVariant = TestApplicationVariant(
            name, flavor, productFlavors,
            TestAndroidBuildType("debug"),
            runtimeConfiguration
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
        Truth.assertThat(applicationVariantWrapper.getBuildType().getName()).isEqualTo("debug")
    }

    @Test
    fun check_getRuntimeConfiguration() {
        // When:
        Truth.assertThat(applicationVariantWrapper.getRuntimeConfiguration()).isEqualTo(runtimeConfiguration)
    }
}
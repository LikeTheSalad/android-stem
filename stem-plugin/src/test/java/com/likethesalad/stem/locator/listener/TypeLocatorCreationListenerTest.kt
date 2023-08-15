package com.likethesalad.stem.locator.listener

import com.google.common.truth.Truth
import com.likethesalad.tools.agpcompat.api.bridges.AndroidVariantData
import com.likethesalad.tools.resource.collector.android.data.variant.VariantTree
import com.likethesalad.tools.resource.locator.android.extension.configuration.data.ResourceLocatorInfo
import com.likethesalad.tools.testing.BaseMockable
import io.mockk.every
import io.mockk.verify
import org.junit.Assert.fail
import org.junit.Test

class TypeLocatorCreationListenerTest : BaseMockable() {

    @Test
    fun `Notify callback when all locators of all defined types of same variant are ready`() {
        val type1 = "type1"
        val type2 = "type2"
        val variant1 = mockk<VariantTree>()
        val locatorType1 = mockk<ResourceLocatorInfo>()
        val locatorType2 = mockk<ResourceLocatorInfo>()
        val callback = mockk<TypeLocatorCreationListener.Callback>()
        val instance = TypeLocatorCreationListener(setOf(type1, type2), callback)

        instance.onLocatorReady(type1, variant1, locatorType1)
        instance.onLocatorReady(type2, variant1, locatorType2)

        verify {
            callback.onLocatorsReady(
                variant1, mapOf(
                    type1 to locatorType1,
                    type2 to locatorType2
                )
            )
        }
    }

    @Test
    fun `Do not notify if all locators but from different variant are added`() {
        val type1 = "type1"
        val type2 = "type2"
        val variant1 = mockk<VariantTree>()
        val variant2 = mockk<VariantTree>()
        val locatorType1 = mockk<ResourceLocatorInfo>()
        val locatorType2 = mockk<ResourceLocatorInfo>()
        val callback = mockk<TypeLocatorCreationListener.Callback>()
        val instance = TypeLocatorCreationListener(setOf(type1, type2), callback)

        instance.onLocatorReady(type1, variant1, locatorType1)
        instance.onLocatorReady(type2, variant2, locatorType2)

        verify(exactly = 0) {
            callback.onLocatorsReady(
                any(), any()
            )
        }
    }

    @Test
    fun `Throw error if more locators than expected are tried to be created per variant`() {
        val type1 = "type1"
        val type2 = "type2"
        val variantName = "someVariant"
        val variant1 = createVariantTree(variantName)
        val locatorType1 = mockk<ResourceLocatorInfo>()
        val locatorType2 = mockk<ResourceLocatorInfo>()
        val callback = mockk<TypeLocatorCreationListener.Callback>()
        val instance = TypeLocatorCreationListener(setOf(type1, type2), callback)

        instance.onLocatorReady(type1, variant1, locatorType1)
        instance.onLocatorReady(type2, variant1, locatorType2)

        try {
            instance.onLocatorReady(type2, variant1, locatorType2)
            fail()
        } catch (e: IllegalStateException) {
            Truth.assertThat(e.message).isEqualTo("Variant already completed: $variantName")
        }
    }

    private fun createVariantTree(variantName: String): VariantTree {
        val variantTree = mockk<VariantTree>()
        val variantData = mockk<AndroidVariantData>()
        every { variantData.getVariantName() }.returns(variantName)
        every { variantTree.androidVariantData }.returns(variantData)

        return variantTree
    }
}
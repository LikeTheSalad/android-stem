package com.likethesalad.placeholder.data.helpers.wrappers

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.helpers.wrappers.testutils.TestProductFlavor
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class ProductFlavorWrapperTest {

    @Test
    fun `Get name`() {
        val testProductFlavor = mockk<TestProductFlavor>()
        every { testProductFlavor.getName() }.returns("the name")

        val productFlavorWrapper = ProductFlavorWrapper(testProductFlavor)

        Truth.assertThat(productFlavorWrapper.getName()).isEqualTo("the name")
        verify { testProductFlavor.getName() }
    }
}
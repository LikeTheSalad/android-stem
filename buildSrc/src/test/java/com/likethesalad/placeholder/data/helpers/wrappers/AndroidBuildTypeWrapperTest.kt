package com.likethesalad.placeholder.data.helpers.wrappers

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.helpers.wrappers.testutils.TestAndroidBuildType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class AndroidBuildTypeWrapperTest {

    @Test
    fun `Get name`() {
        val testAndroidBuildType = mockk<TestAndroidBuildType>()
        every { testAndroidBuildType.getName() }.returns("some name")

        val androidBuildTypeWrapper = AndroidBuildTypeWrapper(testAndroidBuildType)

        Truth.assertThat(androidBuildTypeWrapper.getName()).isEqualTo("some name")
        verify { testAndroidBuildType.getName() }
    }
}
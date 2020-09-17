package com.likethesalad.placeholder.data.helpers.wrappers

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.helpers.wrappers.testutils.TestAndroidSourceDirectorySet
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Test
import java.io.File

class AndroidSourceDirectorySetWrapperTest {

    @Test
    fun check_getSrcDirs() {
        // Given:
        val dirs = setOf<File>(mockk(), mockk())
        val testAndroidSourceDirectorySet = TestAndroidSourceDirectorySet(dirs)

        // When:
        val wrapper = AndroidSourceDirectorySetWrapper(testAndroidSourceDirectorySet)
        val result = wrapper.getSrcDirs()

        // Then:
        Truth.assertThat(result).isEqualTo(dirs)
    }

    @Test
    fun check_setSrcDirs() {
        // Given:
        val dirs = setOf<File>(mockk(), mockk())
        val dirsCaptor = slot<Set<File>>()
        val testAndroidSourceDirectorySet = mockk<TestAndroidSourceDirectorySet>(relaxUnitFun = true)

        // When:
        val wrapper = AndroidSourceDirectorySetWrapper(testAndroidSourceDirectorySet)
        wrapper.setSrcDirs(dirs)

        // Then:
        verify { testAndroidSourceDirectorySet.setSrcDirs(capture(dirsCaptor)) }
        Truth.assertThat(dirsCaptor.captured).isEqualTo(dirs)
    }
}
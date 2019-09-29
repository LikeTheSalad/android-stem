package com.likethesalad.placeholder.data.helpers.wrappers

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.helpers.wrappers.testutils.TestAndroidSourceSet
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import java.io.File

class AndroidSourceSetWrapperTest {

    private lateinit var androidSourceSetWrapper: AndroidSourceSetWrapper
    private val sourceSetName = "values"
    private val sourceSetFiles = setOf<File>(mockk(), mockk())

    @Before
    fun setUp() {
        val testAndroidSourceSet = TestAndroidSourceSet(sourceSetName, sourceSetFiles)
        androidSourceSetWrapper = AndroidSourceSetWrapper(testAndroidSourceSet)
    }

    @Test
    fun check_getRes() {
        // When:
        val result = androidSourceSetWrapper.getRes()

        // Then:
        Truth.assertThat(result.getSrcDirs()).isEqualTo(sourceSetFiles)
    }

    @Test
    fun check_getName() {
        // When:
        val result = androidSourceSetWrapper.getName()

        // Then:
        Truth.assertThat(result).isEqualTo(sourceSetName)
    }
}
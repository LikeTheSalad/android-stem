package com.likethesalad.placeholder.data.helpers.wrappers

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.helpers.wrappers.testutils.TestAndroidExtension
import com.likethesalad.placeholder.data.helpers.wrappers.testutils.TestAndroidSourceSet
import com.likethesalad.placeholder.data.helpers.wrappers.testutils.TestApplicationVariant
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import java.io.File

class AndroidExtensionWrapperTest {

    private lateinit var androidExtensionWrapper: AndroidExtensionWrapper
    private lateinit var testAndroidExtension: TestAndroidExtension
    private val sourceSetsName = "values"
    private val sourceSetFiles = setOf<File>(
        mockk(), mockk(), mockk()
    )
    private val appVariants = setOf(
        TestApplicationVariant("demoDebug", "demo"),
        TestApplicationVariant("paidDebug", "paid")
    )

    @Before
    fun setUp() {
        val sourceSets = setOf(
            TestAndroidSourceSet(sourceSetsName, sourceSetFiles)
        )
        testAndroidExtension = TestAndroidExtension(sourceSets, appVariants)
        androidExtensionWrapper = AndroidExtensionWrapper(testAndroidExtension)
    }

    @Test
    fun check_getSourceSets() {
        // When:
        val sourceSets = androidExtensionWrapper.getSourceSets()

        // Then:
        Truth.assertThat(sourceSets.keys).containsExactly(sourceSetsName)

        val androidSourceSetWrapper = sourceSets.getValue(sourceSetsName)
        val androidSourceDirectorySetWrapper = androidSourceSetWrapper.getRes()

        Truth.assertThat(androidSourceSetWrapper.getName()).isEqualTo(sourceSetsName)
        Truth.assertThat(androidSourceDirectorySetWrapper.getSrcDirs()).isEqualTo(sourceSetFiles)
    }

    @Test
    fun check_getApplicationVariants() {
        // When:
        val result: Set<ApplicationVariantWrapper> = androidExtensionWrapper.getApplicationVariants()

        // Then:
        Truth.assertThat(result.size).isEqualTo(2)
        val first = result.elementAt(0)
        val second = result.elementAt(1)
        Truth.assertThat(first.getName()).isEqualTo("demoDebug")
        Truth.assertThat(first.getFlavorName()).isEqualTo("demo")
        Truth.assertThat(second.getName()).isEqualTo("paidDebug")
        Truth.assertThat(second.getFlavorName()).isEqualTo("paid")
    }
}
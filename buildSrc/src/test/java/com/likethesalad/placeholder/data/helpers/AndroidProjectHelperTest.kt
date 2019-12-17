package com.likethesalad.placeholder.data.helpers

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.helpers.wrappers.testutils.TestAndroidBuildType
import com.likethesalad.placeholder.data.helpers.wrappers.testutils.TestAndroidExtension
import com.likethesalad.placeholder.data.helpers.wrappers.testutils.TestAndroidSourceSet
import com.likethesalad.placeholder.data.helpers.wrappers.testutils.TestApplicationVariant
import io.mockk.every
import io.mockk.mockk
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.junit.Before
import org.junit.Test
import java.io.File

class AndroidProjectHelperTest {

    private lateinit var androidProjectHelper: AndroidProjectHelper
    private lateinit var project: Project

    @Before
    fun setUp() {
        project = mockk()
        androidProjectHelper = AndroidProjectHelper(project)
    }

    @Test
    fun check_androidExtension() {
        // Given:
        val sourceSetFiles = setOf<File>(mockk())
        val sourceSets = setOf(
            TestAndroidSourceSet("values", sourceSetFiles)
        )
        val appVariants = setOf(
            TestApplicationVariant(
                "demoDebug", "demo", emptyList(),
                TestAndroidBuildType("debug")
            )
        )
        val androidExtension = TestAndroidExtension(sourceSets, appVariants)
        val extensions = mockk<ExtensionContainer>()
        every { extensions.getByName("android") }.returns(androidExtension)
        every { project.extensions }.returns(extensions)

        // When:
        val result = androidProjectHelper.androidExtension

        // Then:
        // Source sets:
        val resultSourceSets = result.getSourceSets()
        Truth.assertThat(resultSourceSets.size).isEqualTo(1)
        Truth.assertThat(resultSourceSets).containsKey("values")
        val androidSourceSetWrapper = resultSourceSets.entries.first().value
        Truth.assertThat(androidSourceSetWrapper.getName()).isEqualTo("values")
        Truth.assertThat(androidSourceSetWrapper.getRes().getSrcDirs()).isEqualTo(sourceSetFiles)

        // App variants:
        val resultVariants = result.getApplicationVariants()
        Truth.assertThat(resultVariants.size).isEqualTo(1)
        val appVariant = resultVariants.first()
        Truth.assertThat(appVariant.getName()).isEqualTo("demoDebug")
        Truth.assertThat(appVariant.getFlavorName()).isEqualTo("demo")
    }

    @Test
    fun `Get project src dir path`() {
        val srcDirPath = "/some/path/to/src"
        every { }
    }
}
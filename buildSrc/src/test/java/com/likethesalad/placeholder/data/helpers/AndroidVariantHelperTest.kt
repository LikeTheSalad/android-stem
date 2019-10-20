package com.likethesalad.placeholder.data.helpers

import com.google.common.truth.Truth
import com.likethesalad.placeholder.data.helpers.wrappers.AndroidExtensionWrapper
import com.likethesalad.placeholder.data.helpers.wrappers.AndroidSourceDirectorySetWrapper
import com.likethesalad.placeholder.data.helpers.wrappers.AndroidSourceSetWrapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.junit.Before
import org.junit.Test
import java.io.File

class AndroidVariantHelperTest {

    private lateinit var projectHelper: AndroidProjectHelper
    private val buildVariant = "demoDebug"
    private val flavor = "demo"

    @Before
    fun setUp() {
        projectHelper = mockk()
    }

    @Test
    fun check_isFlavored_true() {
        // Given:
        val androidVariantHelper = getFlavoredAndroidVariantHelper()

        // Then:
        Truth.assertThat(androidVariantHelper.isFlavored).isTrue()
    }

    @Test
    fun check_isFlavored_false() {
        // Given:
        val androidVariantHelper = getNonFlavoredAndroidVariantHelper()

        // Then:
        Truth.assertThat(androidVariantHelper.isFlavored).isFalse()
    }

    @Test
    fun check_taskNames_setup() {
        // Given:
        val androidVariantHelper = getFlavoredAndroidVariantHelper()

        // Then:
        Truth.assertThat(androidVariantHelper.tasksNames.buildVariant).isEqualTo(buildVariant)
    }

    @Test
    fun check_generateResValuesTask() {
        // Given:
        val projectMock = mockk<Project>()
        val taskContainerMock = mockk<TaskContainer>()
        val taskMock = mockk<Task>()
        every { projectMock.tasks }.returns(taskContainerMock)
        every { projectHelper.project }.returns(projectMock)
        val androidVariantHelper = getNonFlavoredAndroidVariantHelper()
        every { taskContainerMock.findByName(androidVariantHelper.tasksNames.generateResValuesName) }.returns(taskMock)

        // When:
        val result = androidVariantHelper.generateResValuesTask

        // Then:
        Truth.assertThat(result).isEqualTo(taskMock)
        verify { taskContainerMock.findByName(androidVariantHelper.tasksNames.generateResValuesName) }
    }

    @Test
    fun check_mergeResourcesTask() {
        // Given:
        val projectMock = mockk<Project>()
        val taskContainerMock = mockk<TaskContainer>()
        val taskMock = mockk<Task>()
        every { projectMock.tasks }.returns(taskContainerMock)
        every { projectHelper.project }.returns(projectMock)
        val androidVariantHelper = getNonFlavoredAndroidVariantHelper()
        every { taskContainerMock.findByName(androidVariantHelper.tasksNames.mergeResourcesName) }.returns(taskMock)

        // When:
        val result = androidVariantHelper.mergeResourcesTask

        // Then:
        Truth.assertThat(result).isEqualTo(taskMock)
        verify { taskContainerMock.findByName(androidVariantHelper.tasksNames.mergeResourcesName) }
    }

    @Test
    fun check_resourceDirs() {
        // Given:
        val files = setOf<File>(mockk(), mockk())
        val sourceSetsMap = mapOf("main" to setUpSourceSetsWrapper("main", files))
        val androidExtensionWrapper = mockk<AndroidExtensionWrapper>()
        every { androidExtensionWrapper.getSourceSets() }.returns(sourceSetsMap)
        every { projectHelper.androidExtension }.returns(androidExtensionWrapper)
        val androidVariantHelper = getNonFlavoredAndroidVariantHelper()

        // When:
        val result = androidVariantHelper.resourceDirs

        // Then:
        Truth.assertThat(result.hasFlavorDirs).isFalse()
        Truth.assertThat(result.flavorDirs).isEmpty()
        Truth.assertThat(result.mainDirs).containsExactlyElementsIn(files)
    }

    @Test
    fun check_resourceDirs_with_flavor() {
        // Given:
        val files = setOf<File>(mockk(), mockk())
        val flavorFiles = setOf<File>(mockk())
        val sourceSetsMap = mapOf(
            "main" to setUpSourceSetsWrapper("main", files),
            flavor to setUpSourceSetsWrapper(flavor, flavorFiles)
        )
        val androidExtensionWrapper = mockk<AndroidExtensionWrapper>()
        every { androidExtensionWrapper.getSourceSets() }.returns(sourceSetsMap)
        every { projectHelper.androidExtension }.returns(androidExtensionWrapper)
        val androidVariantHelper = getFlavoredAndroidVariantHelper()

        // When:
        val result = androidVariantHelper.resourceDirs

        // Then:
        Truth.assertThat(result.hasFlavorDirs).isTrue()
        Truth.assertThat(result.flavorDirs).containsExactlyElementsIn(flavorFiles)
        Truth.assertThat(result.mainDirs).containsExactlyElementsIn(files)
    }

    @Test
    fun check_incrementalDir() {
        // Given:
        val projectMock = mockk<Project>()
        val buildDirMock = mockk<File>()
        val buildDirPath = "/some/dir"
        every { buildDirMock.absolutePath }.returns(buildDirPath)
        every { projectMock.buildDir }.returns(buildDirMock)
        every { projectHelper.project }.returns(projectMock)
        val androidVariantHelper = getNonFlavoredAndroidVariantHelper()

        // When:
        val result = androidVariantHelper.incrementalDir

        // Then:
        Truth.assertThat(result).isEqualTo("$buildDirPath/intermediates/incremental/resolveDebugPlaceholders")
    }

    @Test
    fun check_incrementalDir_with_flavor() {
        // Given:
        val projectMock = mockk<Project>()
        val buildDirMock = mockk<File>()
        val buildDirPath = "/some/dir"
        every { buildDirMock.absolutePath }.returns(buildDirPath)
        every { projectMock.buildDir }.returns(buildDirMock)
        every { projectHelper.project }.returns(projectMock)
        val androidVariantHelper = getFlavoredAndroidVariantHelper()

        // When:
        val result = androidVariantHelper.incrementalDir

        // Then:
        Truth.assertThat(result).isEqualTo("$buildDirPath/intermediates/incremental/resolveDemoDebugPlaceholders")
    }

    private fun setUpSourceSetsWrapper(
        name: String,
        files: Set<File> = setOf(mockk(), mockk())
    ): AndroidSourceSetWrapper {
        val sourceSetWrapper = mockk<AndroidSourceSetWrapper>()
        val sourceDirectorySetWrapper = mockk<AndroidSourceDirectorySetWrapper>()
        every { sourceSetWrapper.getRes() }.returns(sourceDirectorySetWrapper)
        every { sourceDirectorySetWrapper.getSrcDirs() }.returns(files)
        every { sourceSetWrapper.getName() }.returns(name)
        return sourceSetWrapper
    }

    private fun getFlavoredAndroidVariantHelper(): AndroidVariantHelper {
        return AndroidVariantHelper(projectHelper, buildVariant, flavor)
    }

    private fun getNonFlavoredAndroidVariantHelper(): AndroidVariantHelper {
        return AndroidVariantHelper(projectHelper, "debug", "")
    }
}
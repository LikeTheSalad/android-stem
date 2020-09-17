package com.likethesalad.placeholder.data.helpers

import com.google.common.truth.Truth
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

    @Before
    fun setUp() {
        projectHelper = mockk()
    }

    @Test
    fun check_taskNames_setup() {
        // Given:
        val androidVariantHelper = getAndroidVariantHelper()

        // Then:
        Truth.assertThat(androidVariantHelper.tasksNames.buildVariant).isEqualTo(buildVariant)
    }

    @Test
    fun check_mergeResourcesTask() {
        // Given:
        val projectMock = mockk<Project>()
        val taskContainerMock = mockk<TaskContainer>()
        val taskMock = mockk<Task>()
        every { projectMock.tasks }.returns(taskContainerMock)
        every { projectHelper.project }.returns(projectMock)
        val androidVariantHelper = getAndroidVariantHelper()
        every { taskContainerMock.findByName(androidVariantHelper.tasksNames.mergeResourcesName) }.returns(taskMock)

        // When:
        val result = androidVariantHelper.mergeResourcesTask

        // Then:
        Truth.assertThat(result).isEqualTo(taskMock)
        verify { taskContainerMock.findByName(androidVariantHelper.tasksNames.mergeResourcesName) }
    }

    @Test
    fun check_generateResValuesTask() {
        // Given:
        val projectMock = mockk<Project>()
        val taskContainerMock = mockk<TaskContainer>()
        val taskMock = mockk<Task>()
        every { projectMock.tasks }.returns(taskContainerMock)
        every { projectHelper.project }.returns(projectMock)
        val androidVariantHelper = getAndroidVariantHelper()
        every { taskContainerMock.findByName(androidVariantHelper.tasksNames.generateResValuesName) }.returns(taskMock)

        // When:
        val result = androidVariantHelper.generateResValuesTask

        // Then:
        Truth.assertThat(result).isEqualTo(taskMock)
        verify { taskContainerMock.findByName(androidVariantHelper.tasksNames.generateResValuesName) }
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
        val androidVariantHelper = getAndroidVariantHelper()

        // When:
        val result = androidVariantHelper.incrementalDir

        // Then:
        Truth.assertThat(result).isEqualTo("$buildDirPath/intermediates/incremental/resolveDemoDebugPlaceholders")
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
        val androidVariantHelper = getAndroidVariantHelper()

        // When:
        val result = androidVariantHelper.incrementalDir

        // Then:
        Truth.assertThat(result).isEqualTo("$buildDirPath/intermediates/incremental/resolveDemoDebugPlaceholders")
    }

    private fun getAndroidVariantHelper(): AndroidVariantHelper {
        return AndroidVariantHelper(projectHelper, buildVariant)
    }
}
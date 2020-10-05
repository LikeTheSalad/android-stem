package com.likethesalad.placeholder.data.helpers

import com.google.common.truth.Truth
import com.likethesalad.placeholder.models.TasksNamesModel
import com.likethesalad.placeholder.providers.BuildDirProvider
import com.likethesalad.placeholder.providers.TaskProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Task
import org.junit.Before
import org.junit.Test
import java.io.File

class AndroidVariantHelperTest {

    private lateinit var tasksNames: TasksNamesModel
    private lateinit var taskProvider: TaskProvider
    private lateinit var buildDirProvider: BuildDirProvider
    private lateinit var androidVariantHelper: AndroidVariantHelper

    private val mergeResourcesName = "mergeResourcesTask"
    private val generateResValuesName = "generateResValuesTask"
    private val buildDirPath = "/some/dir"

    @Before
    fun setUp() {
        every { tasksNames.mergeResourcesName }.returns(mergeResourcesName)
        every { tasksNames.generateResValuesName }.returns(generateResValuesName)

        val buildDir = mockk<File>()
        every { buildDirProvider.getBuildDir() }.returns(buildDir)
        every { buildDir.absolutePath }.returns(buildDirPath)

        androidVariantHelper = AndroidVariantHelper(tasksNames, taskProvider, buildDirProvider)
    }

    @Test
    fun check_mergeResourcesTask() {
        // Given:
        val taskMock = mockk<Task>()
        every { taskProvider.findTaskByName<Task>(mergeResourcesName) }.returns(taskMock)

        // When:
        val result = androidVariantHelper.mergeResourcesTask

        // Then:
        Truth.assertThat(result).isEqualTo(taskMock)
        verify { taskProvider.findTaskByName<Task>(mergeResourcesName) }
    }

    @Test
    fun check_generateResValuesTask() {
        // Given:
        val taskMock = mockk<Task>()
        every { taskProvider.findTaskByName<Task>(generateResValuesName) }.returns(taskMock)

        // When:
        val result = androidVariantHelper.generateResValuesTask

        // Then:
        Truth.assertThat(result).isEqualTo(taskMock)
        verify { taskProvider.findTaskByName<Task>(generateResValuesName) }
    }

    @Test
    fun check_incrementalDir() {
        // When:
        val result = androidVariantHelper.incrementalDir

        // Then:
        Truth.assertThat(result).isEqualTo("$buildDirPath/intermediates/incremental/resolveDemoDebugPlaceholders")
    }
}
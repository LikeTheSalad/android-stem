package com.likethesalad.placeholder.data.helpers

import com.google.common.truth.Truth
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.modules.common.helpers.dirs.VariantBuildResolvedDir
import com.likethesalad.placeholder.modules.common.models.TasksNamesModel
import com.likethesalad.placeholder.providers.BuildDirProvider
import com.likethesalad.placeholder.providers.TaskProvider
import com.likethesalad.tools.android.plugin.data.AndroidVariantData
import com.likethesalad.tools.resource.serializer.ResourceSerializer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.gradle.api.Task
import org.junit.Before
import org.junit.Test
import java.io.File

class AndroidVariantContextTest {

    private lateinit var tasksNames: TasksNamesModel
    private lateinit var taskProvider: TaskProvider
    private lateinit var buildDirProvider: BuildDirProvider
    private lateinit var variantBuildResolvedDir: VariantBuildResolvedDir
    private lateinit var androidVariantData: AndroidVariantData
    private lateinit var tasksNamesModelFactory: TasksNamesModel.Factory
    private lateinit var variantBuildResolvedDirFactory: VariantBuildResolvedDir.Factory
    private lateinit var resourceSerializer: ResourceSerializer

    private lateinit var androidVariantContext: AndroidVariantContext

    private val mergeResourcesName = "mergeResourcesTask"
    private val generateResValuesName = "generateResValuesTask"
    private val resolvePlaceholdersName = "resolvePlaceholdersName"
    private val buildDirPath = "/some/dir"

    @Before
    fun setUp() {
        tasksNames = mockk()
        buildDirProvider = mockk()
        taskProvider = mockk()
        androidVariantData = mockk()
        tasksNamesModelFactory = mockk()
        variantBuildResolvedDirFactory = mockk()
        variantBuildResolvedDir = mockk()
        resourceSerializer = mockk()

        every { tasksNamesModelFactory.create(androidVariantData) }.returns(tasksNames)
        every { tasksNames.mergeResourcesName }.returns(mergeResourcesName)
        every { tasksNames.generateResValuesName }.returns(generateResValuesName)
        every { tasksNames.resolvePlaceholdersName }.returns(resolvePlaceholdersName)
        every { variantBuildResolvedDirFactory.create(androidVariantData) }.returns(variantBuildResolvedDir)

        val buildDir = mockk<File>()
        every { buildDirProvider.getBuildDir() }.returns(buildDir)
        every { buildDir.absolutePath }.returns(buildDirPath)

        androidVariantContext = AndroidVariantContext(
            androidVariantData,
            resourceSerializer,
            tasksNamesModelFactory,
            variantBuildResolvedDirFactory,
            taskProvider,
            buildDirProvider
        )
    }

    @Test
    fun check_mergeResourcesTask() {
        // Given:
        val taskMock = mockk<Task>()
        every { taskProvider.findTaskByName<Task>(mergeResourcesName) }.returns(taskMock)

        // When:
        val result = androidVariantContext.mergeResourcesTask

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
        val result = androidVariantContext.generateResValuesTask

        // Then:
        Truth.assertThat(result).isEqualTo(taskMock)
        verify { taskProvider.findTaskByName<Task>(generateResValuesName) }
    }

    @Test
    fun check_incrementalDir() {
        // When:
        val result = androidVariantContext.incrementalDir

        // Then:
        Truth.assertThat(result).isEqualTo("$buildDirPath/intermediates/incremental/$resolvePlaceholdersName")
    }
}
package com.likethesalad.placeholder.data.helpers

import com.google.common.truth.Truth
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.modules.common.helpers.dirs.VariantBuildResolvedDir
import com.likethesalad.placeholder.modules.common.models.TasksNamesModel
import com.likethesalad.placeholder.providers.ProjectDirsProvider
import com.likethesalad.placeholder.providers.TaskProvider
import com.likethesalad.tools.android.plugin.data.AndroidVariantData
import com.likethesalad.tools.resource.collector.android.data.variant.VariantTree
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
    private lateinit var projectDirsProvider: ProjectDirsProvider
    private lateinit var variantBuildResolvedDir: VariantBuildResolvedDir
    private lateinit var variantTree: VariantTree
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
        projectDirsProvider = mockk()
        taskProvider = mockk()
        variantTree = mockk()
        androidVariantData = mockk()
        tasksNamesModelFactory = mockk()
        variantBuildResolvedDirFactory = mockk()
        variantBuildResolvedDir = mockk()
        resourceSerializer = mockk()

        every { variantTree.androidVariantData }.returns(androidVariantData)
        every { tasksNamesModelFactory.create(androidVariantData) }.returns(tasksNames)
        every { tasksNames.mergeResourcesName }.returns(mergeResourcesName)
        every { tasksNames.resolvePlaceholdersName }.returns(resolvePlaceholdersName)
        every { variantBuildResolvedDirFactory.create(androidVariantData) }.returns(variantBuildResolvedDir)

        val buildDir = mockk<File>()
        every { projectDirsProvider.getBuildDir() }.returns(buildDir)
        every { buildDir.absolutePath }.returns(buildDirPath)

        androidVariantContext = AndroidVariantContext(
            variantTree,
            tasksNamesModelFactory,
            variantBuildResolvedDirFactory,
            resourceSerializer,
            taskProvider,
            projectDirsProvider
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
    fun check_incrementalDir() {
        // When:
        val result = androidVariantContext.incrementalDir

        // Then:
        Truth.assertThat(result).isEqualTo("$buildDirPath/intermediates/incremental/$resolvePlaceholdersName")
    }
}
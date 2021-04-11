package com.likethesalad.placeholder.data.helpers

import com.google.common.truth.Truth
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidConfigHelperFactory
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.modules.common.helpers.android.AppVariantHelper
import com.likethesalad.placeholder.modules.common.helpers.dirs.VariantBuildResolvedDir
import com.likethesalad.placeholder.modules.common.helpers.dirs.VariantBuildResolvedDirFactory
import com.likethesalad.placeholder.modules.common.helpers.dirs.VariantDirsPathFinderFactory
import com.likethesalad.placeholder.modules.common.models.TasksNamesModel
import com.likethesalad.placeholder.modules.common.models.TasksNamesModelFactory
import com.likethesalad.placeholder.providers.BuildDirProvider
import com.likethesalad.placeholder.providers.TaskProvider
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
    private lateinit var appVariantHelper: AppVariantHelper
    private lateinit var androidConfigHelperFactory: AndroidConfigHelperFactory
    private lateinit var tasksNamesModelFactory: TasksNamesModelFactory
    private lateinit var variantBuildResolvedDirFactory: VariantBuildResolvedDirFactory
    private lateinit var variantDirsPathFinderFactory: VariantDirsPathFinderFactory

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
        appVariantHelper = mockk()
        androidConfigHelperFactory = mockk()
        tasksNamesModelFactory = mockk()
        variantBuildResolvedDirFactory = mockk()
        variantDirsPathFinderFactory = mockk()
        variantBuildResolvedDir = mockk()

        every { tasksNamesModelFactory.create(appVariantHelper) }.returns(tasksNames)
        every { tasksNames.mergeResourcesName }.returns(mergeResourcesName)
        every { tasksNames.generateResValuesName }.returns(generateResValuesName)
        every { tasksNames.resolvePlaceholdersName }.returns(resolvePlaceholdersName)
        every { variantBuildResolvedDirFactory.create(appVariantHelper) }.returns(variantBuildResolvedDir)

        val buildDir = mockk<File>()
        every { buildDirProvider.getBuildDir() }.returns(buildDir)
        every { buildDir.absolutePath }.returns(buildDirPath)

        androidVariantContext = AndroidVariantContext(
            appVariantHelper,
            taskProvider,
            androidConfigHelperFactory,
            tasksNamesModelFactory,
            variantBuildResolvedDirFactory,
            variantDirsPathFinderFactory,
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
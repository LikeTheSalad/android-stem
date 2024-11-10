package com.likethesalad.stem

import com.likethesalad.android.templates.common.configuration.StemConfiguration
import com.likethesalad.android.templates.common.plugins.BaseTemplatesProcessorPlugin
import com.likethesalad.stem.di.AppInjector
import com.likethesalad.stem.locator.listener.TypeLocatorCreationListener
import com.likethesalad.stem.modules.common.helpers.dirs.VariantBuildResolvedDir.Companion.getBuildRelativeResolvedDir
import com.likethesalad.stem.providers.*
import com.likethesalad.stem.utils.PlaceholderTasksCreator
import com.likethesalad.tools.agpcompat.api.bridges.AndroidExtension
import com.likethesalad.tools.resource.locator.android.extension.AndroidResourceLocatorExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.TaskContainer
import java.io.File

@Suppress("UnstableApiUsage")
class ResolvePlaceholdersPlugin : BaseTemplatesProcessorPlugin(), AndroidExtensionProvider, ProjectDirsProvider,
    TaskProvider, TaskContainerProvider, LocatorExtensionProvider, PostConfigurationProvider,
    StemConfigurationProvider {

    private lateinit var project: Project
    private lateinit var androidExtension: AndroidExtension

    override fun apply(project: Project) {
        super.apply(project)
        this.project = project
        AppInjector.init(this)
        androidExtension = androidBridge.androidExtension
        val placeholderTasksCreator = AppInjector.getPlaceholderTasksCreator()
        val commonResourcesEntryPointFactory = AppInjector.getCommonResourcesEntryPointFactory()
        val templateResourcesEntryPointFactory = AppInjector.getTemplateResourcesEntryPointFactory()

        val typeCommon = PlaceholderTasksCreator.RESOURCE_TYPE_COMMON
        val typeTemplate = PlaceholderTasksCreator.RESOURCE_TYPE_TEMPLATE
        val creationListener = TypeLocatorCreationListener(setOf(typeCommon, typeTemplate), placeholderTasksCreator)

        val commonSourceConfigurationCreator = stringsLocatorExtension.getCommonSourceConfigurationCreator()

        stringsLocatorExtension.registerLocator(
            typeCommon,
            commonResourcesEntryPointFactory.create(commonSourceConfigurationCreator),
            creationListener
        )
        stringsLocatorExtension.registerLocator(
            typeTemplate,
            templateResourcesEntryPointFactory.create(commonSourceConfigurationCreator),
            creationListener
        )

        addResolvedResDirs()
    }

    private fun addResolvedResDirs() {
        androidBridge.onVariant {
            androidExtension.addVariantSrcDir(
                it.getVariantName(),
                project.layout.buildDirectory.dir(getBuildRelativeResolvedDir(it.getVariantName()))
            )
        }
    }

    fun getGradleLogger(): Logger {
        return project.logger
    }

    override fun getValidProjectPluginName() = "com.android.application"

    override fun getDisplayName(): String = "strings placeholder resolver"

    override fun getExtension(): AndroidExtension {
        return androidExtension
    }

    override fun getProjectDir(): File {
        return project.projectDir
    }

    override fun getRootProjectDir(): File {
        return project.rootDir
    }

    override fun getBuildDir(): File {
        return project.buildDir
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Task> findTaskByName(name: String): T? {
        return project.tasks.findByName(name) as? T
    }

    override fun getTaskContainer(): TaskContainer {
        return project.tasks
    }

    override fun getLocatorExtension(): AndroidResourceLocatorExtension {
        return stringsLocatorExtension
    }

    override fun executeAfterEvaluate(action: Action<in Project>) {
        project.afterEvaluate(action)
    }

    override fun getStemConfiguration(): StemConfiguration {
        return StemConfiguration.create(extension)
    }
}
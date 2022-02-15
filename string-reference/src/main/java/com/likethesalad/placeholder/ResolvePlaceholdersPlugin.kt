package com.likethesalad.placeholder

import com.likethesalad.android.templates.common.plugins.BaseTemplatesProcessorPlugin
import com.likethesalad.placeholder.di.AppInjector
import com.likethesalad.placeholder.locator.listener.TypeLocatorCreationListener
import com.likethesalad.placeholder.providers.AndroidExtensionProvider
import com.likethesalad.placeholder.providers.LocatorExtensionProvider
import com.likethesalad.placeholder.providers.ProjectDirsProvider
import com.likethesalad.placeholder.providers.TaskContainerProvider
import com.likethesalad.placeholder.providers.TaskProvider
import com.likethesalad.placeholder.utils.PlaceholderTasksCreator
import com.likethesalad.tools.android.plugin.data.AndroidExtension
import com.likethesalad.tools.resource.locator.android.extension.AndroidResourceLocatorExtension
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import java.io.File

@Suppress("UnstableApiUsage")
class ResolvePlaceholdersPlugin : BaseTemplatesProcessorPlugin(), AndroidExtensionProvider, ProjectDirsProvider,
    TaskProvider, TaskContainerProvider, LocatorExtensionProvider {

    companion object {
        const val EXTENSION_NAME = "stringXmlReference"
    }

    private lateinit var project: Project
    private lateinit var androidExtension: AndroidExtension

    override fun apply(project: Project) {
        super.apply(project)
        this.project = project
        AppInjector.init(this)
        androidExtension = androidTools.androidExtension
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
    override fun <T : Task> findTaskByName(name: String): T {
        return project.tasks.findByName(name) as T
    }

    override fun getTaskContainer(): TaskContainer {
        return project.tasks
    }

    override fun getLocatorExtension(): AndroidResourceLocatorExtension {
        return stringsLocatorExtension
    }
}
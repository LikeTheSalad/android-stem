package com.likethesalad.placeholder

import com.likethesalad.android.string.resources.locator.StringResourceLocatorPlugin
import com.likethesalad.placeholder.di.AppInjector
import com.likethesalad.placeholder.locator.listener.TypeLocatorCreationListener
import com.likethesalad.placeholder.providers.AndroidExtensionProvider
import com.likethesalad.placeholder.providers.LocatorExtensionProvider
import com.likethesalad.placeholder.providers.PluginExtensionProvider
import com.likethesalad.placeholder.providers.ProjectDirsProvider
import com.likethesalad.placeholder.providers.TaskContainerProvider
import com.likethesalad.placeholder.providers.TaskProvider
import com.likethesalad.placeholder.utils.PlaceholderTasksCreator
import com.likethesalad.tools.android.plugin.data.AndroidExtension
import com.likethesalad.tools.android.plugin.extension.AndroidToolsPluginExtension
import com.likethesalad.tools.resource.locator.android.extension.AndroidResourceLocatorExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.TaskContainer
import java.io.File

@Suppress("UnstableApiUsage")
class ResolvePlaceholdersPlugin : Plugin<Project>, AndroidExtensionProvider, ProjectDirsProvider,
    TaskProvider, TaskContainerProvider, PluginExtensionProvider, LocatorExtensionProvider {

    companion object {
        const val RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME = "resolver"
        const val EXTENSION_NAME = "stringXmlReference"
    }

    private lateinit var project: Project
    private lateinit var extension: PlaceholderExtension
    private lateinit var androidExtension: AndroidExtension
    private lateinit var stringsLocatorExtension: AndroidResourceLocatorExtension

    override fun apply(project: Project) {
        if (!project.plugins.hasPlugin("com.android.application")) {
            throw IllegalStateException("The strings placeholder resolver can only be applied to Android Application projects")
        }
        this.project = project
        AppInjector.init(this)
        project.plugins.apply(StringResourceLocatorPlugin::class.java)
        androidExtension = project.extensions.getByType(AndroidToolsPluginExtension::class.java).androidExtension
        extension = project.extensions.create(EXTENSION_NAME, PlaceholderExtension::class.java)
        stringsLocatorExtension = project.extensions.getByType(AndroidResourceLocatorExtension::class.java)
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

        checkForDeprecatedConfigs()
    }

    private fun checkForDeprecatedConfigs() {
        if (extension.keepResolvedFiles != null) {
            showDeprecatedConfigurationWarning("keepResolvedFiles")
        }
        if (extension.useDependenciesRes != null) {
            showDeprecatedConfigurationWarning("useDependenciesRes")
        }
    }

    override fun getExtension(): AndroidExtension {
        return androidExtension
    }

    override fun getProjectDir(): File {
        return project.projectDir
    }

    override fun getBuildDir(): File {
        return project.buildDir
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Task> findTaskByName(name: String): T {
        return project.tasks.findByName(name) as T
    }

    private fun showDeprecatedConfigurationWarning(name: String) {
        project.logger.log(
            LogLevel.WARN,
            "'$name' is deprecated and will be removed in the next version"
        )
    }

    override fun getTaskContainer(): TaskContainer {
        return project.tasks
    }

    override fun getPluginExtension(): PlaceholderExtension {
        return extension
    }

    override fun getLocatorExtension(): AndroidResourceLocatorExtension {
        return stringsLocatorExtension
    }
}
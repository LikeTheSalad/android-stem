package com.likethesalad.stem

import com.android.build.api.AndroidPluginVersion
import com.android.build.api.variant.AndroidComponentsExtension
import com.likethesalad.android.templates.common.plugins.BaseTemplatesProcessorPlugin
import com.likethesalad.stem.di.AppInjector
import com.likethesalad.stem.locator.listener.TypeLocatorCreationListener
import com.likethesalad.stem.modules.common.helpers.dirs.VariantBuildResolvedDir.Companion.getBuildRelativeResolvedDir
import com.likethesalad.stem.providers.AndroidExtensionProvider
import com.likethesalad.stem.providers.LocatorExtensionProvider
import com.likethesalad.stem.providers.ProjectDirsProvider
import com.likethesalad.stem.providers.TaskContainerProvider
import com.likethesalad.stem.providers.TaskProvider
import com.likethesalad.stem.utils.PlaceholderTasksCreator
import com.likethesalad.tools.android.plugin.data.AndroidExtension
import com.likethesalad.tools.resource.locator.android.extension.AndroidResourceLocatorExtension
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.TaskContainer
import java.io.File

@Suppress("UnstableApiUsage")
class ResolvePlaceholdersPlugin : BaseTemplatesProcessorPlugin(), AndroidExtensionProvider, ProjectDirsProvider,
    TaskProvider, TaskContainerProvider, LocatorExtensionProvider {

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
        validateAgp73AddingSrcDirs(project)
    }

    private fun validateAgp73AddingSrcDirs(project: Project) {
        val androidComponentsExtension = project.extensions.getByType(AndroidComponentsExtension::class.java)
        if (androidComponentsExtension.pluginVersion >= AndroidPluginVersion(7, 3)) {
            addSrcDirsBeforeVariants(androidComponentsExtension, project)
        }
    }

    private fun addSrcDirsBeforeVariants(extension: AndroidComponentsExtension<*, *, *>, project: Project) {
        extension.onVariants {
            val variantName = it.name
            androidExtension.addVariantSrcDir(
                variantName,
                project.layout.buildDirectory.dir(getBuildRelativeResolvedDir(variantName))
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
}
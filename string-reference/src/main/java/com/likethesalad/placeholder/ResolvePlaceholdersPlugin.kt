package com.likethesalad.placeholder

import com.likethesalad.android.string.resources.locator.StringResourceLocatorPlugin
import com.likethesalad.placeholder.di.AppInjector
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.modules.resolveStrings.ResolvePlaceholdersTask
import com.likethesalad.placeholder.modules.templateStrings.GatherTemplatesTask
import com.likethesalad.placeholder.providers.AndroidExtensionProvider
import com.likethesalad.placeholder.providers.BuildDirProvider
import com.likethesalad.placeholder.providers.PlaceholderExtensionProvider
import com.likethesalad.placeholder.providers.TaskProvider
import com.likethesalad.placeholder.utils.TaskActionProviderHolder
import com.likethesalad.tools.android.plugin.AndroidExtension
import com.likethesalad.tools.resource.locator.android.extension.ResourceLocatorExtension
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import java.io.File

class ResolvePlaceholdersPlugin : Plugin<Project>, AndroidExtensionProvider, BuildDirProvider,
    TaskProvider, PlaceholderExtensionProvider {

    companion object {
        const val RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME = "resolver"
        const val EXTENSION_NAME = "stringXmlReference"
    }

    private lateinit var project: Project
    private lateinit var extension: PlaceholderExtension

    override fun apply(project: Project) {
        //androidExtension = project.extensions.getByType(AppExtension::class.java)

        project.plugins.apply(StringResourceLocatorPlugin::class.java)
        val stringsLocatorExtension = project.extensions.getByType(ResourceLocatorExtension::class.java)
        val taskActionProviderHolder = AppInjector.getTaskActionProviderHolder()
        val appVariantHelperFactory = AppInjector.getAppVariantHelperFactory()
        val androidVariantContextFactory = AppInjector.getAndroidVariantContextFactory()

        stringsLocatorExtension.onResourceLocatorTaskCreated(Action { taskContainer ->
            //val androidVariantName = taskContainer.taskContext.variantTree.
        })

//        project.plugins.withId("com.android.application") {
//            this.project = project
//            AppInjector.init(this)
//            extension = project.extensions.create(EXTENSION_NAME, PlaceholderExtension::class.java)
//            project.afterEvaluate {
//                val taskActionProviderHolder = AppInjector.getTaskActionProviderHolder()
//                val appVariantHelperFactory = AppInjector.getAppVariantHelperFactory()
//                val androidVariantContextFactory = AppInjector.getAndroidVariantContextFactory()
//
//                androidExtension.applicationVariants.forEach {
//                    val androidVariantContext = androidVariantContextFactory.create(appVariantHelperFactory.create(it))
//                    createResolvePlaceholdersTaskForVariant(
//                        androidVariantContext,
//                        taskActionProviderHolder,
//                        extension.resolveOnBuild
//                    )
//                }
//            }
//        }
    }

    private fun createResolvePlaceholdersTaskForVariant(
        androidVariantContext: AndroidVariantContext,
        taskActionProviderHolder: TaskActionProviderHolder,
        resolveOnBuild: Boolean
    ) {
        val gatherTemplatesActionProvider = taskActionProviderHolder.gatherTemplatesActionProvider
        val resolvePlaceholdersActionProvider = taskActionProviderHolder.resolvePlaceholdersActionProvider

        val gatherTemplatesTask = project.tasks.register(
            androidVariantContext.tasksNames.gatherStringTemplatesName,
            GatherTemplatesTask::class.java,
            gatherTemplatesActionProvider.provide(androidVariantContext)
        )

        gatherTemplatesTask.configure {
            it.group = RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME
            //it.dependsOn(gatherStringsTask)
        }

        val resolvePlaceholdersTask = project.tasks.register(
            androidVariantContext.tasksNames.resolvePlaceholdersName,
            ResolvePlaceholdersTask::class.java,
            resolvePlaceholdersActionProvider.provide(androidVariantContext)
        )

        resolvePlaceholdersTask.configure {
            it.group = RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME
            it.dependsOn(gatherTemplatesTask)
        }

        if (resolveOnBuild) {
            androidVariantContext.mergeResourcesTask.dependsOn(resolvePlaceholdersTask)
        }
    }

    override fun getExtension(): AndroidExtension {
        TODO()
    }

    override fun getBuildDir(): File {
        return project.buildDir
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Task> findTaskByName(name: String): T {
        return project.tasks.findByName(name) as T
    }

    override fun getPlaceholderExtension(): PlaceholderExtension {
        return extension
    }
}
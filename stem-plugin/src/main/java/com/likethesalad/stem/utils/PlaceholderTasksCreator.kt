package com.likethesalad.stem.utils

import com.likethesalad.android.templates.common.configuration.StemConfiguration
import com.likethesalad.android.templates.common.tasks.identifier.TemplatesIdentifierTask
import com.likethesalad.stem.locator.listener.TypeLocatorCreationListener
import com.likethesalad.stem.modules.collector.VariantRes
import com.likethesalad.stem.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.stem.modules.common.models.TasksNamesModel
import com.likethesalad.stem.modules.resolveStrings.ResolvePlaceholdersTask
import com.likethesalad.stem.modules.resolveStrings.data.ResolvePlaceholdersArgs
import com.likethesalad.stem.modules.templateStrings.GatherTemplatesTask
import com.likethesalad.stem.modules.templateStrings.data.GatherTemplatesArgs
import com.likethesalad.stem.providers.AndroidExtensionProvider
import com.likethesalad.stem.providers.PostConfigurationProvider
import com.likethesalad.stem.providers.TaskContainerProvider
import com.likethesalad.tools.resource.collector.android.data.variant.VariantTree
import com.likethesalad.tools.resource.locator.android.extension.configuration.data.ResourceLocatorInfo
import javax.inject.Inject
import javax.inject.Singleton
import org.gradle.api.tasks.TaskProvider

@Singleton
class PlaceholderTasksCreator @Inject constructor(
    taskContainerProvider: TaskContainerProvider,
    private val androidVariantContextFactory: AndroidVariantContext.Factory,
    private val taskActionProviderHolder: TaskActionProviderHolder,
    private val stemConfiguration: StemConfiguration,
    private val postConfigurationProvider: PostConfigurationProvider,
    private val androidExtensionProvider: AndroidExtensionProvider
) : TypeLocatorCreationListener.Callback {

    companion object {
        const val RESOURCE_TYPE_COMMON = "common"
        const val RESOURCE_TYPE_TEMPLATE = "template"
    }

    private val taskContainer by lazy { taskContainerProvider.getTaskContainer() }
    private val variantRes = VariantRes(androidExtensionProvider.getApplicationExtension())

    override fun onLocatorsReady(variantTree: VariantTree, locatorsByType: Map<String, ResourceLocatorInfo>) {
        val androidVariantContext = androidVariantContextFactory.create(variantTree)
        val commonResourcesInfo = locatorsByType.getValue(RESOURCE_TYPE_COMMON)
        val templateResourcesInfo = locatorsByType.getValue(RESOURCE_TYPE_TEMPLATE)
        createResolvePlaceholdersTaskForVariant(androidVariantContext, commonResourcesInfo, templateResourcesInfo)
    }

    private fun createResolvePlaceholdersTaskForVariant(
        androidVariantContext: AndroidVariantContext,
        commonResourcesInfo: ResourceLocatorInfo,
        templateResourcesInfo: ResourceLocatorInfo
    ) {
        val gatherTemplatesActionProvider = taskActionProviderHolder.gatherTemplatesActionProvider
        val resolvePlaceholdersActionProvider = taskActionProviderHolder.resolvePlaceholdersActionProvider

        val templatesIdentifierTask = createTemplatesIdentifierTaskProvider(
            androidVariantContext.tasksNames,
            templateResourcesInfo
        )

        val gatherTemplatesTask = taskContainer.register(
            androidVariantContext.tasksNames.gatherStringTemplatesName,
            GatherTemplatesTask::class.java,
            GatherTemplatesArgs(
                gatherTemplatesActionProvider.provide(androidVariantContext),
                variantRes.getResDirs(androidVariantContext.variantTree.androidVariantData)
            )
        )

        gatherTemplatesTask.configure {
            it.commonResourcesDir.set(commonResourcesInfo.taskInfo.outputDirectoryProvider.getOutputDirProperty())
            it.libraryResources.from(androidVariantContext.androidVariantData.getLibrariesResources())
            it.templateIdsFile.set(templatesIdentifierTask.flatMap { identifierTask -> identifierTask.outputFile })
        }

        val resolvePlaceholdersTask = taskContainer.register(
            androidVariantContext.tasksNames.resolvePlaceholdersName,
            ResolvePlaceholdersTask::class.java,
            ResolvePlaceholdersArgs(resolvePlaceholdersActionProvider.provide(androidVariantContext))
        )

        resolvePlaceholdersTask.configure {
            it.templatesDir.set(gatherTemplatesTask.flatMap { gatherTemplates -> gatherTemplates.outDir })
            it.outputDir.set(androidVariantContext.variantBuildResolvedDir.resolvedDir)
        }

        postConfigurationProvider.executeAfterEvaluate {
            androidVariantContext.mergeResourcesTask.dependsOn(resolvePlaceholdersTask)
            addExplicitDependencies(
                androidVariantContext,
                resolvePlaceholdersTask,
                listOf(
                    "map%sSourceSetPaths",
                    "package%sResources",
                    "extractDeepLinks%s",
                    "generate%sResources",
                    "extract%sSupportedLocales"
                )
            )
        }
    }

    private fun addExplicitDependencies(
        androidVariantContext: AndroidVariantContext,
        resolvePlaceholdersTask: TaskProvider<ResolvePlaceholdersTask>,
        dependantTaskNames: List<String>
    ) {
        dependantTaskNames.forEach { nameTemplate ->
            androidVariantContext.findVariantTask(nameTemplate)?.mustRunAfter(resolvePlaceholdersTask)
        }
    }

    private fun createTemplatesIdentifierTaskProvider(
        taskNames: TasksNamesModel,
        localResourcesInfo: ResourceLocatorInfo
    ): TaskProvider<TemplatesIdentifierTask> {
        val provider = taskContainer.register(
            taskNames.templatesIdentifierName,
            TemplatesIdentifierTask::class.java,
            TemplatesIdentifierTask.Args(
                localResourcesInfo.resourcesProvider,
                stemConfiguration
            )
        )

        provider.configure {
            it.localResourcesDir.set(localResourcesInfo.taskInfo.outputDirectoryProvider.getOutputDirProperty())
        }

        return provider
    }
}

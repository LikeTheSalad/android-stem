package com.likethesalad.android.templates.provider.locator.listener

import com.likethesalad.android.templates.common.tasks.identifier.TemplatesIdentifierTask
import com.likethesalad.android.templates.common.tasks.identifier.action.TemplatesIdentifierAction
import com.likethesalad.android.templates.provider.tasks.metainf.ServiceMetaInfGeneratorTask
import com.likethesalad.android.templates.provider.tasks.service.TemplatesServiceGeneratorTask
import com.likethesalad.android.templates.provider.tasks.service.action.TemplatesServiceGeneratorAction
import com.likethesalad.tools.android.plugin.data.AndroidVariantData
import com.likethesalad.tools.resource.collector.android.data.variant.VariantTree
import com.likethesalad.tools.resource.locator.android.extension.configuration.data.ResourceLocatorInfo
import com.likethesalad.tools.resource.locator.android.extension.listener.ResourceLocatorCreationListener
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

@Suppress("UnstableApiUsage")
class TemplatesProviderTaskCreator(
    private val project: Project,
    private val taskServiceGeneratorActionFactory: TemplatesServiceGeneratorAction.Factory,
    private val templatesIdentifierActionFactory: TemplatesIdentifierAction.Factory
) : ResourceLocatorCreationListener {

    companion object {
        private const val SERVICE_GENERATOR_TASK_TEMPLATE = "templateProvider%sGenerateService"
        private const val META_INF_GENERATOR_TASK_TEMPLATE = "templateProvider%sGenerateMetadata"
    }

    override fun onLocatorReady(type: String, variantTree: VariantTree, info: ResourceLocatorInfo) {
        attachTasks(variantTree.androidVariantData, info)
    }

    private fun attachTasks(variant: AndroidVariantData, resourcesInfo: ResourceLocatorInfo) {
        val serviceGenerator = createServiceGeneratorTask(project, variant.getVariantName())
        val templatesIdentifier = createTemplatesIdentifierTask(variant.getVariantName(), resourcesInfo)
        val metaInfGenerator = createMetaInfGeneratorTask(project, variant.getVariantName())

        serviceGenerator.configure {
            it.templateIdsFile.set(templatesIdentifier.flatMap { templatesIdentifier -> templatesIdentifier.outputFile })
        }
        metaInfGenerator.configure { task ->
            task.generatedClasspath.set(serviceGenerator.flatMap { it.outputDir })
        }

        variant.registerGeneratedJavaBinaries(serviceGenerator, serviceGenerator.flatMap { it.outputDir })
        variant.getProcessJavaResourcesProvider().configure {
            it.from(metaInfGenerator)
        }
    }

    private fun createServiceGeneratorTask(
        project: Project,
        variantName: String
    ): TaskProvider<TemplatesServiceGeneratorTask> {
        return project.tasks.register(
            SERVICE_GENERATOR_TASK_TEMPLATE.format(variantName.capitalize()),
            TemplatesServiceGeneratorTask::class.java,
            TemplatesServiceGeneratorTask.Args(taskServiceGeneratorActionFactory)
        )
    }

    private fun createMetaInfGeneratorTask(
        project: Project,
        variantName: String
    ): TaskProvider<ServiceMetaInfGeneratorTask> {
        return project.tasks.register(
            META_INF_GENERATOR_TASK_TEMPLATE.format(variantName.capitalize()),
            ServiceMetaInfGeneratorTask::class.java
        )
    }

    private fun createTemplatesIdentifierTask(
        variantName: String,
        localResourcesInfo: ResourceLocatorInfo
    ): TaskProvider<TemplatesIdentifierTask> {
        val provider = project.tasks.register(
            TemplatesIdentifierTask.generateTaskName(variantName),
            TemplatesIdentifierTask::class.java,
            TemplatesIdentifierTask.Args(
                localResourcesInfo.resourcesProvider,
                templatesIdentifierActionFactory
            )
        )

        provider.configure {
            it.localResourcesDir.set(localResourcesInfo.taskInfo.outputDirectoryProvider.getOutputDirProperty())
        }

        return provider
    }
}
package com.likethesalad.android.templates.common.tasks.identifier

import com.likethesalad.android.templates.common.tasks.identifier.action.TemplatesIdentifierAction
import com.likethesalad.android.templates.common.tasks.identifier.data.TemplateItemsSerializer
import com.likethesalad.tools.resource.locator.android.extension.configuration.data.ResourcesProvider
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class TemplatesIdentifierTask @Inject constructor(private val args: Args) : DefaultTask() {

    @InputDirectory
    val localResourcesDir: DirectoryProperty = project.objects.directoryProperty()

    @OutputFile
    val outputFile: RegularFileProperty = project.objects.fileProperty()

    init {
        outputFile.set(project.layout.buildDirectory.file("intermediates/incremental/$name"))
    }

    @TaskAction
    fun execute() {
        val action = TemplatesIdentifierAction(
            args.localResources,
            outputFile.get().asFile,
            args.templateItemsSerializer
        )
        action.execute()
    }

    data class Args(
        val localResources: ResourcesProvider,
        val templateItemsSerializer: TemplateItemsSerializer
    )
}
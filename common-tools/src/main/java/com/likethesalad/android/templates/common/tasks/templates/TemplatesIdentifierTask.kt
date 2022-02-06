package com.likethesalad.android.templates.common.tasks.templates

import com.likethesalad.android.templates.common.tasks.templates.action.TemplatesIdentifierAction
import com.likethesalad.tools.resource.locator.android.extension.configuration.data.ResourcesProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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
        args.actionFactory.create(args.localResources, outputFile.get().asFile)
    }

    data class Args @AssistedInject constructor(
        @Assisted val localResources: ResourcesProvider,
        val actionFactory: TemplatesIdentifierAction.Factory
    ) {

        @AssistedFactory
        interface Factory {
            fun create(localResources: ResourcesProvider): Args
        }
    }
}
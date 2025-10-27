package com.likethesalad.android.templates.common.tasks.identifier

import com.likethesalad.android.templates.common.configuration.StemConfiguration
import com.likethesalad.android.templates.common.tasks.BaseTask
import com.likethesalad.android.templates.common.tasks.identifier.action.TemplatesIdentifierAction2
import java.io.File
import javax.inject.Inject
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

open class TemplatesIdentifierTask2 @Inject constructor(private val args: Args) : BaseTask() {

    @OutputFile
    val outputFile: RegularFileProperty = project.objects.fileProperty()

    init {
        outputFile.set(project.layout.buildDirectory.file("intermediates/incremental/$name/templateNames.txt"))
    }

    @TaskAction
    fun execute() {
        val action = TemplatesIdentifierAction2.create(
            args.stemConfiguration,
            args.localResDirs,
            outputFile.get().asFile
        )
        action.execute()
    }

    data class Args(
        val localResDirs: List<Collection<File>>,
        val stemConfiguration: StemConfiguration
    )
}

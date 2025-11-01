package com.likethesalad.stem.modules.resolveStrings

import com.likethesalad.stem.modules.common.BaseTask
import com.likethesalad.stem.tools.DirectoryUtils
import com.likethesalad.stem.modules.resolveStrings.data.ResolvePlaceholdersArgs2
import javax.inject.Inject
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction

open class ResolvePlaceholdersTask2
@Inject constructor(private val args: ResolvePlaceholdersArgs2) : BaseTask() {

    @SkipWhenEmpty
    @InputDirectory
    val templatesDir: DirectoryProperty = project.objects.directoryProperty()

    @OutputDirectory
    val outputDir: DirectoryProperty = project.objects.directoryProperty()

    @TaskAction
    fun resolve() {
        DirectoryUtils.clearIfNeeded(outputDir.get().asFile)
        args.resolvePlaceholdersAction.resolve(templatesDir.get().asFile, outputDir.get().asFile)
    }
}

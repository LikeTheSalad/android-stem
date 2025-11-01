package com.likethesalad.stem.modules.collector

import com.likethesalad.android.protos.ValuesStringResources
import com.likethesalad.android.resources.StringResourceCollector
import com.likethesalad.stem.modules.common.BaseTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class RawStringCollectorTask : BaseTask() {
    @get:InputFiles
    abstract val localResDirs: ListProperty<Collection<Directory>>

    @get:InputFiles
    abstract val libraryResDirs: ConfigurableFileCollection

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun execute() {
        val resDirs = localResDirs.get().map { collection -> collection.map { dir -> dir.asFile } }.toMutableList()
        resDirs.add(libraryResDirs.files.toList())
        val values = StringResourceCollector.collectStringResourcesPerValueDir(resDirs)
        outputFile.get().asFile.outputStream().use {
            ValuesStringResources.ADAPTER.encode(it, values)
        }
    }
}
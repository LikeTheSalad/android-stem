package com.likethesalad.android.templates.common.tasks.templates

import com.likethesalad.tools.resource.locator.android.extension.configuration.data.ResourcesProvider
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

open class TemplatesIdentifierTask @Inject constructor(private val args: Args) : DefaultTask() {

    @TaskAction
    fun execute() {

    }

    data class Args(val localResources: ResourcesProvider)
}
package com.likethesalad.android.templates.provider.task.action

import com.likethesalad.android.templates.provider.task.action.helpers.RandomStringGenerator
import com.likethesalad.tools.resource.locator.android.extension.configuration.data.ResourcesProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TemplatesServiceGeneratorAction @AssistedInject constructor(
    @Assisted private val projectName: String,
    @Assisted private val templatesProvider: ResourcesProvider,
    private val randomStringGenerator: RandomStringGenerator
) {

    @AssistedFactory
    interface Factory {
        fun create(projectName: String, templatesProvider: ResourcesProvider): TemplatesServiceGeneratorAction
    }

    fun execute() {

    }
}
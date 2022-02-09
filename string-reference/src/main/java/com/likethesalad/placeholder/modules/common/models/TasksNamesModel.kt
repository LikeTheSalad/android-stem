package com.likethesalad.placeholder.modules.common.models

import com.likethesalad.android.templates.common.tasks.identifier.TemplatesIdentifierTask
import com.likethesalad.tools.android.plugin.data.AndroidVariantData
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TasksNamesModel @AssistedInject constructor(@Assisted androidVariantData: AndroidVariantData) {

    @AssistedFactory
    interface Factory {
        fun create(androidVariantData: AndroidVariantData): TasksNamesModel
    }

    companion object {
        private const val GATHER_STRING_TEMPLATES_NAME_FORMAT = "gather%sStringTemplates"
        private const val RESOLVE_PLACEHOLDERS_NAME_FORMAT = "resolve%sPlaceholders"
        private const val ANDROID_MERGE_RESOURCES_TASK_NAME_FORMAT = "merge%sResources"
    }

    private val capitalizedBuildVariant = androidVariantData.getVariantName().capitalize()

    val templatesIdentifierName: String by lazy {
        TemplatesIdentifierTask.generateTaskName(capitalizedBuildVariant)
    }

    val gatherStringTemplatesName: String by lazy {
        GATHER_STRING_TEMPLATES_NAME_FORMAT.format(capitalizedBuildVariant)
    }

    val resolvePlaceholdersName: String by lazy {
        RESOLVE_PLACEHOLDERS_NAME_FORMAT.format(capitalizedBuildVariant)
    }

    val mergeResourcesName: String by lazy {
        ANDROID_MERGE_RESOURCES_TASK_NAME_FORMAT.format(capitalizedBuildVariant)
    }
}
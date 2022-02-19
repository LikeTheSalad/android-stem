package com.likethesalad.android.templates.common.tasks.identifier.action

import com.likethesalad.android.templates.common.tasks.identifier.data.TemplateItem
import com.likethesalad.android.templates.common.tasks.identifier.data.TemplateItemsSerializer
import com.likethesalad.android.templates.common.utils.CommonConstants
import com.likethesalad.tools.resource.api.android.data.AndroidResourceType
import com.likethesalad.tools.resource.api.android.environment.Language
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource
import com.likethesalad.tools.resource.locator.android.extension.configuration.data.ResourcesProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.File

class TemplatesIdentifierAction @AssistedInject constructor(
    @Assisted private val localResources: ResourcesProvider,
    @Assisted private val outputFile: File,
    private val templateItemsSerializer: TemplateItemsSerializer
) {

    @AssistedFactory
    interface Factory {
        fun create(localResources: ResourcesProvider, outputFile: File): TemplatesIdentifierAction
    }

    fun execute() {
        val templates = getTemplatesFromResources()
        outputFile.writeText(templateItemsSerializer.serialize(templates))
    }

    @Suppress("UNCHECKED_CAST")
    private fun getTemplatesFromResources(): List<TemplateItem> {
        val mainLanguageResources = localResources.resources.getMergedResourcesForLanguage(Language.Default)
        val stringResources = mainLanguageResources.getResourcesByType(AndroidResourceType.StringType)
        val templates = filterTemplates(stringResources as List<StringAndroidResource>)

        return templates.sortedBy { it.name() }.map {
            TemplateItem(it.name(), it.type().getName())
        }
    }

    private fun filterTemplates(stringResources: List<StringAndroidResource>): List<StringAndroidResource> {
        return stringResources.filter { stringResource ->
            CommonConstants.PLACEHOLDER_REGEX.containsMatchIn(stringResource.stringValue())
        }
    }
}
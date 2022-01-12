package com.likethesalad.placeholder.modules.templateStrings.collector

import com.likethesalad.tools.resource.api.android.environment.Language
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource
import com.likethesalad.tools.resource.api.collection.BasicResourceCollection
import com.likethesalad.tools.resource.api.collection.ResourceCollection
import com.likethesalad.tools.resource.locator.android.extension.resources.LanguageCollectionProvider

class TemplatesCollectionProvider(private val resources: List<StringAndroidResource>) : LanguageCollectionProvider {

    override fun getCollectionByLanguage(language: Language): ResourceCollection? {
        val list = resources.filter { it.getAndroidScope().language == language }
        if (list.isEmpty()) {
            return null
        }
        return BasicResourceCollection(list.sortedBy { it.name() })
    }

    override fun listLanguages(): List<Language> {
        return resources.map { it.getAndroidScope().language }.distinct()
    }
}
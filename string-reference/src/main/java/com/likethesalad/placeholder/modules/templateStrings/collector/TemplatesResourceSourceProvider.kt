package com.likethesalad.placeholder.modules.templateStrings.collector

import com.likethesalad.tools.resource.collector.android.data.resdir.ResDir
import com.likethesalad.tools.resource.collector.android.source.providers.ResDirResourceSourceProvider
import com.likethesalad.tools.resource.collector.source.ResourceSource
import com.likethesalad.tools.resource.collector.source.ResourceSourceProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TemplatesResourceSourceProvider @AssistedInject constructor(
    @Assisted private val templatesResDirs: List<ResDir>,
    private val resDirResourceSourceProviderFactory: ResDirResourceSourceProvider.Factory
) : ResourceSourceProvider() {

    private val internalSources by lazy { findSources() }

    @AssistedFactory
    interface Factory {
        fun create(templatesResDirs: List<ResDir>): TemplatesResourceSourceProvider
    }

    override fun doGetSources(): List<ResourceSource> {
        return internalSources
    }

    private fun findSources(): List<ResourceSource> {
        val sources = mutableListOf<ResourceSource>()

        templatesResDirs.forEach { resDir ->
            sources.addAll(getSourcesFromResDir(resDir))
        }

        return sources
    }

    private fun getSourcesFromResDir(resDir: ResDir): List<ResourceSource> {
        return resDirResourceSourceProviderFactory.create(resDir).getSources()
    }
}
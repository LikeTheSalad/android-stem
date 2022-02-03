package com.likethesalad.android.templates.provider.di

import com.likethesalad.android_templates.provider.plugin.generated.BuildConfig
import com.likethesalad.tools.plugin.metadata.api.PluginMetadata
import com.likethesalad.tools.plugin.metadata.consumer.PluginMetadataProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class TemplatesProviderModule {

    @Provides
    @Singleton
    fun providePluginMetadataProvider(): PluginMetadata {
        return PluginMetadataProvider.getInstance(BuildConfig.METADATA_PROPERTIES_ID).provide()
    }
}
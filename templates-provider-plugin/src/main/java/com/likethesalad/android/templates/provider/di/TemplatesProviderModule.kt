package com.likethesalad.android.templates.provider.di

import com.likethesalad.android.templates.common.plugins.extension.StemExtension
import com.likethesalad.android.templates.provider.TemplatesProviderPlugin
import com.likethesalad.android_templates.provider.plugin.generated.BuildConfig
import com.likethesalad.tools.plugin.metadata.api.PluginMetadata
import com.likethesalad.tools.plugin.metadata.consumer.PluginMetadataProvider
import dagger.Module
import dagger.Provides
import java.util.Base64
import javax.inject.Singleton

@Module
class TemplatesProviderModule(private val templatesProviderPlugin: TemplatesProviderPlugin) {

    @Provides
    @Singleton
    fun providePluginMetadataProvider(): PluginMetadata {
        return PluginMetadataProvider.getInstance(BuildConfig.METADATA_PROPERTIES_ID).provide()
    }

    @Provides
    @Singleton
    fun provideBase64Encoder(): Base64.Encoder {
        return Base64.getEncoder()
    }

    @Provides
    @Singleton
    fun provideExtension(): StemExtension {
        return templatesProviderPlugin.extension
    }
}
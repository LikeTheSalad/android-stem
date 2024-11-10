package com.likethesalad.stem.di

import com.likethesalad.android.templates.common.configuration.StemConfiguration
import com.likethesalad.stem.ResolvePlaceholdersPlugin
import com.likethesalad.stem.providers.*
import com.likethesalad.tools.resource.serializer.ResourceSerializer
import dagger.Module
import dagger.Provides
import org.gradle.api.logging.Logger
import javax.inject.Singleton

@Module
class AppModule(private val resolvePlaceholdersPlugin: ResolvePlaceholdersPlugin) {

    @Provides
    @Singleton
    fun provideAndroidExtensionProvider(): AndroidExtensionProvider {
        return resolvePlaceholdersPlugin
    }

    @Provides
    @Singleton
    fun provideProjectDirsProvider(): ProjectDirsProvider {
        return resolvePlaceholdersPlugin
    }

    @Provides
    @Singleton
    fun provideTaskProvider(): TaskProvider {
        return resolvePlaceholdersPlugin
    }

    @Provides
    @Singleton
    fun provideTaskContainerProvider(): TaskContainerProvider {
        return resolvePlaceholdersPlugin
    }

    @Provides
    @Singleton
    fun provideResourceSerializer(): ResourceSerializer {
        return resolvePlaceholdersPlugin.getLocatorExtension().getResourceSerializer()
    }

    @Provides
    @Singleton
    fun provideGradleLogger(): Logger {
        return resolvePlaceholdersPlugin.getGradleLogger()
    }

    @Provides
    @Singleton
    fun provideExtension(): StemConfiguration {
        return resolvePlaceholdersPlugin.getStemConfiguration()
    }

    @Provides
    @Singleton
    fun providePostConfigurationProvider(): PostConfigurationProvider {
        return resolvePlaceholdersPlugin
    }
}
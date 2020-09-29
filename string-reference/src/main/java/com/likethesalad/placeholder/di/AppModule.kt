package com.likethesalad.placeholder.di

import com.likethesalad.placeholder.ResolvePlaceholdersPlugin
import com.likethesalad.placeholder.providers.AndroidExtensionProvider
import com.likethesalad.placeholder.providers.BuildDirProvider
import com.likethesalad.placeholder.providers.PlaceholderExtensionProvider
import com.likethesalad.placeholder.providers.TaskProvider
import dagger.Module
import dagger.Provides
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
    fun provideBuildDirProvider(): BuildDirProvider {
        return resolvePlaceholdersPlugin
    }

    @Provides
    @Singleton
    fun provideTaskProvider(): TaskProvider {
        return resolvePlaceholdersPlugin
    }

    @Provides
    @Singleton
    fun providePlaceholderExtensionProvider(): PlaceholderExtensionProvider {
        return resolvePlaceholdersPlugin
    }
}
package com.likethesalad.placeholder.di

import com.likethesalad.placeholder.ResolvePlaceholdersPlugin
import com.likethesalad.placeholder.providers.AndroidExtensionProvider
import com.likethesalad.placeholder.providers.ProjectDirsProvider
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
    fun provideProjectDirsProvider(): ProjectDirsProvider {
        return resolvePlaceholdersPlugin
    }

    @Provides
    @Singleton
    fun provideTaskProvider(): TaskProvider {
        return resolvePlaceholdersPlugin
    }
}
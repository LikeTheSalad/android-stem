package com.likethesalad.stem.di

import com.likethesalad.stem.locator.entrypoints.common.CommonResourcesEntryPoint
import com.likethesalad.stem.locator.entrypoints.templates.TemplateResourcesEntryPoint
import com.likethesalad.stem.utils.PlaceholderTasksCreator
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun placeholderTasksCreator(): PlaceholderTasksCreator
    fun commonResourcesEntryPointFactory(): CommonResourcesEntryPoint.Factory
    fun templateResourcesEntryPointFactory(): TemplateResourcesEntryPoint.Factory
}
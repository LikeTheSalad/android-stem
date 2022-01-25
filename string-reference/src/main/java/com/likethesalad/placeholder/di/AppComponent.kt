package com.likethesalad.placeholder.di

import com.likethesalad.placeholder.locator.entrypoints.common.CommonResourcesEntryPoint
import com.likethesalad.placeholder.locator.entrypoints.templates.TemplateResourcesEntryPoint
import com.likethesalad.placeholder.utils.PlaceholderTasksCreator
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun placeholderTasksCreator(): PlaceholderTasksCreator
    fun commonResourcesEntryPointFactory(): CommonResourcesEntryPoint.Factory
    fun templateResourcesEntryPointFactory(): TemplateResourcesEntryPoint
}
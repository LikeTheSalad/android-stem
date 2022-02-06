package com.likethesalad.placeholder.di

import com.likethesalad.placeholder.ResolvePlaceholdersPlugin
import com.likethesalad.placeholder.locator.entrypoints.common.CommonResourcesEntryPoint
import com.likethesalad.placeholder.locator.entrypoints.templates.TemplateResourcesEntryPoint
import com.likethesalad.placeholder.utils.PlaceholderTasksCreator

object AppInjector {

    private lateinit var component: AppComponent

    fun init(resolvePlaceholdersPlugin: ResolvePlaceholdersPlugin) {
        component = DaggerAppComponent.builder()
            .appModule(AppModule(resolvePlaceholdersPlugin))
            .build()
    }

    fun getPlaceholderTasksCreator(): PlaceholderTasksCreator {
        return component.placeholderTasksCreator()
    }

    fun getCommonResourcesEntryPointFactory(): CommonResourcesEntryPoint.Factory {
        return component.commonResourcesEntryPointFactory()
    }

    fun getTemplateResourcesEntryPointFactory(): TemplateResourcesEntryPoint.Factory {
        return component.templateResourcesEntryPointFactory()
    }
}
package com.likethesalad.stem.di

import com.likethesalad.stem.ResolvePlaceholdersPlugin
import com.likethesalad.stem.locator.entrypoints.common.CommonResourcesEntryPoint
import com.likethesalad.stem.locator.entrypoints.templates.TemplateResourcesEntryPoint
import com.likethesalad.stem.utils.PlaceholderTasksCreator

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
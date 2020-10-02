package com.likethesalad.placeholder.di

import com.likethesalad.placeholder.ResolvePlaceholdersPlugin
import com.likethesalad.placeholder.utils.AppVariantHelperFactory
import com.likethesalad.placeholder.utils.TaskActionProviderFactory

object AppInjector {

    private lateinit var component: AppComponent

    fun init(resolvePlaceholdersPlugin: ResolvePlaceholdersPlugin) {
        component = DaggerAppComponent.builder()
            .appModule(AppModule(resolvePlaceholdersPlugin))
            .build()
    }

    fun getTaskActionProviderFactory(): TaskActionProviderFactory {
        return component.taskActionProviderFactory()
    }

    fun getAppVariantHelperFactory(): AppVariantHelperFactory {
        return component.appVariantHelperFactory()
    }
}
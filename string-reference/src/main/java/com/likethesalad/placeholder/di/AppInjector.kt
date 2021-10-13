package com.likethesalad.placeholder.di

import com.likethesalad.placeholder.ResolvePlaceholdersPlugin
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.utils.TaskActionProviderHolder

object AppInjector {

    private lateinit var component: AppComponent

    fun init(resolvePlaceholdersPlugin: ResolvePlaceholdersPlugin) {
        component = DaggerAppComponent.builder()
            .appModule(AppModule(resolvePlaceholdersPlugin))
            .build()
    }

    fun getTaskActionProviderHolder(): TaskActionProviderHolder {
        return component.taskActionProviderHolder()
    }


    fun getAndroidVariantContextFactory(): AndroidVariantContext.Factory {
        return component.androidVariantContextFactory()
    }
}
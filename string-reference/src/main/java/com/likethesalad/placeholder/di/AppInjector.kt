package com.likethesalad.placeholder.di

import com.likethesalad.placeholder.ResolvePlaceholdersPlugin
import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContextFactory
import com.likethesalad.placeholder.modules.common.helpers.android.AppVariantHelperFactory
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

    fun getAppVariantHelperFactory(): AppVariantHelperFactory {
        return component.appVariantHelperFactory()
    }

    fun getAndroidVariantContextFactory(): AndroidVariantContextFactory {
        return component.androidVariantContextFactory()
    }
}
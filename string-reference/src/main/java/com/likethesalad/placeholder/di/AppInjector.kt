package com.likethesalad.placeholder.di

import com.likethesalad.placeholder.utils.TaskActionProviderFactory
import com.likethesalad.placeholder.utils.VariantDataExtractorFactory

object AppInjector {

    private val component: AppComponent by lazy {
        DaggerAppComponent.builder().build()
    }

    fun getTaskActionProviderFactory(): TaskActionProviderFactory {
        return component.taskActionProviderFactory()
    }

    fun getVariantDataExtractorFactory(): VariantDataExtractorFactory {
        return component.variantDataExtractorFactory()
    }
}
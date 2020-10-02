package com.likethesalad.placeholder.di

import com.likethesalad.placeholder.utils.AppVariantHelperFactory
import com.likethesalad.placeholder.utils.TaskActionProviderFactory
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun taskActionProviderFactory(): TaskActionProviderFactory
    fun appVariantHelperFactory(): AppVariantHelperFactory
}
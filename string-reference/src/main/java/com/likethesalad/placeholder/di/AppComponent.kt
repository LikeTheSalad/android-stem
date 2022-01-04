package com.likethesalad.placeholder.di

import com.likethesalad.placeholder.modules.common.helpers.android.AndroidVariantContext
import com.likethesalad.placeholder.utils.TaskActionProviderHolder
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun taskActionProviderHolder(): TaskActionProviderHolder
    fun androidVariantContextFactory(): AndroidVariantContext.Factory
}
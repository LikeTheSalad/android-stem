package com.likethesalad.placeholder.di

import com.likethesalad.placeholder.utils.TaskActionProviderFactory
import com.likethesalad.placeholder.utils.VariantDataExtractorFactory
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component
interface AppComponent {
    fun taskActionProviderFactory(): TaskActionProviderFactory
    fun variantDataExtractorFactory(): VariantDataExtractorFactory
}
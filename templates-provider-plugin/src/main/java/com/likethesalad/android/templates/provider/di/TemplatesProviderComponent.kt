package com.likethesalad.android.templates.provider.di

import com.likethesalad.android.templates.common.tasks.identifier.action.TemplatesIdentifierAction
import com.likethesalad.android.templates.provider.tasks.service.action.TemplatesServiceGeneratorAction
import dagger.Component
import javax.inject.Singleton

@Component(modules = [TemplatesProviderModule::class])
@Singleton
interface TemplatesProviderComponent {
    fun templatesServiceGeneratorActionFactory(): TemplatesServiceGeneratorAction.Factory
    fun templatesIdentifierActionFactory(): TemplatesIdentifierAction.Factory
}
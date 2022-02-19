package com.likethesalad.android.templates.provider.di

object TemplatesProviderInjector {

    fun getComponent(): TemplatesProviderComponent {
        return DaggerTemplatesProviderComponent.builder().build()
    }
}
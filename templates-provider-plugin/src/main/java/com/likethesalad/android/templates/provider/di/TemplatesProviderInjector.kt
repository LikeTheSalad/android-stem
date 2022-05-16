package com.likethesalad.android.templates.provider.di

import com.likethesalad.android.templates.provider.TemplatesProviderPlugin

object TemplatesProviderInjector {

    private lateinit var component: TemplatesProviderComponent

    internal fun init(templatesProviderPlugin: TemplatesProviderPlugin) {
        component = DaggerTemplatesProviderComponent.builder()
            .templatesProviderModule(TemplatesProviderModule(templatesProviderPlugin))
            .build()
    }

    fun getComponent(): TemplatesProviderComponent {
        return component
    }
}
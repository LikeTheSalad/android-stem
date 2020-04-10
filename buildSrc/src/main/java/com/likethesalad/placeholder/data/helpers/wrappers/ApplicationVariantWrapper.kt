package com.likethesalad.placeholder.data.helpers.wrappers

import org.gradle.api.artifacts.Configuration

class ApplicationVariantWrapper(private val applicationVariant: Any) {

    private val clazz = applicationVariant.javaClass
    private val getNameMethod by lazy { clazz.getMethod("getName") }
    private val getFlavorNameMethod by lazy { clazz.getMethod("getFlavorName") }
    private val getProductFlavorsMethod by lazy { clazz.getMethod("getProductFlavors") }
    private val getBuildTypeMethod by lazy { clazz.getMethod("getBuildType") }
    private val getRuntimeConfigurationMethod by lazy { clazz.getMethod("getRuntimeConfiguration") }

    fun getName(): String {
        return getNameMethod.invoke(applicationVariant) as String
    }

    fun getFlavorName(): String {
        return getFlavorNameMethod.invoke(applicationVariant) as String
    }

    @Suppress("UNCHECKED_CAST")
    fun getProductFlavors(): List<ProductFlavorWrapper> {
        return (getProductFlavorsMethod.invoke(applicationVariant) as List<Any>).map { ProductFlavorWrapper(it) }
    }

    fun getBuildType(): AndroidBuildTypeWrapper {
        return AndroidBuildTypeWrapper(getBuildTypeMethod.invoke(applicationVariant))
    }

    fun getRuntimeConfiguration(): Configuration {
        return getRuntimeConfigurationMethod.invoke(applicationVariant) as Configuration
    }
}
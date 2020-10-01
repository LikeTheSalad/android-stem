package com.likethesalad.placeholder.utils

import com.android.build.gradle.api.ApplicationVariant
import com.google.auto.factory.AutoFactory
import org.gradle.api.artifacts.Configuration

@AutoFactory
class AppVariantHelper(private val variant: ApplicationVariant) {

    fun getVariantName(): String {
        return variant.name
    }

    fun getVariantType(): String {
        return variant.buildType.name
    }

    fun getVariantFlavors(): List<String> {
        return variant.productFlavors.map { it.name }
    }

    fun getRuntimeConfiguration(): Configuration {
        return variant.runtimeConfiguration
    }
}
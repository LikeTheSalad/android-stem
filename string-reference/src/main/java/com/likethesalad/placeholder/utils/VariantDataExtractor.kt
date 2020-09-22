package com.likethesalad.placeholder.utils

import com.likethesalad.placeholder.data.helpers.wrappers.ApplicationVariantWrapper
import org.gradle.api.artifacts.Configuration

@AutoFactory
class VariantDataExtractor(private val variantWrapper: ApplicationVariantWrapper) {

    fun getVariantName(): String {
        return variantWrapper.getName()
    }

    fun getVariantType(): String {
        return variantWrapper.getBuildType().getName()
    }

    fun getVariantFlavors(): List<String> {
        return variantWrapper.getProductFlavors().map { it.getName() }
    }

    fun getRuntimeConfiguration(): Configuration {
        return variantWrapper.getRuntimeConfiguration()
    }
}
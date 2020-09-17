package com.likethesalad.placeholder.data.helpers.wrappers.testutils

import org.gradle.api.artifacts.Configuration

class TestApplicationVariant(
    private val theName: String,
    private val theFlavorName: String,
    private val theProductFlavors: List<TestProductFlavor>,
    private val theBuildType: TestAndroidBuildType,
    private val runtimeConfiguration: Configuration
) {

    fun getName(): String {
        return theName
    }

    fun getFlavorName(): String {
        return theFlavorName
    }

    fun getProductFlavors(): List<TestProductFlavor> {
        return theProductFlavors
    }

    fun getBuildType(): TestAndroidBuildType {
        return theBuildType
    }

    fun getRuntimeConfiguration(): Configuration {
        return runtimeConfiguration
    }
}
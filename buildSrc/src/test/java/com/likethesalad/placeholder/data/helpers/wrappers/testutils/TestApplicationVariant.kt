package com.likethesalad.placeholder.data.helpers.wrappers.testutils

class TestApplicationVariant(
    private val theName: String,
    private val theFlavorName: String,
    private val theProductFlavors: List<TestProductFlavor>,
    private val theBuildType: TestAndroidBuildType
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
}
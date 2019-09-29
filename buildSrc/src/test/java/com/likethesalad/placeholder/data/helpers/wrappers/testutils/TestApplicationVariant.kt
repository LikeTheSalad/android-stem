package com.likethesalad.placeholder.data.helpers.wrappers.testutils

class TestApplicationVariant(private val theName: String, private val theFlavorName: String) {

    fun getName(): String {
        return theName
    }

    fun getFlavorName(): String {
        return theFlavorName
    }
}
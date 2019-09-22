package com.example.placeholder.data.helpers.wrappers

class ApplicationVariantWrapper(private val applicationVariant: Any) {

    private val clazz = applicationVariant.javaClass
    private val getNameMethod by lazy { clazz.getMethod("getName") }
    private val getFlavorNameMethod by lazy { clazz.getMethod("getFlavorName") }

    fun getName(): String {
        return getNameMethod.invoke(applicationVariant) as String
    }

    fun getFlavorName(): String {
        return getFlavorNameMethod.invoke(applicationVariant) as String
    }
}
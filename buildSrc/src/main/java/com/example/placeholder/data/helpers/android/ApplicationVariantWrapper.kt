package com.example.placeholder.data.helpers.android

class ApplicationVariantWrapper(private val applicationVariant: Any) {
    //    private val clazz = Class.forName("com.android.build.gradle.api.ApplicationVariant")
    private val clazz = applicationVariant.javaClass
    private val getNameMethod = clazz.getMethod("getName")
    private val getFlavorNameMethod = clazz.getMethod("getFlavorName")

    fun getName(): String {
        return getNameMethod.invoke(applicationVariant) as String
    }

    fun getFlavorName(): String {
        return getFlavorNameMethod.invoke(applicationVariant) as String
    }
}
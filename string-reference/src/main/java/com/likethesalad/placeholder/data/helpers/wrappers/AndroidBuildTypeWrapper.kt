package com.likethesalad.placeholder.data.helpers.wrappers

data class AndroidBuildTypeWrapper(private val androidBuildType: Any) {

    private val clazz = androidBuildType.javaClass
    private val getNameMethod by lazy { clazz.getMethod("getName") }

    fun getName(): String {
        return getNameMethod.invoke(androidBuildType) as String
    }
}
package com.example.placeholder.data.helpers.android

class AndroidSourceSetWrapper(private val androidSourceSet: Any) {

    //    private val clazz = Class.forName("com.android.build.gradle.api.AndroidSourceSet")
    private val clazz = androidSourceSet.javaClass

    private val getResMethod = clazz.getMethod("getRes")
    private val getNameMethod = clazz.getMethod("getName")

    fun getRes(): AndroidSourceDirectorySetWrapper {
        return AndroidSourceDirectorySetWrapper(getResMethod.invoke(androidSourceSet))
    }

    fun getName(): String {
        return getNameMethod.invoke(androidSourceSet) as String
    }
}
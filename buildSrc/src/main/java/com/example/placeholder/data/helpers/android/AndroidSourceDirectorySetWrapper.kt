package com.example.placeholder.data.helpers.android

import java.io.File

@Suppress("UNCHECKED_CAST")
class AndroidSourceDirectorySetWrapper(private val androidSourceDirectorySet: Any) {

    //    private val clazz: Class<*> = Class.forName("com.android.build.gradle.api.AndroidSourceDirectorySet")
    private val clazz: Class<*> = androidSourceDirectorySet.javaClass

    private val getSrcDirsMethod = clazz.getDeclaredMethod("getSrcDirs")

    fun getSrcDirs(): Set<File> {
        return getSrcDirsMethod.invoke(androidSourceDirectorySet) as Set<File>
    }
}
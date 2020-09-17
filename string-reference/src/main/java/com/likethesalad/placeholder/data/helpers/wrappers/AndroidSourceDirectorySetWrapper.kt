package com.likethesalad.placeholder.data.helpers.wrappers

import java.io.File

@Suppress("UNCHECKED_CAST")
class AndroidSourceDirectorySetWrapper(private val androidSourceDirectorySet: Any) {

    private val clazz: Class<*> = androidSourceDirectorySet.javaClass
    private val getSrcDirsMethod by lazy { clazz.getDeclaredMethod("getSrcDirs") }
    private val setSrcDirsMethod by lazy { clazz.getDeclaredMethod("setSrcDirs", Iterable::class.java) }

    fun getSrcDirs(): Set<File> {
        return getSrcDirsMethod.invoke(androidSourceDirectorySet) as Set<File>
    }

    fun setSrcDirs(srcDirs: Set<File>) {
        setSrcDirsMethod.invoke(androidSourceDirectorySet, srcDirs)
    }
}
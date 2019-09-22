package com.example.placeholder.data.helpers.android

import org.gradle.api.DomainObjectSet
import org.gradle.api.NamedDomainObjectContainer

class AndroidExtensionWrapper(private val androidExtension: Any) {

    private val clazz: Class<*> = androidExtension.javaClass
    private val getSourceSetsMethod by lazy { clazz.getMethod("getSourceSets") }
    private val getApplicationVariantsMethod by lazy { clazz.getMethod("getApplicationVariants") }

    fun getSourceSets(): Map<String, AndroidSourceSetWrapper> {
        val generic = getSourceSetsMethod.invoke(androidExtension) as NamedDomainObjectContainer<*>
        return generic.map {
            AndroidSourceSetWrapper(it)
        }.map { it.getName() to it }.toMap()
    }

    fun getApplicationVariants(): Set<ApplicationVariantWrapper> {
        return (getApplicationVariantsMethod.invoke(androidExtension) as DomainObjectSet<*>).map {
            ApplicationVariantWrapper(it)
        }.toSet()
    }
}
package com.likethesalad.placeholder.data.helpers.wrappers.testutils

import org.gradle.api.DomainObjectSet
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Namer
import org.gradle.api.internal.AbstractNamedDomainObjectContainer
import org.gradle.api.internal.DefaultDomainObjectSet
import org.gradle.api.internal.collections.ListElementSource
import org.gradle.internal.reflect.Instantiator

class TestAndroidExtension(
    private val theSourceSets: Set<TestAndroidSourceSet>,
    private val theApplicationVariants: Set<TestApplicationVariant>
) {
    private val sourceSetsLazy: NamedDomainObjectContainer<TestAndroidSourceSet> by lazy {
        TestNamedDomainObjectContainer(theSourceSets)
    }

    private val applicationVariantsLazy: DomainObjectSet<TestApplicationVariant> by lazy {
        val listElementSource = ListElementSource<TestApplicationVariant>()
        for (it in theApplicationVariants) {
            listElementSource.add(it)
        }
        DefaultDomainObjectSet(TestApplicationVariant::class.java, listElementSource)
    }

    fun getSourceSets(): NamedDomainObjectContainer<TestAndroidSourceSet> {
        return sourceSetsLazy
    }

    fun getApplicationVariants(): DomainObjectSet<TestApplicationVariant> {
        return applicationVariantsLazy
    }

    class TestNamedDomainObjectContainer(sourceSets: Set<TestAndroidSourceSet>) :
        AbstractNamedDomainObjectContainer<TestAndroidSourceSet>(
            TestAndroidSourceSet::class.java,
            object : Instantiator {
                override fun <T : Any?> newInstance(type: Class<out T>?, vararg parameters: Any?): T? {
                    return null
                }
            },
            Namer<TestAndroidSourceSet> { `object` -> `object`.getName() }) {

        init {
            addAll(sourceSets)
        }

        override fun doCreate(name: String): TestAndroidSourceSet? {
            return null
        }
    }
}
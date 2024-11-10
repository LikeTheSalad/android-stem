package com.likethesalad.android.templates.common.plugins.extension

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

open class StemExtension @Inject constructor(objectFactory: ObjectFactory) {
    val includeLocalizedOnlyTemplates: Property<Boolean> = objectFactory.property(Boolean::class.java)
    val placeholder: Placeholder = objectFactory.newInstance(Placeholder::class.java)

    init {
        includeLocalizedOnlyTemplates.convention(false)
        placeholder.getStart().convention("\${")
        placeholder.getEnd().convention("}")
    }

    fun placeholder(action: Action<Placeholder>) {
        action.execute(placeholder)
    }

    interface Placeholder {
        fun getStart(): Property<String>
        fun getEnd(): Property<String>
    }
}
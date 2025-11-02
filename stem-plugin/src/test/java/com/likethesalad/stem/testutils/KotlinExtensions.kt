package com.likethesalad.stem.testutils

import com.likethesalad.android.protos.Attribute
import com.likethesalad.android.protos.StringResource
import com.likethesalad.stem.configuration.StemConfiguration
import java.util.Locale

fun StemConfiguration.Companion.createForTest(
    placeholderStart: String = "\${",
    placeholderEnd: String = "}",
    includeLocalizedOnlyTemplates: Boolean = false
): StemConfiguration {
    return StemConfiguration({ placeholderStart }, { placeholderEnd }, { includeLocalizedOnlyTemplates })
}

fun StringResource.Companion.named(
    name: String,
    text: String,
    attributes: List<Attribute> = emptyList()
): StringResource {
    val finalAttributes = mutableListOf<Attribute>()
    finalAttributes.add(Attribute("name", name))
    finalAttributes.addAll(attributes)
    return StringResource(text, finalAttributes)
}

fun String.upperFirst(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.US
        ) else it.toString()
    }
}
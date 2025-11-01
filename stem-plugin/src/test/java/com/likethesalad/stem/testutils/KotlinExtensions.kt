package com.likethesalad.stem.testutils

import com.likethesalad.android.protos.Attribute
import com.likethesalad.android.protos.StringResource
import com.likethesalad.stem.configuration.StemConfiguration

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
    return StringResource(text, attributes)
}
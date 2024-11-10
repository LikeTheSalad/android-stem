package com.likethesalad.stem.functionaltest.testtools

import com.likethesalad.tools.functional.testing.blocks.GradleBlockItem

class StemConfigBlock(
    private val includeLocalizedOnlyTemplates: Boolean? = null,
    private val placeholderBlock: PlaceholderBlock? = null
) : GradleBlockItem {

    override fun getItemText(): String {
        if (includeLocalizedOnlyTemplates == null && placeholderBlock == null) {
            return ""
        }

        val builder = StringBuilder()

        builder.appendLine("androidStem {")
        if (includeLocalizedOnlyTemplates != null) {
            builder.appendLine("includeLocalizedOnlyTemplates = $includeLocalizedOnlyTemplates")
        }
        if (placeholderBlock != null) {
            builder.appendLine(placeholderBlock.getItemText())
        }
        builder.appendLine("}")

        return builder.toString()
    }
}
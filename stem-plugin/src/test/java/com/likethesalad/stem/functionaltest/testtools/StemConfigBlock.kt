package com.likethesalad.stem.functionaltest.testtools

import com.likethesalad.tools.functional.testing.blocks.GradleBlockItem

class StemConfigBlock(private val includeLocalizedOnlyTemplates: Boolean? = null) : GradleBlockItem {

    override fun getItemText(): String {
        if (includeLocalizedOnlyTemplates == null) {
            return ""
        }

        val builder = StringBuilder()

        builder.appendLine("androidStem {")
        builder.appendLine("includeLocalizedOnlyTemplates = $includeLocalizedOnlyTemplates")
        builder.appendLine("}")

        return builder.toString()
    }
}
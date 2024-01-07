package com.likethesalad.stem.functionaltest.testtools

import com.likethesalad.tools.functional.testing.blocks.GradleBlockItem

class StemConfigBlock(private val includeLocalizedOnlyTemplates: Boolean) : GradleBlockItem {

    override fun getItemText(): String {
        return """
            androidStem {
                includeLocalizedOnlyTemplates = $includeLocalizedOnlyTemplates
            }
        """.trimIndent()
    }
}
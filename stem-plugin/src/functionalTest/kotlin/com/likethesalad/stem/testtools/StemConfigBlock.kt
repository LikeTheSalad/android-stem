package com.likethesalad.stem.testtools

import com.likethesalad.tools.functional.testing.layout.items.GradleBlockItem

class StemConfigBlock(private val includeLocalizedOnlyTemplates: Boolean) : GradleBlockItem {

    override fun getItemText(): String {
        return """
            androidStem {
                includeLocalizedOnlyTemplates = $includeLocalizedOnlyTemplates
            }
        """.trimIndent()
    }
}
package com.likethesalad.stem.functionaltest.testtools

import com.likethesalad.tools.functional.testing.blocks.GradleBlockItem

class PlaceholderBlock(private val start: String?=null, private val end: String?=null) : GradleBlockItem {

    override fun getItemText(): String {
        if (start == null && end == null) {
            return ""
        }

        val builder = StringBuilder()

        builder.appendLine("placeholder {")
        if (start != null) {
            builder.appendLine("start = \"$start\"")
        }
        if (end != null) {
            builder.appendLine("end = \"$end\"")
        }
        builder.appendLine("}")

        return builder.toString()
    }
}
package com.likethesalad.tools.blocks.impl.plugins

import com.likethesalad.tools.blocks.GradleBlockItem

class GradlePluginsBlockItem : GradleBlockItem {

    private val plugins = mutableListOf<GradlePluginDeclaration>()

    override fun getItemText(): String {
        return """
            plugins {
                ${plugins.joinToString("\n") { convertToStringDeclaration(it) }}
            }
        """.trimIndent()
    }

    private fun convertToStringDeclaration(declaration: GradlePluginDeclaration): String {
        var result = "id '${declaration.id}'"
        declaration.version?.let { version ->
            result += " version '$version'"
        }
        return result
    }

    fun addPlugin(plugin: GradlePluginDeclaration) {
        plugins.add(plugin)
    }
}
package com.likethesalad.tools.blocks.impl

import com.likethesalad.tools.blocks.GradleBlockItem

class GradleDependenciesBlockItem : GradleBlockItem {

    private val dependencies = mutableListOf<String>()

    override fun getItemText(): String {
        return """
            dependencies {
                ${dependencies.joinToString("\n")}
            }
        """.trimIndent()
    }

    fun addDependency(dependencyDeclaration: String) {
        dependencies.add(dependencyDeclaration)
    }
}
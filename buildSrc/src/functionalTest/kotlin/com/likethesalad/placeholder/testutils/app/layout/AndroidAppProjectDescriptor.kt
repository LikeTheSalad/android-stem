package com.likethesalad.placeholder.testutils.app.layout

import com.likethesalad.placeholder.testutils.base.layout.ProjectDescriptor

class AndroidAppProjectDescriptor(
    private val name: String,
    private val flavors: List<FlavorDescriptor> = emptyList()
) : ProjectDescriptor() {

    override fun getBuildGradleContents(): String {
        return """
            apply plugin: 'com.android.application'
            apply plugin: 'placeholder-resolver'
            
            android {
                compileSdkVersion = 28
                
                ${getFlavorsConfigText()}
            }
        """.trimIndent()
    }

    private fun getFlavorsConfigText(): String {
        if (flavors.isEmpty()) {
            return ""
        }

        return """
            flavorDimensions ${getFlavorDimensionsQuotedAndCommaSeparated()}
            
            productFlavors {
                ${getProductFlavorsBlockItems()}
            }
        """.trimIndent()
    }

    private fun getFlavorDimensionsQuotedAndCommaSeparated(): String {
        return flavors.map { "\"${it.dimension}\"" }.fold("") { accumulated, current ->
            if (accumulated.isEmpty()) current else "$accumulated, $current"
        }
    }

    private fun getProductFlavorsBlockItems(): String {
        var flavorsList = ""
        for (descriptor in flavors) {
            val dimension = descriptor.dimension
            for (flavorName in descriptor.flavorNames) {
                flavorsList += "\n${getProductFlavorDefinition(dimension, flavorName)}"
            }
        }

        return flavorsList
    }

    private fun getProductFlavorDefinition(dimension: String, flavorName: String): String {
        return """
            $flavorName {
                dimension = "$dimension"
            }
        """.trimIndent()
    }

    override fun getProjectName(): String = name

    data class FlavorDescriptor(val dimension: String, val flavorNames: List<String>)
}
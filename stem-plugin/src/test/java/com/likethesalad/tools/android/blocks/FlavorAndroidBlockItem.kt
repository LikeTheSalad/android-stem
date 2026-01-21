package com.likethesalad.tools.android.blocks

class FlavorAndroidBlockItem(private val flavors: List<FlavorDescriptor>) : AndroidBlockItem {

    override fun getItemText(): String {
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

    data class FlavorDescriptor(val dimension: String, val flavorNames: List<String>)
}
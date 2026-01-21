package com.likethesalad.tools.android.blocks

class DefaultConfigAndroidBlockItem(private val strings: Map<String, String>) : AndroidBlockItem {

    override fun getItemText(): String {
        return """
            defaultConfig {
                buildFeatures {
                    resValues = true
                }
                ${getGradleStrings()}
            }
        """.trimIndent()
    }

    private fun getGradleStrings(): String {
        val stringLines = mutableListOf<String>()
        strings.forEach { (name, value) ->
            stringLines.add(getGradleStringGenDeclaration(name, value))
        }

        return stringLines.fold("") { accumulated, current ->
            "$accumulated\n$current"
        }
    }

    private fun getGradleStringGenDeclaration(stringName: String, stringValue: String): String {
        return "resValue \"string\", \"$stringName\", \"\\\"$stringValue\\\"\""
    }
}
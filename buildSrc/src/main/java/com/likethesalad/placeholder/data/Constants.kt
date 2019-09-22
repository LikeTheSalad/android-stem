package com.likethesalad.placeholder.data

class Constants {
    companion object {
        const val TEMPLATE_STRING_PREFIX = "template_"
        val TEMPLATE_STRING_REGEX = Regex("^$TEMPLATE_STRING_PREFIX")
        val PLACEHOLDER_REGEX = Regex("\\$\\{([a-zA-Z0-9_]+)}")
        val TEMPLATE_AS_PLACEHOLDER_REGEX = Regex("\\\$\\{($TEMPLATE_STRING_PREFIX[a-zA-Z0-9_]+)}")

        const val XML_STRING_TAG = "string"
        const val XML_RESOURCES_TAG = "resources"
    }
}
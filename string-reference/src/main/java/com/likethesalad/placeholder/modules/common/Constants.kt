package com.likethesalad.placeholder.modules.common

class Constants {
    companion object {
        const val TEMPLATE_STRING_PREFIX = "template_"
        val TEMPLATE_STRING_PREFIX_REGEX = Regex("^$TEMPLATE_STRING_PREFIX")
        val PLACEHOLDER_REGEX = Regex("\\$\\{([a-zA-Z0-9_]+)}")
        val TEMPLATE_AS_PLACEHOLDER_REGEX = Regex("\\\$\\{($TEMPLATE_STRING_PREFIX[a-zA-Z0-9_]+)}")

        const val XML_STRING_TAG = "string"
        const val XML_RESOURCES_TAG = "resources"
        const val RESOLVED_FILE_NAME = "resolved.xml"
        const val TEMPLATE_BASE_FILE_NAME = "templates"
    }
}
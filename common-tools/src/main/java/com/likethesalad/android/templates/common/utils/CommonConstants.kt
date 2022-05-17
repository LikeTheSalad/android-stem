package com.likethesalad.android.templates.common.utils

import java.io.File
import java.util.regex.Matcher

object CommonConstants {
    val PLACEHOLDER_REGEX = Regex("\\$\\{([a-zA-Z0-9_]+)}")
    const val PROVIDER_PACKAGE_NAME = "com.likethesalad.android.templates.provider.implementation"
    const val RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME = "stem"
    val FILE_SEPARATOR_MATCHER: String = Matcher.quoteReplacement(File.separator)
}
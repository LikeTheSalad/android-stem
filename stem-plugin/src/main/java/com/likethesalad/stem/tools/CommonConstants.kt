package com.likethesalad.stem.tools

import java.io.File
import java.util.regex.Matcher

object CommonConstants {
    const val PROVIDER_PACKAGE_NAME = "com.likethesalad.android.templates.provider.implementation"
    const val RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME = "stem"
    val FILE_SEPARATOR_MATCHER: String = Matcher.quoteReplacement(File.separator)
}
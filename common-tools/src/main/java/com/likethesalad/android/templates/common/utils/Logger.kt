package com.likethesalad.android.templates.common.utils

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class Logger @AssistedInject constructor(
    @Assisted private val javaClass: Class<out Any>,
    private val gradleLogger: org.gradle.api.logging.Logger
) {

    @AssistedFactory
    interface Factory {
        fun create(javaClass: Class<out Any>): Logger
    }

    private val prefix by lazy {
        "[TBD] - ${javaClass.name} - "
    }

    fun debug(message: String, vararg args: Any) {
        gradleLogger.debug(getIdentifiedMessage(message), *args)
    }

    private fun getIdentifiedMessage(message: String): String {
        return "$prefix$message"
    }
}
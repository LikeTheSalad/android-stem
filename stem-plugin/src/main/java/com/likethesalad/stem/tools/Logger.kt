package com.likethesalad.stem.tools

class Logger private constructor(
    private val gradleLogger: org.gradle.api.logging.Logger,
    private val hostClass: Class<out Any>
) {

    companion object {
        private var gradleLogger: org.gradle.api.logging.Logger? = null

        fun init(gradleLogger: org.gradle.api.logging.Logger) {
            Companion.gradleLogger = gradleLogger
        }

        fun create(hostClass: Class<out Any>): Logger {
            return Logger(gradleLogger!!, hostClass)
        }
    }

    private val prefix by lazy {
        "[STEM] - ${hostClass.name} - "
    }

    fun debug(message: String, vararg args: Any) {
        gradleLogger.debug(getIdentifiedMessage(message), *args)
    }

    private fun getIdentifiedMessage(message: String): String {
        return "$prefix$message"
    }
}
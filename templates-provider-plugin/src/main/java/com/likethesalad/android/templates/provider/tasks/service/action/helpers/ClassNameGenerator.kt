package com.likethesalad.android.templates.provider.tasks.service.action.helpers

import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClassNameGenerator @Inject constructor(
    private val randomStringGenerator: RandomStringGenerator,
    private val base64Encoder: Base64.Encoder
) {

    companion object {
        const val PACKAGE_NAME = "com.likethesalad.android.templates.provider.implementation"
        private const val NAME_PREFIX = "A_"
        private val BASE64_TO_JAVA_IDENTIFIER = mapOf(
            "=" to "_",
            "+" to "€",
            "/" to "$"
        )
    }

    fun generate(identifier: String): String {
        val raw = "${randomStringGenerator.generate()}_$identifier"
        val encoded = base64Encoder.encodeToString(raw.toByteArray())
        val curated = clearBase64NonAlphanumerics(encoded)

        return "$PACKAGE_NAME.$NAME_PREFIX$curated"
    }

    private fun clearBase64NonAlphanumerics(encoded: String): String {
        return BASE64_TO_JAVA_IDENTIFIER.entries.fold(encoded) { text, entry ->
            text.replace(entry.key, entry.value)
        }
    }
}
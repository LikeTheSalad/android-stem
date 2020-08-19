package com.likethesalad.placeholder.testutils

import com.likethesalad.placeholder.testutils.base.ProjectDefinition

class AndroidAppProjectDefinition(private val name: String) : ProjectDefinition {

    override fun getBuildGradleContents(): String {
        return """
            apply plugin: 'com.android.application'
            apply plugin: 'placeholder-resolver'
            
            android {
                compileSdkVersion = 28
            }
        """.trimIndent()
    }

    override fun projectName(): String = name
}
package com.likethesalad.placeholder.testutils.app.layout

import com.likethesalad.placeholder.testutils.base.layout.ProjectDescriptor
import com.likethesalad.placeholder.testutils.data.ResolverPluginConfig

class AndroidAppProjectDescriptor(
    private val name: String,
    private val androidBlockItems: List<AndroidBlockItem> = emptyList(),
    private val dependencies: List<String> = emptyList(),
    private val resolverPluginConfig: ResolverPluginConfig = ResolverPluginConfig()
) : ProjectDescriptor() {

    override fun getBuildGradleContents(): String {
        return """
            apply plugin: 'com.android.application'
            apply plugin: 'placeholder-resolver'
            
            android {
                compileSdkVersion = 28
                
                ${placeAndroidBlockItems()}
            }
            
            stringXmlReference {
                resolveOnBuild = ${resolverPluginConfig.resolveOnBuild}
                keepResolvedFiles = ${resolverPluginConfig.keepResolvedFiles}
                useDependenciesRes = ${resolverPluginConfig.useDependenciesRes}
            }
            
            ${getDependenciesBlock()}
        """.trimIndent()
    }

    private fun placeAndroidBlockItems(): String {
        if (androidBlockItems.isEmpty()) {
            return ""
        }

        return androidBlockItems.map { it.getItemText() }
            .fold("") { accumulated, current ->
                "$accumulated\n$current"
            }
    }

    private fun getDependenciesBlock(): String {
        if (dependencies.isEmpty()) {
            return ""
        }

        return """
            dependencies {
                ${getDependenciesListed()}
            }
        """.trimIndent()
    }

    private fun getDependenciesListed(): String {
        return dependencies.fold("") { accumulated, current ->
            "$accumulated\n$current"
        }
    }

    override fun getProjectName(): String = name
}
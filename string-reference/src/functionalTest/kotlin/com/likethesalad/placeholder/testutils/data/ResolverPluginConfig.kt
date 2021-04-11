package com.likethesalad.placeholder.testutils.data

data class ResolverPluginConfig(
    val resolveOnBuild: Boolean = true,
    val keepResolvedFiles: Boolean = false,
    val useDependenciesRes: Boolean = false
)
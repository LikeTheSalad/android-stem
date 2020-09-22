package com.likethesalad.placeholder.utils


class ConfigurationProvider {

    fun keepResolvedFiles(): Boolean {
        return false//todo get from extension
    }

    fun useDependenciesRes(): Boolean {
        return false
    }
}
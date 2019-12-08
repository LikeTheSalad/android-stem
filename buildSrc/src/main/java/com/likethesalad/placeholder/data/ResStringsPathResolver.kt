package com.likethesalad.placeholder.data

class ResStringsPathResolver(
    flavors: List<String>, suffix: String
) {
    private val capitalizedSuffix = suffix.capitalize()
    private val localFlavors = setOf("main") + flavors

    fun getPath(): List<String> {
        val pathList = mutableListOf<String>()
        for (flavor in localFlavors) {
            addFlavorPathsToList(flavor, pathList)
        }
        return pathList
    }

    private fun addFlavorPathsToList(flavorName: String, list: MutableList<String>) {
        list.add(flavorName)
        list.add("$flavorName$capitalizedSuffix")
    }

}

package com.likethesalad.placeholder.models

data class StringsGatheredModel(
    val suffix: String,
    val mainStrings: List<StringResourceModel>,
    val complementaryStrings: List<List<StringResourceModel>>
) {

    fun getMergedStrings(): Map<String, StringResourceModel> {
        val map = mutableMapOf<String, StringResourceModel>()
        for (list in complementaryStrings) {
            for (it in list) {
                map[it.name] = it
            }
        }
        for (it in mainStrings) {
            map[it.name] = it
        }
        return map
    }
}
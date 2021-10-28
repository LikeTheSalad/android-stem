package com.likethesalad.placeholder.modules.common.helpers.resources.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.likethesalad.tools.resource.api.android.environment.Language
import com.likethesalad.tools.resource.api.android.environment.Variant
import java.lang.reflect.Type

class LanguageTypeAdapter : JsonSerializer<Language>, JsonDeserializer<Language> {

    override fun serialize(src: Language, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.id)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Language {
        return Language.fromId(json.asString)
    }
}

class VariantTypeAdapter : JsonSerializer<Variant>, JsonDeserializer<Variant> {

    override fun serialize(src: Variant, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.name)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Variant {
        return Variant.fromId(json.asString)
    }
}
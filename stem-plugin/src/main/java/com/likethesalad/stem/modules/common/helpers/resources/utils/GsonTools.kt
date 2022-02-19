package com.likethesalad.stem.modules.common.helpers.resources.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.likethesalad.tools.resource.api.android.environment.Language
import com.likethesalad.tools.resource.api.android.modules.string.StringAndroidResource
import com.likethesalad.tools.resource.serializer.ResourceSerializer
import java.lang.reflect.Type

class LanguageTypeAdapter : JsonSerializer<Language>, JsonDeserializer<Language> {

    override fun serialize(src: Language, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.id)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Language {
        return Language.fromId(json.asString)
    }
}

class StringResourceTypeAdapter(private val resourceSerializer: ResourceSerializer) :
    JsonSerializer<StringAndroidResource>, JsonDeserializer<StringAndroidResource> {

    override fun serialize(
        src: StringAndroidResource,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(resourceSerializer.serialize(src))
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): StringAndroidResource {
        return resourceSerializer.deserialize(json.asString) as StringAndroidResource
    }
}
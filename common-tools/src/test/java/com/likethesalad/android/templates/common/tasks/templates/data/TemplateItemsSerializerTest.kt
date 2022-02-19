package com.likethesalad.android.templates.common.tasks.templates.data

import com.google.common.truth.Truth
import com.likethesalad.android.templates.common.tasks.identifier.data.TemplateItem
import com.likethesalad.android.templates.common.tasks.identifier.data.TemplateItemsSerializer
import org.junit.Test

class TemplateItemsSerializerTest {

    private val templateItemsSerializer = TemplateItemsSerializer()

    @Test
    fun `Convert template items to string`() {
        val item1 = TemplateItem("template1", "string")
        val item2 = TemplateItem("template2", "integer")

        val result = templateItemsSerializer.serialize(listOf(item1, item2))

        Truth.assertThat(result).isEqualTo("template1:string,template2:integer")
    }

    @Test
    fun `Avoid duplicates when converting items to string`() {
        val item1 = TemplateItem("template1", "string")
        val item2 = TemplateItem("template2", "integer")
        val item3 = TemplateItem("template1", "string")

        val result = templateItemsSerializer.serialize(listOf(item1, item2, item3))

        Truth.assertThat(result).isEqualTo("template1:string,template2:integer")
    }

    @Test
    fun `Convert string to template items`() {
        val string = "template2:string,template5:integer,template1:dimen"

        val result = templateItemsSerializer.deserialize(string)

        Truth.assertThat(result).containsExactly(
            TemplateItem("template2", "string"),
            TemplateItem("template5", "integer"),
            TemplateItem("template1", "dimen")
        )
    }

    @Test
    fun `Get empty list when deserializing empty string`() {
        val result = templateItemsSerializer.deserialize("")

        Truth.assertThat(result).isEmpty()
    }
}
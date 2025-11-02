package com.likethesalad.stem.modules.common.helpers.files

import com.google.common.truth.Truth
import com.likethesalad.android.protos.Attribute
import com.likethesalad.android.protos.StringResource
import com.likethesalad.stem.testutils.named
import java.io.File
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.InputSource

class AndroidXmlResDocumentTest {

    @TempDir
    lateinit var temporaryFolder: File

    private val documentBuilderFactory = DocumentBuilderFactory.newInstance().apply { isNamespaceAware = true }
    private val docBuilder = documentBuilderFactory.newDocumentBuilder()

    @Test
    fun checkEmptyConstructor_should_create_resources() {
        // When
        val androidXmlResDocument = AndroidXmlResDocument()

        // Then
        val resourcesList = androidXmlResDocument.document.getElementsByTagName("resources")
        Truth.assertThat(resourcesList.length).isEqualTo(1)
        Truth.assertThat(androidXmlResDocument.document.xmlStandalone).isTrue()
    }

    @Test
    fun checkDocumentConstructor_should_get_resources() {
        // Given
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        val resources = document.createElement("resources")
        document.appendChild(resources)

        // When
        val androidXmlResDocument = AndroidXmlResDocument(document)

        // Then
        Truth.assertThat(androidXmlResDocument.resources).isEqualTo(resources)
    }

    @Test
    fun checkAppend() {
        // Given
        val stringItem = createXmlElement("<string>some string</string>")

        val androidXmlResDocument = createDocument("<resources></resources>")

        // When
        androidXmlResDocument.append(stringItem)

        // Then
        assertThat(androidXmlResDocument.getStringList().length).isEqualTo(1)
    }

    @Test
    fun checkAppendStringResource() {
        // Given
        val stringResourceModel = StringResource.named(
            "some_name",
            "some content"
        )
        val androidXmlResDocument = createDocument("<resources></resources>")

        // When
        androidXmlResDocument.appendStringResource(stringResourceModel)

        // Then
        assertThat(androidXmlResDocument.getStringList().length).isEqualTo(1)
    }

    @Test
    fun checkAppendAll() {
        // Given
        val sourceDocument = createDocument(
            """
            <resources>
                <string>some string</string>
                <string>some other string</string>
            </resources>
        """.trimIndent()
        )
        val destDocument = createDocument("<resources></resources>")

        // When
        destDocument.appendAll(sourceDocument.getStringList())

        // Then
        assertThat(destDocument.getStringList().length).isEqualTo(2)
    }

    @Test
    fun checkAppendAllStringResources() {
        // Given
        val androidXmlResDocument = createDocument("<resources></resources>")
        val stringResources = listOf(
            StringResource.named("one string", "one value"),
            StringResource.named("another string", "another value")
        )

        // When
        androidXmlResDocument.appendAllStringResources(stringResources)

        // Then
        assertThat(androidXmlResDocument.getStringList().length).isEqualTo(2)
    }

    @Test
    fun checkGetStringList() {
        // Given
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        val resources: Element = document.createElement("resources")
        document.appendChild(resources)
        resources.run {
            appendChild(createStringNode("some_name", "some content", document))
            appendChild(createStringNode("some_name2", "some content2", document))
            appendChild(createStringNode("some_name3", "some content3", document))
        }
        val androidXmlResDocument =
            AndroidXmlResDocument(document)

        // When
        val result = androidXmlResDocument.getStringList()

        // Then
        Truth.assertThat(result.length).isEqualTo(3)
        Truth.assertThat(result.item(0).attributes.getNamedItem("name").textContent)
            .isEqualTo("some_name")
        Truth.assertThat(result.item(1).attributes.getNamedItem("name").textContent)
            .isEqualTo("some_name2")
        Truth.assertThat(result.item(2).attributes.getNamedItem("name").textContent)
            .isEqualTo("some_name3")
    }

    @Test
    fun checkSaveToFile() {
        // Given
        val androidXmlResDocument =
            AndroidXmlResDocument()
        val file = File(temporaryFolder, "temp_file")
        androidXmlResDocument.appendStringResource(
            StringResource.named(
                "some_name1",
                "some content1"
            )
        )
        androidXmlResDocument.appendStringResource(
            StringResource.named(
                "some_name2",
                "some content2"
            )
        )
        androidXmlResDocument.appendStringResource(
            StringResource.named(
                "some_name3",
                "some content3"
            )
        )

        // When
        androidXmlResDocument.saveToFile(file)

        // Then
        Truth.assertThat(file.readText()).isEqualTo(
            """
            <resources><string name="some_name1">some content1</string><string name="some_name2">some content2</string><string name="some_name3">some content3</string></resources>
            """.trimIndent()
        )
    }

    @Test
    fun checkSaveToFile_indent() {
        // Given
        val androidXmlResDocument =
            AndroidXmlResDocument()
        val file = File(temporaryFolder, "temp_file")
        androidXmlResDocument.appendStringResource(
            StringResource.named(
                "some_name1",
                "some content1"
            )
        )
        androidXmlResDocument.appendStringResource(
            StringResource.named(
                "some_name2",
                "some content2"
            )
        )
        androidXmlResDocument.appendStringResource(
            StringResource.named(
                "some_name3",
                "some content3"
            )
        )

        // When
        androidXmlResDocument.saveToFile(file)

        // Then
        Truth.assertThat(file.readText()).isEqualTo(
            """
            <resources><string name="some_name1">some content1</string><string name="some_name2">some content2</string><string name="some_name3">some content3</string></resources>
            """.trimIndent()
        )
    }

    @Test
    fun `verify custom prefixes`() {
        val document = AndroidXmlResDocument()
        val outputFile = File(temporaryFolder, "temp_file")
        val namespace = "http://some.namespace.com/"
        val resource = StringResource.named(
            "someName", "someValue",
            listOf(Attribute("someAttr", "someAttrValue", namespace))
        )
        document.appendStringResource(resource)

        document.saveToFile(outputFile)

        Truth.assertThat(outputFile.readText()).isEqualTo(
            """
                <resources xmlns:ns1="$namespace"><string name="someName" ns1:someAttr="someAttrValue">someValue</string></resources>
            """.trimIndent()
        )
    }

    @Test
    fun `verify custom prefixes reused`() {
        val document = AndroidXmlResDocument()
        val outputFile = File(temporaryFolder, "temp_file")
        val namespace = "http://some.namespace.com/"
        val resource = StringResource.named(
            "someName", "someValue", listOf(
                Attribute("someAttr", "someAttrValue", namespace),
                Attribute("someAttr2", "someOtherAttrValue", namespace)
            )
        )
        val resource2 = StringResource.named(
            "someOtherName", "someOtherValue", listOf(
                Attribute("someAttr3", "Some attr3 value", namespace)
            )
        )
        document.appendStringResource(resource)
        document.appendStringResource(resource2)

        document.saveToFile(outputFile)

        Truth.assertThat(outputFile.readText()).isEqualTo(
            """
                <resources xmlns:ns1="$namespace"><string name="someName" ns1:someAttr="someAttrValue" ns1:someAttr2="someOtherAttrValue">someValue</string><string name="someOtherName" ns1:someAttr3="Some attr3 value">someOtherValue</string></resources>
            """.trimIndent()
        )
    }

    @Test
    fun `verify multipl prefixes`() {
        val document = AndroidXmlResDocument()
        val outputFile = File(temporaryFolder, "temp_file")
        val namespace = "http://some.namespace.com/"
        val namespace2 = "http://someother.namespace.com/"
        val resource = StringResource.named(
            "someName", "someValue", listOf(
                Attribute("someAttr", "someAttrValue", namespace),
                Attribute("someAttr2", "someOtherAttrValue", namespace)
            )
        )
        val resource2 = StringResource.named(
            "someOtherName", "someOtherValue", listOf(
                Attribute("someAttr3", "Some attr3 value", namespace),
                Attribute("someAttr3", "Some attr3 namespace2 value", namespace2)
            )
        )
        document.appendStringResource(resource)
        document.appendStringResource(resource2)

        document.saveToFile(outputFile)

        Truth.assertThat(outputFile.readText()).isEqualTo(
            """
                <resources xmlns:ns1="$namespace" xmlns:ns2="$namespace2"><string name="someName" ns1:someAttr="someAttrValue" ns1:someAttr2="someOtherAttrValue">someValue</string><string name="someOtherName" ns1:someAttr3="Some attr3 value" ns2:someAttr3="Some attr3 namespace2 value">someOtherValue</string></resources>
            """.trimIndent()
        )
    }

    private fun createStringNode(name: String, content: String, document: Document): Node {
        val node = document.createElement("string")
        node.setAttribute("name", name)
        node.textContent = content
        return node
    }

    private fun createDocument(contents: String): AndroidXmlResDocument {
        val xmlInput = InputSource(StringReader(contents))
        return AndroidXmlResDocument(docBuilder.parse(xmlInput))
    }

    private fun createXmlElement(contents: String): Element {
        val reader = StringReader(contents)
        return docBuilder.parse(InputSource(reader)).documentElement
    }
}
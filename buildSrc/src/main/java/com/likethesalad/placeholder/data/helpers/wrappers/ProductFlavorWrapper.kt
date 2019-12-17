package com.likethesalad.placeholder.data.helpers.wrappers

data class ProductFlavorWrapper(private val productFlavor: Any) {
    private val clazz = productFlavor.javaClass
    private val getNameMethod by lazy { clazz.getMethod("getName") }

    fun getName(): String {
        return getNameMethod.invoke(productFlavor) as String
    }
}
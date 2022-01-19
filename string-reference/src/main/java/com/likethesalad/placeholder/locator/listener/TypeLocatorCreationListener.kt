package com.likethesalad.placeholder.locator.listener

import com.likethesalad.tools.resource.collector.android.data.variant.VariantTree
import com.likethesalad.tools.resource.locator.android.extension.configuration.data.ResourceLocatorInfo
import com.likethesalad.tools.resource.locator.android.extension.listener.ResourceLocatorCreationListener

class TypeLocatorCreationListener(
    types: Set<String>,
    private val callback: Callback
) : ResourceLocatorCreationListener {

    private val totalLocatorsPerVariant = types.size
    private val locatorsPerVariant = mutableMapOf<VariantTree, MutableMap<String, ResourceLocatorInfo>>()

    override fun onLocatorReady(type: String, variantTree: VariantTree, info: ResourceLocatorInfo) {
        val locatorsPerVariant = getLocatorsPerVariant(variantTree)
        addLocatorInfo(variantTree, locatorsPerVariant, type, info)
        notifyIfLocatorsAreComplete(variantTree, locatorsPerVariant)
    }

    private fun addLocatorInfo(
        variantTree: VariantTree,
        locatorsPerVariant: MutableMap<String, ResourceLocatorInfo>,
        type: String,
        info: ResourceLocatorInfo
    ) {
        if (locatorsPerVariant.size == totalLocatorsPerVariant) {
            throw IllegalStateException("Variant already completed: ${variantTree.androidVariantData.getVariantName()}")
        }
        locatorsPerVariant[type] = info
    }

    private fun notifyIfLocatorsAreComplete(
        variantTree: VariantTree,
        locatorsPerVariant: MutableMap<String, ResourceLocatorInfo>
    ) {
        if (locatorsPerVariant.size < totalLocatorsPerVariant) {
            return
        }

        callback.onLocatorsReady(variantTree, locatorsPerVariant)
    }

    private fun getLocatorsPerVariant(variantTree: VariantTree): MutableMap<String, ResourceLocatorInfo> {
        if (!locatorsPerVariant.containsKey(variantTree)) {
            locatorsPerVariant[variantTree] = mutableMapOf()
        }

        return locatorsPerVariant.getValue(variantTree)
    }

    interface Callback {
        fun onLocatorsReady(variantTree: VariantTree, locatorsByType: Map<String, ResourceLocatorInfo>)
    }
}
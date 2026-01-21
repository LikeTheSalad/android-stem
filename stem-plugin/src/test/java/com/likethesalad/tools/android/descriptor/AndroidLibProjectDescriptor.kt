package com.likethesalad.tools.android.descriptor

import com.likethesalad.tools.blocks.GradleBlockItem
import java.io.File

class AndroidLibProjectDescriptor(
    name: String,
    inputDir: File,
    blockItems: List<GradleBlockItem> = emptyList(),
    namespace: String = "some.localpackage.localname.forlocal.$name",
    compileSdkVersion: Int = 28
) : AndroidProjectDescriptor(name, inputDir, "com.android.library", blockItems, namespace, compileSdkVersion)
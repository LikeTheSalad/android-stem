package com.likethesalad.stem.testutils

import com.likethesalad.android.templates.common.configuration.StemConfiguration


fun StemConfiguration.Companion.createForTest(
    placeholderStart: String = "\${",
    placeholderEnd: String = "}",
    includeLocalizedOnlyTemplates: Boolean = false
): StemConfiguration {
    return StemConfiguration({ placeholderStart }, { placeholderEnd }, { includeLocalizedOnlyTemplates })
}
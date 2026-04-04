package com.likethesalad.stem.modules.common

import org.gradle.api.DefaultTask
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Not worth caching")
open class BaseTask : DefaultTask() {
    init {
        group = "stem"
    }
}
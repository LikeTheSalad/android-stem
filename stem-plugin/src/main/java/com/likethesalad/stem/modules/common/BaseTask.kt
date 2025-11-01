package com.likethesalad.stem.modules.common

import org.gradle.api.DefaultTask

open class BaseTask : DefaultTask() {
    init {
        group = "stem"
    }
}
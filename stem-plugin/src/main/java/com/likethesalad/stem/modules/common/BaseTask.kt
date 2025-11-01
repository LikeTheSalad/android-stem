package com.likethesalad.stem.modules.common

import com.likethesalad.stem.tools.CommonConstants
import org.gradle.api.DefaultTask

open class BaseTask : DefaultTask() {
    init {
        group = CommonConstants.RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME
    }
}
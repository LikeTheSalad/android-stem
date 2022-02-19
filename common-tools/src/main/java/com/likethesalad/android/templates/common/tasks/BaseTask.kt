package com.likethesalad.android.templates.common.tasks

import com.likethesalad.android.templates.common.utils.CommonConstants
import org.gradle.api.DefaultTask

open class BaseTask : DefaultTask() {
    init {
        group = CommonConstants.RESOLVE_PLACEHOLDERS_TASKS_GROUP_NAME
    }
}
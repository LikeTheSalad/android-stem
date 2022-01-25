package com.likethesalad.placeholder.providers

import org.gradle.api.tasks.TaskContainer

interface TaskContainerProvider {
    fun getTaskContainer(): TaskContainer
}
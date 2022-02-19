package com.likethesalad.stem.providers

import org.gradle.api.tasks.TaskContainer

interface TaskContainerProvider {
    fun getTaskContainer(): TaskContainer
}
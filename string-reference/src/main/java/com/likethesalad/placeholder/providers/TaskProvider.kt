package com.likethesalad.placeholder.providers

import org.gradle.api.Task

interface TaskProvider {
    fun <T : Task> findTaskByName(name: String): T
}
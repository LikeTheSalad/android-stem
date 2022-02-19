package com.likethesalad.stem.providers

import org.gradle.api.Task

interface TaskProvider {
    fun <T : Task> findTaskByName(name: String): T
}
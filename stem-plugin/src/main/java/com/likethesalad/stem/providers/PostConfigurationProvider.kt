package com.likethesalad.stem.providers

import org.gradle.api.Action
import org.gradle.api.Project

interface PostConfigurationProvider {

    fun executeAfterEvaluate(action: Action<in Project>)
}
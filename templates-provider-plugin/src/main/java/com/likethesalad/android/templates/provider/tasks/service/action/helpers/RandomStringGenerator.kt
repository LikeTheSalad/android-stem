package com.likethesalad.android.templates.provider.tasks.service.action.helpers

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RandomStringGenerator @Inject constructor() {

    fun generate(): String {
        return UUID.randomUUID().toString()
    }
}
package io.kotest.core.factory

import java.util.UUID

actual fun uniqueId(): String = UUID.randomUUID().toString()

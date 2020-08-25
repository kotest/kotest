package io.kotest.mpp

import java.util.UUID

actual fun uniqueId(): String = UUID.randomUUID().toString()

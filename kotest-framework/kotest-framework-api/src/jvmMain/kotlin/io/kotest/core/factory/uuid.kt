package io.kotest.core.factory

import java.util.UUID

actual fun uuid(): String = UUID.randomUUID().toString()

package io.kotest.core.factory

import kotlin.random.Random

internal const val UUID_STRING_LENGTH = 32

internal val chars = ('a'..'z').toList() + ('0'..'9').toList()

actual fun uniqueId(): String = (List(UUID_STRING_LENGTH) { chars[Random.nextInt(36)] }).joinToString("")

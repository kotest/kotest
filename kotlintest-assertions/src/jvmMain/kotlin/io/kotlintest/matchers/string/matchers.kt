@file:JvmName("matchersJvm")

package io.kotlintest.matchers.string

import kotlin.text.isDigit as kotlinIsDigit

actual fun Char.isDigit() = this.kotlinIsDigit()
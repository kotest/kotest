package io.kotest.core

import io.kotest.fp.Option
import kotlin.reflect.KClass

actual fun KClass<*>.fqn(): Option<String> = Option.None
actual fun KClass<*>.displayName(): Option<String> = Option.None

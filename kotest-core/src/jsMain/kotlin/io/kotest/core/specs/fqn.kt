package io.kotest.core.specs

import io.kotest.core.fp.Option
import kotlin.reflect.KClass

actual fun KClass<*>.fqn(): Option<String> = Option.None

package io.kotest.core.specs

import io.kotest.core.fp.Option
import io.kotest.core.fp.toOption
import kotlin.reflect.KClass

actual fun KClass<*>.fqn(): Option<String> = this.qualifiedName.toOption()

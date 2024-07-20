package io.kotest.core.extensions

import kotlin.reflect.KClass

actual val ApplyExtension.wrapper: Array<out KClass<out Extension>>
   get() = extensions

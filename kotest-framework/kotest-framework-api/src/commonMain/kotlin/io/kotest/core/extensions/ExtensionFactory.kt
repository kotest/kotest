package io.kotest.core.extensions

import kotlin.reflect.KClass

interface ExtensionFactory {
   fun extension(spec: KClass<*>): Extension?
}

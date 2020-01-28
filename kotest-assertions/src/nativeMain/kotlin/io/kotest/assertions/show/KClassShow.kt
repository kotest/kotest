package io.kotest.assertions.show

import kotlin.reflect.KClass

actual fun KClassShow(): Show<KClass<*>> = object : Show<KClass<*>> {
   override fun show(a: KClass<*>): String = a.simpleName ?: a.toString()
}

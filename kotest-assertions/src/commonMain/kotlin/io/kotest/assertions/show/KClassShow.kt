package io.kotest.assertions.show

import io.kotest.assertions.bestName
import kotlin.reflect.KClass

object KClassShow: Show<KClass<*>> {
   override fun show(a: KClass<*>): String = a.bestName()
}

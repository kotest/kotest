package io.kotest.assertions.show

import io.kotest.mpp.bestName
import kotlin.reflect.KClass

object KClassShow: Show<KClass<*>> {
   override fun show(a: KClass<*>): String = a.bestName()
}

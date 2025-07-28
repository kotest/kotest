package io.kotest.assertions.print

import io.kotest.common.reflection.bestName
import kotlin.reflect.KClass

object KClassPrint: Print<KClass<*>> {
   override fun print(a: KClass<*>, level: Int): Printed = a.bestName().printed()
}

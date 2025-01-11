package io.kotest.assertions.print

import io.kotest.mpp.bestName
import kotlin.reflect.KClass

object KClassPrint: Print<KClass<*>> {
   override fun print(a: KClass<*>, level: Int): Printed = a.bestName().printed()
}

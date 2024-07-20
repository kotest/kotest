package io.kotest.assertions.print

import io.kotest.mpp.bestName
import kotlin.reflect.KClass

object KClassPrint: Print<KClass<*>> {
   @Deprecated(PRINT_DEPRECATION_MSG)
   override fun print(a: KClass<*>): Printed = a.bestName().printed()
}

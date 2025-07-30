package io.kotest.assertions.print

import io.kotest.common.reflection.bestName
import kotlin.reflect.KClass

/**
 * Prints a [KClass] using the qualified name of the class if available, otherwise the simple name.
 */
object KClassPrint : Print<KClass<*>> {
   override fun print(a: KClass<*>): Printed = Printed(a.bestName(), KClass::class)
}

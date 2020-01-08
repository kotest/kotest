package io.kotest.core

import io.kotest.SpecClass
import io.kotest.core.spec.SpecConfiguration
import kotlin.reflect.KClass

fun Description.Companion.fromSpecClass(klass: KClass<out SpecClass>): Description = spec(klass.bestName())

/**
 * Returns a spec level [Description] with the name derived from the class.
 */
fun KClass<out SpecConfiguration>.description() = Description.spec(bestName())

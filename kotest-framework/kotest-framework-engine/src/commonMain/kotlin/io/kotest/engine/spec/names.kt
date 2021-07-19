package io.kotest.engine.spec

import io.kotest.core.plan.displayName
import io.kotest.core.spec.Spec
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

/**
 * Returns the value of the @DisplayName annotation on JVM platforms, if present.
 * On other platforms, returns null.
 */
expect fun KClass<*>.displayName(): String?

fun KClass<*>.outputName() = displayName() ?: bestName()

internal class SpecNameFormatter {
   fun format(spec: Spec) = format(spec::class)
   fun format(kclass: KClass<*>): String = kclass.displayName().value
}

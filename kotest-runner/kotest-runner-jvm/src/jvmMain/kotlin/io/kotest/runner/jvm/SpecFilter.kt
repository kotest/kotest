package io.kotest.runner.jvm

import io.kotest.core.spec.SpecConfiguration
import kotlin.reflect.KClass

interface SpecFilter {
  fun invoke(klass: KClass<out SpecConfiguration>): Boolean
}

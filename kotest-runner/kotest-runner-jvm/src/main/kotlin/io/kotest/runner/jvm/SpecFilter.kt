package io.kotest.runner.jvm

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

interface SpecFilter {
  fun invoke(klass: KClass<out Spec>): Boolean
}

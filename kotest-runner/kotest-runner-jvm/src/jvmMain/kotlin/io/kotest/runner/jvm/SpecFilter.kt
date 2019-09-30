package io.kotest.runner.jvm

import io.kotest.Spec
import kotlin.reflect.KClass

interface SpecFilter {
  fun invoke(klass: KClass<out Spec>): Boolean
}
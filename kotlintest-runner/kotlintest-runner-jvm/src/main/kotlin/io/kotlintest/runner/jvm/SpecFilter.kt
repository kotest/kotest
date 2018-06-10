package io.kotlintest.runner.jvm

import io.kotlintest.Spec
import kotlin.reflect.KClass

interface SpecFilter {
  fun invoke(klass: KClass<out Spec>): Boolean
}
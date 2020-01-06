package io.kotest.runner.jvm

import io.kotest.SpecClass
import kotlin.reflect.KClass

interface SpecFilter {
  fun invoke(klass: KClass<out SpecClass>): Boolean
}

package io.kotest.runner.jvm

import io.kotest.core.specs.SpecContainer

interface SpecFilter {
  fun invoke(container: SpecContainer): Boolean
}

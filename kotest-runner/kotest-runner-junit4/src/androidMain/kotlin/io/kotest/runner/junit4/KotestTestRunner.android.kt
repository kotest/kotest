package io.kotest.runner.junit4

import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef

internal actual fun specRef(clazz: Class<out Spec>): SpecRef = SpecRef.Reference(clazz.kotlin, clazz.name)

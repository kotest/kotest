package io.kotest.core.listeners

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

interface SpecInstantiationListener : Listener {
   fun specInstantiated(spec: Spec) {}
   fun specInstantiationError(kclass: KClass<out Spec>, t: Throwable) {}
}

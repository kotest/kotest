package io.kotest.core.listeners

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

@Deprecated("Deprecated in favour of SpecInstantiationExtension which supports suspension. Deprecated since 5.0.")
interface SpecInstantiationListener : Listener {
   fun specInstantiated(spec: Spec) {}
   fun specInstantiationError(kclass: KClass<out Spec>, t: Throwable) {}
}

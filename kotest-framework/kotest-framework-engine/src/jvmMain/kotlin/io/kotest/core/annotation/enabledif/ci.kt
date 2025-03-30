package io.kotest.core.annotation.enabledif

import io.kotest.core.annotation.EnabledCondition
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

class CICondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = System.getenv("CI") == "true"
}

class NotCICondition : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = System.getenv("CI") != "true"
}

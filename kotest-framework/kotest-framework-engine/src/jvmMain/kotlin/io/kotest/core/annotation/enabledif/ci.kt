package io.kotest.core.annotation.enabledif

import io.kotest.core.annotation.Condition
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

private fun osName() = System.getenv("CI").lowercase()

class CICondition : Condition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean = System.getenv("CI") == "true"
}

class NotCICondition : Condition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean = System.getenv("CI") != "true"
}

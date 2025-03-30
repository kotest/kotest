package io.kotest.core.annotation.enabledif

import io.kotest.core.annotation.EnabledCondition
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

class CICondition : EnabledCondition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean = System.getenv("CI") == "true"
}

class NotCICondition : EnabledCondition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean = System.getenv("CI") != "true"
}

class GithubActionCondition : EnabledCondition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean = System.getenv("GITHUB_ACTION") != null
}

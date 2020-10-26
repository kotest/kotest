package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.Spec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.Description
import io.kotest.core.test.TestCaseConfig
import kotlin.reflect.KClass

interface RootScope {
   fun defaultConfig(): TestCaseConfig
   fun description(): Description = (this::class as KClass<out Spec>).toDescription()
   fun lifecycle(): Lifecycle
   fun registration(): RootTestRegistration
}

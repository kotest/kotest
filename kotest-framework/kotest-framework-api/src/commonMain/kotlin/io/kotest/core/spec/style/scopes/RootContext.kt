package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.Spec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.Description
import io.kotest.core.test.TestCaseConfig
import kotlin.reflect.KClass

@Deprecated("Renamed to RootContext. This alias will be removed in 4.8")
typealias RootScope = RootContext

interface RootContext {
   fun defaultConfig(): TestCaseConfig
   fun description(): Description = (this::class as KClass<out Spec>).toDescription()
   fun registration(): RootTestRegistration
}

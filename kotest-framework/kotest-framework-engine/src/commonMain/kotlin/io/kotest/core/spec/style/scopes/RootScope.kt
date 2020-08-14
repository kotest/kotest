package io.kotest.core.spec.style.scopes

import io.kotest.engine.spec.AbstractSpec
import io.kotest.core.test.Description
import io.kotest.core.test.TestCaseConfig
import io.kotest.engine.test.toDescription
import kotlin.reflect.KClass

interface RootScope {
   fun defaultConfig(): TestCaseConfig
   fun description(): Description = (this::class as KClass<out AbstractSpec>).toDescription()
   fun lifecycle(): Lifecycle
   fun registration(): RootTestRegistration
}

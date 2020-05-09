package io.kotest.core.spec.style.scopes

import io.kotest.core.test.Description
import io.kotest.core.test.TestCaseConfig

interface RootScope {
   fun defaultConfig(): TestCaseConfig
   fun description(): Description = Description.specUnsafe(this)
   fun lifecycle(): Lifecycle
   fun registration(): RootTestRegistration
}

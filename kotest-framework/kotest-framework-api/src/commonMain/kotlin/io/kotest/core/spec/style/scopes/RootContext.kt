package io.kotest.core.spec.style.scopes

import io.kotest.core.plan.Descriptor
import io.kotest.core.test.TestCaseConfig

interface RootContext {
   fun defaultConfig(): TestCaseConfig
   fun descriptor(): Descriptor.SpecDescriptor = Descriptor.SpecDescriptor(this::class)
   fun registration(): RootTestRegistration
}

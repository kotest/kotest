package io.kotest.core.spec.style.scopes

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.spec.Spec
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.test.TestCaseConfig
import kotlin.reflect.KClass

interface RootContext {
   fun defaultConfig(): TestCaseConfig
   fun description(): Descriptor = (this::class as KClass<out Spec>).toDescriptor()
   fun registration(): RootTestRegistration
}

package com.sksamuel.kotest.engine.spec.execution.enabled

import io.kotest.core.annotation.AlwaysFalseCondition
import io.kotest.core.annotation.AlwaysTrueCondition
import io.kotest.core.annotation.DisabledIf
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.execution.enabled.DisabledIfAnnotationSpecRefEnabledExtension
import io.kotest.engine.spec.execution.enabled.EnabledOrDisabled
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class DisabledIfAnnotationSpecRefEnabledExtensionTest : FunSpec({

   test("DisabledIfInterceptor should proceed for any spec not annotated with @DisabledIf") {
      DisabledIfAnnotationSpecRefEnabledExtension.isEnabled(SpecRef.Reference(UnannotatedSpec::class)) shouldBe EnabledOrDisabled.Enabled
   }

   test("DisabledIfInterceptor should proceed any spec annotated with @DisabledIf that fails predicate") {
      DisabledIfAnnotationSpecRefEnabledExtension.isEnabled(SpecRef.Reference(FalseSpec::class)) shouldBe EnabledOrDisabled.Enabled
   }

   test("DisabledIfInterceptor should skip any spec annotated with @DisabledIf that fails predicate") {
      DisabledIfAnnotationSpecRefEnabledExtension.isEnabled(SpecRef.Reference(TrueSpec::class)) shouldBe EnabledOrDisabled.Disabled("Disabled by @DisabledIf (AlwaysTrueCondition)")
   }
})

@DisabledIf(AlwaysTrueCondition::class)
private class TrueSpec : FunSpec()

@DisabledIf(AlwaysFalseCondition::class)
private class FalseSpec : FunSpec()

private class UnannotatedSpec : FunSpec()

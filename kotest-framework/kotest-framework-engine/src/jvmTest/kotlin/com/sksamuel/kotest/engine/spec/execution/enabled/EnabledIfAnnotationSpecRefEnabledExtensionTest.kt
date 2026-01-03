package com.sksamuel.kotest.engine.spec.execution.enabled

import io.kotest.core.annotation.AlwaysFalseCondition
import io.kotest.core.annotation.AlwaysTrueCondition
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.execution.enabled.EnabledIfAnnotationSpecRefEnabledExtension
import io.kotest.engine.spec.execution.enabled.EnabledOrDisabled
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class EnabledIfAnnotationSpecRefEnabledExtensionTest : FunSpec({

   test("EnabledIfSpecInterceptor should proceed for any spec not annotated with @EnabledIf") {
      EnabledIfAnnotationSpecRefEnabledExtension.isEnabled(SpecRef.Reference(MyUnannotatedSpec::class)) shouldBe EnabledOrDisabled.Enabled
   }

   test("EnabledIfSpecInterceptor should proceed any spec annotated with @EnabledIf that passes predicate") {
      EnabledIfAnnotationSpecRefEnabledExtension.isEnabled(SpecRef.Reference(MyEnabledSpec::class)) shouldBe EnabledOrDisabled.Enabled
   }

   test("EnabledIfSpecInterceptor should skip any spec annotated with @EnabledIf that fails predicate") {
      EnabledIfAnnotationSpecRefEnabledExtension.isEnabled(SpecRef.Reference(MyDisabledSpec::class)) shouldBe
         EnabledOrDisabled.Disabled("Disabled by @EnabledIf (AlwaysFalseCondition)")
   }
})

@EnabledIf(AlwaysTrueCondition::class)
private class MyEnabledSpec : FunSpec()

@EnabledIf(AlwaysFalseCondition::class)
private class MyDisabledSpec : FunSpec()

private class MyUnannotatedSpec : FunSpec()

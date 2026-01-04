package com.sksamuel.kotest.engine.spec.execution.enabled

import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Ignored
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.execution.enabled.EnabledOrDisabled
import io.kotest.engine.spec.execution.enabled.IgnoredAnnotationSpecRefEnabledExtension
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class IgnoredAnnotationSpecRefEnabledExtensionTest : FunSpec({

   test("IgnoredSpecInterceptor should pass any class not annotated with @Ignored") {
      IgnoredAnnotationSpecRefEnabledExtension.isEnabled(SpecRef.Reference(NotIgnoredSpec::class)) shouldBe EnabledOrDisabled.Enabled
   }

   test("IgnoredSpecInterceptor should skip any spec annotated with @Ignored") {
      IgnoredAnnotationSpecRefEnabledExtension.isEnabled(SpecRef.Reference(MyIgnoredSpec::class)) shouldBe EnabledOrDisabled.Disabled("Disabled by @Ignored")
   }

   test("IgnoredSpec should use reason from annotation when annotated") {
      IgnoredAnnotationSpecRefEnabledExtension.isEnabled(SpecRef.Reference(ReasonIgnoredSpec::class)) shouldBe EnabledOrDisabled.Disabled("Disabled by @Ignored(reason=\"it's a good reason!\")")
   }
})

private class NotIgnoredSpec : FunSpec()

@Ignored
private class MyIgnoredSpec : FunSpec()

@Ignored("it's a good reason!")
private class ReasonIgnoredSpec : FunSpec({
   test("boom") { AssertionErrorBuilder.fail("boom") }
})

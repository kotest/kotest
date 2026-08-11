package com.sksamuel.kotest.engine.spec.dsl

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.AbstractSpec
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.TestDefinitionBuilder
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.TestRunnable
import io.kotest.core.test.AbstractTestScope
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class CustomSpecStyleTest : FunSpec({

   test("custom spec style executes all tests without errors") {
      val listener = CollectingTestEngineListener()
      TestEngineLauncher().withListener(listener)
         .withSpecRefs(SpecRef.Reference(MySuiteSpec::class))
         .withoutEnvFilters()
         .execute()
      listener.errors shouldBe false
      listener.names shouldBe listOf("root test", "nested test", "deeply nested test", "inner suite", "outer suite")
   }

   test("custom spec style leaf test passes") {
      val listener = CollectingTestEngineListener()
      TestEngineLauncher().withListener(listener)
         .withSpecRefs(SpecRef.Reference(MySuiteSpec::class))
         .withoutEnvFilters()
         .execute()
      listener.result("root test")!!.isSuccess shouldBe true
   }

   test("custom spec style nested test passes") {
      val listener = CollectingTestEngineListener()
      TestEngineLauncher().withListener(listener)
         .withSpecRefs(SpecRef.Reference(MySuiteSpec::class))
         .withoutEnvFilters()
         .execute()
      listener.result("nested test")!!.isSuccess shouldBe true
   }

   test("custom spec style supports multiple nesting levels") {
      val listener = CollectingTestEngineListener()
      TestEngineLauncher().withListener(listener)
         .withSpecRefs(SpecRef.Reference(MySuiteSpec::class))
         .withoutEnvFilters()
         .execute()
      listener.result("deeply nested test")!!.isSuccess shouldBe true
   }
})

private class MySuiteSpec : SuiteSpec() {
   init {
      test("root test") {}

      suite("outer suite") {
         test("nested test") {}

         suite("inner suite") {
            test("deeply nested test") {}
         }
      }
   }
}

private abstract class SuiteSpec(body: SuiteSpec.() -> Unit = {}) : AbstractSpec() {

   init {
      body()
   }

   @TestRunnable
   fun suite(name: String, test: suspend SuiteScope.() -> Unit) {
      add(
         TestDefinitionBuilder
            .builder(TestNameBuilder.builder(name).build(), TestType.Container)
            .build { SuiteScope(this).test() }
      )
   }

   @TestRunnable
   fun test(name: String, test: suspend TestScope.() -> Unit) {
      add(
         TestDefinitionBuilder
            .builder(TestNameBuilder.builder(name).build(), TestType.Test)
            .build(test)
      )
   }
}

private class SuiteScope(delegate: TestScope) : AbstractTestScope(delegate) {

   @TestRunnable
   suspend fun suite(name: String, test: suspend SuiteScope.() -> Unit) {
      registerTest(
         TestDefinitionBuilder
            .builder(TestNameBuilder.builder(name).build(), TestType.Container)
            .build { SuiteScope(this).test() }
      )
   }

   @TestRunnable
   suspend fun test(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(
         TestDefinitionBuilder
            .builder(TestNameBuilder.builder(name).build(), TestType.Test)
            .build(test)
      )
   }
}

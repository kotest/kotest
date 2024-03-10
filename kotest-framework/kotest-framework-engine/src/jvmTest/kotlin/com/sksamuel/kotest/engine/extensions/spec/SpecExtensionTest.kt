package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.annotation.Isolate
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.extensions.MountableExtension
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.extensions.install
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.extensions.ExtensionException.BeforeSpecException
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.inspectors.forOne
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe

@Isolate
class SpecExtensionTest : FunSpec() {
   init {

      test("SpecExtension should be invoked IsolationMode.Single") {
         var count = 0

         val ext = object : SpecExtension {
            override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
               execute(spec)
               count++
            }
         }

         val conf = ProjectConfiguration()
         conf.registry.add(ext)
         conf.includePrivateClasses = true

         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(SpecInterceptSingleInstance::class)
            .withConfiguration(conf)
            .launch()

         count shouldBe 1
      }

      test("SpecExtension should be invoked for each instance created") {
         var count = 0

         val ext = object : SpecExtension {
            override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
               execute(spec)
               count++
            }
         }

         val conf = ProjectConfiguration()
         conf.registry.add(ext)
         conf.includePrivateClasses = true

         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(SpecInterceptInstancePerTest::class)
            .withConfiguration(conf)
            .launch()

         count shouldBe 2
      }

      test("SpecExtension can opt to skip processing") {

         val ext = object : SpecExtension {
            override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {}
         }

         val conf = ProjectConfiguration()
         conf.registry.add(ext)
         conf.includePrivateClasses = true

         val collecting = CollectingTestEngineListener()

         TestEngineLauncher(collecting)
            .withClasses(SpecInterceptInstancePerTest::class)
            .withConfiguration(conf)
            .launch()

         collecting.tests.shouldBeEmpty()
      }

      test("Broken BeforeSpecListener should fail the spec") {
         val collecting = CollectingTestEngineListener()

         TestEngineLauncher(collecting)
            .withClasses(BrokenSpec::class)
            .withConfiguration(ProjectConfiguration().also { it.includePrivateClasses = true })
            .launch()

         collecting.specs.forOne {
            it.key shouldBe BrokenSpec::class
            it.value.errorOrNull shouldBe BeforeSpecException(
               IllegalStateException(BrokenExtension.DUMMY_ERROR_MESSAGE)
            )
         }
      }
   }
}

private class SpecInterceptSingleInstance : FunSpec() {
   init {
      isolationMode = IsolationMode.SingleInstance
      test("a") {}
      test("b") {}
   }
}


private class SpecInterceptInstancePerTest : FunSpec() {
   init {
      isolationMode = IsolationMode.InstancePerTest
      test("a") {}
      test("b") {}
   }
}

private object BrokenExtension: MountableExtension<Unit, Unit>, BeforeSpecListener {
   const val DUMMY_ERROR_MESSAGE = "should fail!"
   override fun mount(configure: Unit.() -> Unit) {}

   override suspend fun beforeSpec(spec: Spec) {
      error(DUMMY_ERROR_MESSAGE)
   }
}


private class BrokenSpec: DescribeSpec({
   isolationMode = IsolationMode.InstancePerTest

   install(BrokenExtension)

   it("it should fail") { // this test is not even run
      true.shouldBeFalse()
   }
})

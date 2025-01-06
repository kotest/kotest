package io.kotest.engine.spec.interceptor

import io.kotest.assertions.all
import io.kotest.assertions.fail
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Ignored
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.engine.extensions.EmptyExtensionRegistry
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.datatest.withData
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.spec.interceptor.ref.IgnoredSpecInterceptor
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

@Isolate
@EnabledIf(LinuxCondition::class)
class IgnoredSpecInterceptorTests : FunSpec({
   context("IgnoredSpecInterceptor should report appropriate reasons when a class is ignored by @Ignored") {
      withData(
         nameFn = { "Interceptor reports: $it" },
         "Disabled by @Ignored" to DefaultIgnoredSpec::class,
         """Disabled by @Ignored(reason="it's a good reason!")""" to ReasonIgnoredSpec::class,
      ) { (expected, kclass) ->

         val listener = TestIgnoredSpecListener()
         IgnoredSpecInterceptor(listener, EmptyExtensionRegistry)
            .intercept(SpecRef.Reference(kclass), object : NextSpecRefInterceptor {
               override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
                  error("boom")
               }
            })

         all(listener) {
            name shouldBe kclass.simpleName
            reason shouldBe expected
         }
      }
   }
})

@Ignored
private class DefaultIgnoredSpec : FunSpec({
   test("boom") { fail("boom") }
})

@Ignored("it's a good reason!")
private class ReasonIgnoredSpec : FunSpec({
   test("boom") { fail("boom") }
})

private class TestIgnoredSpecListener : AbstractTestEngineListener() {
   var name: String = ""
      private set

   var reason: String = ""
      private set

   override suspend fun specIgnored(kclass: KClass<*>, reason: String?) {
      this.name = kclass.simpleName ?: ""
      this.reason = reason ?: ""
   }
}

package io.kotest.engine.spec.interceptor

import io.kotest.assertions.all
import io.kotest.assertions.fail
import io.kotest.common.ExperimentalKotest
import io.kotest.core.annotation.Ignored
import io.kotest.core.config.EmptyExtensionRegistry
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.spec.interceptor.ref.IgnoredSpecInterceptor
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

@OptIn(ExperimentalKotest::class)
@Isolate
class IgnoredSpecInterceptorTests : FunSpec({
   context("IgnoredSpecInterceptor should report appropriate reasons when a class is ignored by @Ignored") {
      withData(nameFn = { "Interceptor reports: $it" },
         "Disabled by @Ignored" to DefaultIgnoredSpec::class,
         """Disabled by @Ignored(reason="it's a good reason!")""" to ReasonIgnoredSpec::class,
      ) { (expected, kclass) ->

         val listener = TestIgnoredSpecListener()
         IgnoredSpecInterceptor(listener, EmptyExtensionRegistry)
            .intercept(SpecRef.Reference(kclass)) { error("boom") }

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

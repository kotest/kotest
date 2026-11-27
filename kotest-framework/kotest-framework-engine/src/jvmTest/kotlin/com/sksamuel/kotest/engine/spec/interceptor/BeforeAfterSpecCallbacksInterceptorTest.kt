package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.extensions.ExtensionException
import io.kotest.engine.extensions.MultipleExceptions
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.NextSpecInterceptor
import io.kotest.engine.spec.interceptor.instance.BeforeAfterSpecCallbacksInterceptor
import io.kotest.engine.test.TestResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class BeforeAfterSpecCallbacksInterceptorTest : FunSpec() {
   init {

      test("a failure returned by next should be reported alongside an afterSpec failure") {

         val downstream = RuntimeException("downstream")
         val next = NextSpecInterceptor { Result.failure<Map<TestCase, TestResult>>(downstream) }

         val spec = CallbackSpec()
         val result = BeforeAfterSpecCallbacksInterceptor(specExtensions).intercept(
            spec,
            SpecRef.Reference(spec::class, spec::class.java.name),
            next,
         )

         val error = result.exceptionOrNull()
         error.shouldBeInstanceOf<MultipleExceptions>()
         error.causes.first() shouldBe downstream
         error.causes[1].shouldBeInstanceOf<ExtensionException.AfterSpecException>()
      }
   }
}

private val specExtensions = SpecExtensions(
   SpecConfigResolver(object : AbstractProjectConfig() {
      override val extensions = listOf(FailingAfterSpecListener)
   }),
   ProjectConfigResolver(),
)

private object FailingAfterSpecListener : AfterSpecListener {
   override suspend fun afterSpec(spec: Spec) {
      error("bang")
   }
}

private class CallbackSpec : FunSpec()

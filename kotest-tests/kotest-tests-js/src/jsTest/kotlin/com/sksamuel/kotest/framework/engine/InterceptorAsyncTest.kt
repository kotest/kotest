@file:Suppress("RUNTIME_ANNOTATION_NOT_SUPPORTED")

package com.sksamuel.kotest.framework.engine

import io.kotest.core.annotation.Issue
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.delay

@Issue("https://github.com/kotest/kotest/issues/3317")
class InterceptorAsyncTest : FunSpec() {
   init {
      extension(SomethingExtensions)
      test("support async in spec intercept") { }
   }
}

object SomethingExtensions : SpecExtension {

   override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
      delay(10)
      execute(spec)
   }
}

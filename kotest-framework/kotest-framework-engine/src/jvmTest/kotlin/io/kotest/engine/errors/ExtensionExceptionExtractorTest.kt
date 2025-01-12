package io.kotest.engine.errors

import io.kotest.core.listeners.ContextAwareAfterProjectListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.extensions.ExtensionException
import io.kotest.matchers.shouldBe
import java.io.IOException

class ExtensionExceptionExtractorTest : FunSpec() {
   init {
      test("AfterProjectException should have context when attached") {
         ExtensionExceptionExtractor.resolve(
            ExtensionException.AfterProjectException(
               IOException("foo"),
               ContextAwareAfterProjectListener("context mccontextface", {}),
            )
         ).first shouldBe "After Project Error: context mccontextface"
      }
   }
}

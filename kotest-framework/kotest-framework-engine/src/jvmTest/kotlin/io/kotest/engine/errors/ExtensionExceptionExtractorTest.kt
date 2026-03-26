package io.kotest.engine.errors

import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.ContextAwareListener
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
               object : AfterProjectListener, ContextAwareListener {
                  override val context: String = "context mccontextface"
                  override suspend fun afterProject() {}
               },
            )
         ).first shouldBe "After Project Error: context mccontextface"
      }
   }
}

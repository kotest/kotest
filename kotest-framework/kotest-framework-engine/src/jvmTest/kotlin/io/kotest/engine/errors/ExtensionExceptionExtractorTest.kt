package io.kotest.engine.errors

import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.ContextAwareListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.extensions.ExtensionException
import io.kotest.engine.extensions.MultipleExceptions
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
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

      test("flatten should unwrap MultipleExceptions into its constituent causes") {
         val a = IOException("a")
         val b = IllegalStateException("b")
         val c = RuntimeException("c")

         ExtensionExceptionExtractor.flatten(MultipleExceptions(listOf(a, b, c))) shouldContainExactly listOf(a, b, c)
      }

      test("flatten should return a non-MultipleExceptions throwable as a single-element list") {
         val t = IOException("boom")
         val result = ExtensionExceptionExtractor.flatten(t)
         result shouldHaveSize 1
         result.single() shouldBeSameInstanceAs t
      }
   }
}

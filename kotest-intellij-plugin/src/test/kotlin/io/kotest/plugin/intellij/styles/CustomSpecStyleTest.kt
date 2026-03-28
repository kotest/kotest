package io.kotest.plugin.intellij.styles

import com.intellij.openapi.application.ApplicationManager
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.plugin.intellij.psi.elementAtLine
import java.nio.file.Paths

/**
 * Tests for [CustomSpecStyle], which recognises calls to `@TestRunnable`-annotated functions
 * with a [String] first parameter as Kotest tests.
 *
 * The test fixture (`customspec.kt`) contains:
 *  - `runTest(name: String, ...)` — annotated with `@TestRunnable`; calls are tests.
 *  - `notAnnotated(name: String, ...)` — not annotated; calls are NOT tests.
 *
 * ```
 * line  1: package io.kotest.samples.gradle
 * line  3: import io.kotest.core.spec.style.CustomSpec
 * line  4: import io.kotest.core.annotation.TestRunnable
 * line  6: @TestRunnable
 * line  7: fun runTest(name: String, action: () -> Unit) { action() }
 * line  9: fun notAnnotated(name: String, action: () -> Unit) { action() }
 * line 11: class CustomSpecExample : CustomSpec() {
 * line 12:    init {
 * line 13:       runTest("a test") {
 * line 14:       }
 * line 15:       notAnnotated("not a test") {
 * line 16:       }
 * line 17:    }
 * line 18: }
 * ```
 */
class CustomSpecStyleTest : LightJavaCodeInsightFixtureTestCase() {

   override fun getTestDataPath(): String =
      Paths.get("./src/test/resources/").toAbsolutePath().toString()

   // Run tests off the EDT so that the K2 analysis reads triggered by reference resolution
   // are not blocked by EDT restrictions.
   override fun runInDispatchThread(): Boolean = false

   fun testAnnotatedFunctionCallIsDetectedAsTest() {
      val psiFiles = myFixture.configureByFiles(
         "/customspec.kt",
         "/io/kotest/core/spec/style/specs.kt",
         "/io/kotest/core/annotation/TestRunnable.kt"
      )

      ApplicationManager.getApplication().runReadAction {
         // Line 13: runTest("a test") {
         val element = psiFiles[0].elementAtLine(13) ?: error("No PSI element on line 13")
         val test = CustomSpecStyle.findAssociatedTest(element)
         test shouldNotBe null
         test!!.name.name shouldBe "a test"
      }
   }

   fun testNonAnnotatedFunctionCallIsNotDetectedAsTest() {
      val psiFiles = myFixture.configureByFiles(
         "/customspec.kt",
         "/io/kotest/core/spec/style/specs.kt",
         "/io/kotest/core/annotation/TestRunnable.kt"
      )

      ApplicationManager.getApplication().runReadAction {
         // Line 15: notAnnotated("not a test") {
         val element = psiFiles[0].elementAtLine(15) ?: error("No PSI element on line 15")
         val test = CustomSpecStyle.findAssociatedTest(element)
         test shouldBe null
      }
   }

   fun testMethodGeneration() {
      CustomSpecStyle.generateTest("MySpec", "my test") shouldBe "test(\"my test\") { }"
   }
}

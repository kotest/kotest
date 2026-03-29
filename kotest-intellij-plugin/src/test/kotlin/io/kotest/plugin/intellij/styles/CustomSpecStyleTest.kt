package io.kotest.plugin.intellij.styles

import com.intellij.openapi.application.ApplicationManager
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.plugin.intellij.psi.elementAtLine
import java.nio.file.Paths

/**
 * Tests for [AbstractSpecStyle], which recognizes calls to `@TestRunnable`-annotated functions
 * with a [String] first parameter as Kotest tests.
 *
 * The test fixture (`customspec.kt`) contains:
 *  - `runTest(name: String, ...)` — annotated with `@TestRunnable`; calls are tests.
 *  - `notAnnotated(name: String, ...)` — not annotated; calls are NOT tests.
 *
 * ```
 * line  1: package io.kotest.samples.gradle
 * line  3: import io.kotest.core.spec.AbstractSpec
 * line  4: import io.kotest.core.spec.style.TestRunnable
 * line  6: @TestRunnable
 * line  7: fun runTest(name: String, action: () -> Unit) { action() }
 * line  9: fun notAnnotated(name: String, action: () -> Unit) { action() }
 * line 11: class CustomSpecExample : AbstractSpec() {
 * line 12:    init {
 * line 13:       runTest("a test") {
 * line 15:       notAnnotated("not a test") {
 * line 20: @TestRunnable
 * line 21: fun context(name: String, action: () -> Unit) { action() }
 * line 23: class NestedCustomSpecExample : AbstractSpec() {
 * line 25:    context("outer") {
 * line 26:       runTest("inner") {
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
         "/io/kotest/core/spec/AbstractSpec.kt",
         "/io/kotest/core/spec/style/TestRunnable.kt"
      )

      ApplicationManager.getApplication().runReadAction {
         // Line 13: runTest("a test") {
         val element = psiFiles[0].elementAtLine(13) ?: error("No PSI element on line 13")
         val test = AbstractSpecStyle.findAssociatedTest(element)
         test shouldNotBe null
         test!!.name.name shouldBe "a test"
      }
   }

   fun testNonAnnotatedFunctionCallIsNotDetectedAsTest() {
      val psiFiles = myFixture.configureByFiles(
         "/customspec.kt",
         "/io/kotest/core/spec/AbstractSpec.kt",
         "/io/kotest/core/spec/style/TestRunnable.kt"
      )

      ApplicationManager.getApplication().runReadAction {
         // Line 15: notAnnotated("not a test") {
         val element = psiFiles[0].elementAtLine(15) ?: error("No PSI element on line 15")
         val test = AbstractSpecStyle.findAssociatedTest(element)
         test shouldBe null
      }
   }

   fun testMethodGeneration() {
      AbstractSpecStyle.generateTest("MySpec", "my test") shouldBe "test(\"my test\") { }"
   }

   fun testNestedTestHasCorrectParent() {
      val psiFiles = myFixture.configureByFiles(
         "/customspec.kt",
         "/io/kotest/core/spec/AbstractSpec.kt",
         "/io/kotest/core/spec/style/TestRunnable.kt"
      )

      ApplicationManager.getApplication().runReadAction {
         // Line 26: runTest("inner") {
         val element = psiFiles[0].elementAtLine(26) ?: error("No PSI element on line 26")
         val test = AbstractSpecStyle.findAssociatedTest(element)
         test shouldNotBe null
         test!!.name.name shouldBe "inner"
         test.parent shouldNotBe null
         test.parent!!.name.name shouldBe "outer"
      }
   }
}

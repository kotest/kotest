// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package io.kotest.plugin.intellij.breadcrumbs

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase

/**
 * Tests for [KotestBreadcrumbsCollector].
 *
 * Each test verifies that the collector:
 *  - correctly identifies Kotest spec files (handlesFile)
 *  - produces the right breadcrumb trail for the caret position (computeCrumbs)
 *
 * Test files are configured inline using `<caret>` as the offset marker.
 * The collector is instantiated directly so tests are independent of plugin registration.
 */
class KotestBreadcrumbsCollectorTest : LightJavaCodeInsightFixtureTestCase() {

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private fun collector() = KotestBreadcrumbsCollector(project)

    /**
     * Configures an in-memory `.kt` file from [code] (which must contain exactly one
     * `<caret>` marker), then asserts that [KotestBreadcrumbsCollector.computeCrumbs]
     * returns crumbs whose texts equal [expectedCrumbs] in order.
     */
    private fun assertCrumbs(code: String, vararg expectedCrumbs: String) {
        val caretOffset = code.indexOf("<caret>")
        check(caretOffset >= 0) { "No <caret> marker in test source" }
        val cleanCode = code.replace("<caret>", "")
        myFixture.configureByText("KotestSpec.kt", cleanCode)
        val crumbs = collector().computeCrumbs(
            myFixture.file.virtualFile,
            myFixture.editor.document,
            caretOffset,
            null,
        )
        assertEquals(expectedCrumbs.toList(), crumbs.map { it.text })
    }

    /** Asserts that [KotestBreadcrumbsCollector.handlesFile] returns [expected] for the given [code]. */
    private fun assertHandles(code: String, expected: Boolean) {
        myFixture.configureByText("KotestSpec.kt", code)
        assertEquals(expected, collector().handlesFile(myFixture.file.virtualFile))
    }

    // -------------------------------------------------------------------------
    // handlesFile
    // -------------------------------------------------------------------------

    fun `test handlesFile returns true for FunSpec`() {
        assertHandles("class MyTests : FunSpec({ })", true)
    }

    fun `test handlesFile returns true for DescribeSpec`() {
        assertHandles("class MyTests : DescribeSpec({ })", true)
    }

    fun `test handlesFile returns true for BehaviorSpec`() {
        assertHandles("class MyTests : BehaviorSpec({ })", true)
    }

    fun `test handlesFile returns true for StringSpec`() {
        assertHandles("class MyTests : StringSpec({ })", true)
    }

    fun `test handlesFile returns true for FreeSpec`() {
        assertHandles("class MyTests : FreeSpec({ })", true)
    }

    fun `test handlesFile returns true for ShouldSpec`() {
        assertHandles("class MyTests : ShouldSpec({ })", true)
    }

    fun `test handlesFile returns true for FeatureSpec`() {
        assertHandles("class MyTests : FeatureSpec({ })", true)
    }

    fun `test handlesFile returns true for WordSpec`() {
        assertHandles("class MyTests : WordSpec({ })", true)
    }

    fun `test handlesFile returns true for ExpectSpec`() {
        assertHandles("class MyTests : ExpectSpec({ })", true)
    }

    fun `test handlesFile returns true for AnnotationSpec`() {
        assertHandles("class MyTests : AnnotationSpec()", true)
    }

    fun `test handlesFile returns false for plain Kotlin class`() {
        assertHandles("class MyTests { }", false)
    }

    fun `test handlesFile returns false for unrelated supertype`() {
        assertHandles("class MyTests : Runnable { override fun run() {} }", false)
    }

    fun `test handlesFile returns false for empty file`() {
        assertHandles("", false)
    }

    fun `test handlesFile returns true when at least one class is a spec`() {
        assertHandles(
            """
            class Helper
            class MyTests : FunSpec({ })
            """.trimIndent(),
            true,
        )
    }

    // -------------------------------------------------------------------------
    // FunSpec
    // -------------------------------------------------------------------------

    fun `test FunSpec top-level test`() {
        assertCrumbs(
            """
            class MyTests : FunSpec({
                test("addition") {
                    <caret>
                }
            })
            """.trimIndent(),
            "MyTests", "addition",
        )
    }

    fun `test FunSpec test inside context`() {
        assertCrumbs(
            """
            class MyTests : FunSpec({
                context("math") {
                    test("addition") {
                        <caret>
                    }
                }
            })
            """.trimIndent(),
            "MyTests", "math", "addition",
        )
    }

    fun `test FunSpec deeply nested contexts`() {
        assertCrumbs(
            """
            class MyTests : FunSpec({
                context("level 1") {
                    context("level 2") {
                        test("the test") {
                            <caret>
                        }
                    }
                }
            })
            """.trimIndent(),
            "MyTests", "level 1", "level 2", "the test",
        )
    }

    fun `test FunSpec caret inside context but not in any test`() {
        assertCrumbs(
            """
            class MyTests : FunSpec({
                context("math") {
                    <caret>
                }
            })
            """.trimIndent(),
            "MyTests", "math",
        )
    }

    fun `test FunSpec caret at spec body level`() {
        assertCrumbs(
            """
            class MyTests : FunSpec({
                <caret>
            })
            """.trimIndent(),
            "MyTests",
        )
    }

    // -------------------------------------------------------------------------
    // DescribeSpec
    // -------------------------------------------------------------------------

    fun `test DescribeSpec describe-it`() {
        assertCrumbs(
            """
            class MyTests : DescribeSpec({
                describe("Calculator") {
                    it("adds numbers") {
                        <caret>
                    }
                }
            })
            """.trimIndent(),
            "MyTests", "Calculator", "adds numbers",
        )
    }

    fun `test DescribeSpec describe-context-it`() {
        assertCrumbs(
            """
            class MyTests : DescribeSpec({
                describe("Calculator") {
                    context("when adding") {
                        it("returns the sum") {
                            <caret>
                        }
                    }
                }
            })
            """.trimIndent(),
            "MyTests", "Calculator", "when adding", "returns the sum",
        )
    }

    fun `test DescribeSpec caret inside describe block`() {
        assertCrumbs(
            """
            class MyTests : DescribeSpec({
                describe("Calculator") {
                    <caret>
                }
            })
            """.trimIndent(),
            "MyTests", "Calculator",
        )
    }

    // -------------------------------------------------------------------------
    // BehaviorSpec
    // -------------------------------------------------------------------------

    fun `test BehaviorSpec given-when-then`() {
        assertCrumbs(
            """
            class MyTests : BehaviorSpec({
                given("a calculator") {
                    `when`("adding two numbers") {
                        then("it returns the sum") {
                            <caret>
                        }
                    }
                }
            })
            """.trimIndent(),
            "MyTests", "a calculator", "adding two numbers", "it returns the sum",
        )
    }

    fun `test BehaviorSpec with and block`() {
        assertCrumbs(
            """
            class MyTests : BehaviorSpec({
                given("setup") {
                    and("additional context") {
                        `when`("action") {
                            then("outcome") {
                                <caret>
                            }
                        }
                    }
                }
            })
            """.trimIndent(),
            "MyTests", "setup", "additional context", "action", "outcome",
        )
    }

    // -------------------------------------------------------------------------
    // StringSpec
    // -------------------------------------------------------------------------

    fun `test StringSpec single test`() {
        assertCrumbs(
            """
            class MyTests : StringSpec({
                "addition works" {
                    <caret>
                }
            })
            """.trimIndent(),
            "MyTests", "addition works",
        )
    }

    fun `test StringSpec multiple tests - caret in second`() {
        assertCrumbs(
            """
            class MyTests : StringSpec({
                "first test" {
                }
                "second test" {
                    <caret>
                }
            })
            """.trimIndent(),
            "MyTests", "second test",
        )
    }

    // -------------------------------------------------------------------------
    // FreeSpec
    // -------------------------------------------------------------------------

    fun `test FreeSpec container-leaf with minus`() {
        assertCrumbs(
            """
            class MyTests : FreeSpec({
                "math operations" - {
                    "addition" {
                        <caret>
                    }
                }
            })
            """.trimIndent(),
            "MyTests", "math operations", "addition",
        )
    }

    fun `test FreeSpec three levels deep`() {
        assertCrumbs(
            """
            class MyTests : FreeSpec({
                "level 1" - {
                    "level 2" - {
                        "level 3" {
                            <caret>
                        }
                    }
                }
            })
            """.trimIndent(),
            "MyTests", "level 1", "level 2", "level 3",
        )
    }

    fun `test FreeSpec caret inside container block`() {
        assertCrumbs(
            """
            class MyTests : FreeSpec({
                "outer" - {
                    <caret>
                }
            })
            """.trimIndent(),
            "MyTests", "outer",
        )
    }

    // -------------------------------------------------------------------------
    // ShouldSpec
    // -------------------------------------------------------------------------

    fun `test ShouldSpec context-should`() {
        assertCrumbs(
            """
            class MyTests : ShouldSpec({
                context("Calculator") {
                    should("add numbers correctly") {
                        <caret>
                    }
                }
            })
            """.trimIndent(),
            "MyTests", "Calculator", "add numbers correctly",
        )
    }

    fun `test ShouldSpec top-level should`() {
        assertCrumbs(
            """
            class MyTests : ShouldSpec({
                should("work") {
                    <caret>
                }
            })
            """.trimIndent(),
            "MyTests", "work",
        )
    }

    // -------------------------------------------------------------------------
    // FeatureSpec
    // -------------------------------------------------------------------------

    fun `test FeatureSpec feature-scenario`() {
        assertCrumbs(
            """
            class MyTests : FeatureSpec({
                feature("user login") {
                    scenario("with valid credentials") {
                        <caret>
                    }
                }
            })
            """.trimIndent(),
            "MyTests", "user login", "with valid credentials",
        )
    }

    // -------------------------------------------------------------------------
    // ExpectSpec
    // -------------------------------------------------------------------------

    fun `test ExpectSpec context-expect`() {
        assertCrumbs(
            """
            class MyTests : ExpectSpec({
                context("Calculator") {
                    expect("add works") {
                        <caret>
                    }
                }
            })
            """.trimIndent(),
            "MyTests", "Calculator", "add works",
        )
    }

    // -------------------------------------------------------------------------
    // WordSpec
    // -------------------------------------------------------------------------

    fun `test WordSpec binary should form`() {
        assertCrumbs(
            """
            class MyTests : WordSpec({
                "Calculator" should {
                    "add numbers" {
                        <caret>
                    }
                }
            })
            """.trimIndent(),
            "MyTests", "Calculator", "add numbers",
        )
    }

    // -------------------------------------------------------------------------
    // Edge cases
    // -------------------------------------------------------------------------

    fun `test non-Kotest file returns empty crumbs`() {
        // computeCrumbs is only called when handlesFile is true, but we verify
        // it gracefully handles any PSI by returning empty when no spec class is found.
        val code = """
            class Plain {
                fun foo() {
                    val x = 1
                }
            }
        """.trimIndent()
        val caretOffset = code.indexOf("val x")
        myFixture.configureByText("Plain.kt", code)
        val crumbs = collector().computeCrumbs(
            myFixture.file.virtualFile,
            myFixture.editor.document,
            caretOffset,
            null,
        )
        // No spec class in scope – the walk-up exits at KtFile with no crumbs added
        assertTrue("Expected no crumbs for plain Kotlin class", crumbs.toList().isEmpty())
    }

    fun `test multiple spec classes in one file`() {
        // Caret is inside the second spec class; breadcrumbs should stop there.
        assertCrumbs(
            """
            class FirstSpec : FunSpec({
                test("first") { }
            })
            class SecondSpec : FunSpec({
                test("second") {
                    <caret>
                }
            })
            """.trimIndent(),
            "SecondSpec", "second",
        )
    }

    fun `test AnnotationSpec shows class name and method name`() {
        // AnnotationSpec uses @Test annotations; AnnotationSpecStyle recognises them as tests.
        assertCrumbs(
            """
            class MyTests : AnnotationSpec() {
                @Test
                fun addition() {
                    val x = 1<caret>
                }
            }
            """.trimIndent(),
            "MyTests", "addition",
        )
    }

    fun `test init body style FunSpec`() {
        // Alternative Kotest style: tests registered inside init { }
        assertCrumbs(
            """
            class MyTests : FunSpec() {
                init {
                    test("via init") {
                        <caret>
                    }
                }
            }
            """.trimIndent(),
            "MyTests", "via init",
        )
    }

    fun `test caret outside all test bodies returns only class name`() {
        assertCrumbs(
            """
            class MyTests : FunSpec({
                test("a") { }
                <caret>
                test("b") { }
            })
            """.trimIndent(),
            "MyTests",
        )
    }

    fun `test test name with spaces and special chars`() {
        assertCrumbs(
            """
            class MyTests : FunSpec({
                test("should handle spaces & special chars!") {
                    <caret>
                }
            })
            """.trimIndent(),
            "MyTests", "should handle spaces & special chars!",
        )
    }
}

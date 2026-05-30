package io.kotest.plugin.intellij.util

import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.psi.extractStringArgForFunctionWithStringAndLambdaArgs
import io.kotest.plugin.intellij.psi.extractStringLiteralFromLhsOfInfixFunction
import io.kotest.plugin.intellij.psi.lambdaBody
import io.kotest.plugin.intellij.psi.lineNumber
import io.kotest.plugin.intellij.styles.BehaviorSpecStyle
import io.kotest.plugin.intellij.styles.DescribeSpecStyle
import io.kotest.plugin.intellij.styles.ExpectSpecStyle
import io.kotest.plugin.intellij.styles.FeatureSpecStyle
import io.kotest.plugin.intellij.styles.FreeSpecStyle
import io.kotest.plugin.intellij.styles.FunSpecStyle
import io.kotest.plugin.intellij.styles.ShouldSpecStyle
import io.kotest.plugin.intellij.styles.SpecStyle
import io.kotest.plugin.intellij.styles.StringSpecStyle
import io.kotest.plugin.intellij.styles.WordSpecStyle
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression

/**
 * Contains information about a data test's tag expression and ancestor context path.
 *
 * @param tag The tag expression for filtering (e.g., `kotest.data.12` or `kotest.data.12 | !kotest.data`)
 * @param ancestorTestPath The path to the ancestor regular context, if the data test is nested inside one.
 *                         This is used in conjunction with the tag to properly filter tests.
 *                         Example: "a context with nested withXXX calls"
 */
data class DataTestInfo(
   val tag: String,
   val ancestorTestPath: String? = null
)

object DataTestUtil {
   val styleToDataTestMethodNames: Map<SpecStyle, Set<String>> = mapOf(
      BehaviorSpecStyle to setOf(
         "withData",
         "withContexts",
         "withGivens",
         "withWhens",
         "withThens",
         "withAnds"
      ),
      DescribeSpecStyle to setOf(
         "withData",
         "withContexts",
         "withDescribes",
         "withIts"
      ),
      ExpectSpecStyle to setOf(
         "withData",
         "withContexts",
         "withExpects"
      ),
      FeatureSpecStyle to setOf(
         "withData",
         "withFeatures",
         "withScenarios"
      ),
      FreeSpecStyle to setOf(
         "withData",
         "withContexts",
         "withTests"
      ),
      FunSpecStyle to setOf(
         "withData",
         "withContexts",
         "withTests"
      ),
      ShouldSpecStyle to setOf(
         "withData",
         "withContexts",
         "withShoulds"
      ),
      StringSpecStyle to setOf(
         "withData",
      ),
      WordSpecStyle to setOf(
         "withData",
         "withWhens",
         "withShoulds"
      )
   )
   private val allDataTestMethodNames = styleToDataTestMethodNames.values.flatten().toSet()

   private val regularContainerMethodNames = setOf(
      // used in multiple specs
      "context", "when", "When",
      // BehaviorSpec
      "Context", "`Context`", "`context`",
      "given", "Given", "`given`", "`Given`",
      "and", "And", "`and`", "`And`",
      "`when`", "`When`",
      // DescribeSpec
      "describe",
      // FeatureSpec
      "feature",
   )


   /**
    * Returns [DataTestInfo] for this data test, or null if this is not a data test or the line number
    * cannot be determined.
    *
    * **Tag expression** ([DataTestInfo.tag]):
    * - Root data tests: `(kotest.data.{lineNumber}) | kotest.data.nonJvm`
    * - Nested inside other data tests: root ancestor tag ANDed with `!kotest.data.{siblingLine}` exclusions
    *   at every nesting level, e.g. `(kotest.data.10 & !kotest.data.15 & !kotest.data.20) | kotest.data.nonJvm`.
    * - The `| kotest.data.nonJvm` suffix supports platforms that cannot attach line-number tags (no stack trace),
    *   allowing users to run all data tests within a block on those targets.
    *
    * **Ancestor test path** ([DataTestInfo.ancestorTestPath]):
    * - Non-null only when the data test is nested inside one or more regular (non-data-test) containers
    *   such as `context("...")` or `describe("...")`. In that case it holds the ` -- ` separated path of
    *   those containers, e.g. `"parent context -- child context"`.
    * - The run producers store this value in the `KOTEST_DATA_TEST_ANCESTOR_PATH` environment variable.
    *   The engine's [io.kotest.engine.test.enabled.TagsEnabledExtension] uses it to allow only the direct
    *   ancestor containers through tag filtering, so the engine can discover the target data test without
    *   also running sibling containers or sibling regular tests at any level.
    *
    * @see <a href="https://github.com/kotest/kotest/blob/081125c5f6b0a90d8201b9acea2b698a388984d6/kotest-intellij-plugin/src/test/resources/data-test-tags/DataTestTagsBehaviorSpec.kt">
    * Example for full details of the various DataTestInfo generated for different data test nesting scenarios</a>
    */
   fun dataTestInfoMaybe(isDataTest: Boolean, currentTestPsi: PsiElement): DataTestInfo? {
      if (!isDataTest) return null
      val thisLine = currentTestPsi.lineNumber() ?: return null

      // Check if this data test is inside a regular container
      val regularAncestorPath = findRegularAncestorPath(currentTestPsi)
      val isInsideRegularContainer = regularAncestorPath != null

      // Build the ancestor chain from current test up to root data test
      // Each entry is (dataTestPsi, lineNumber)
      val ancestorChain = buildAncestorChain(currentTestPsi, thisLine)

      // Build the base tag expression
      val baseTag = if (ancestorChain.size == 1) {
         // No data test ancestors, this is either a root-level data test or a data test inside a regular container
         "kotest.data.$thisLine"
      } else {
         // The root ancestor is the first in the chain
         val rootLine = ancestorChain.first().second

         // Collect all sibling exclusions at every level
         val allExclusions = mutableListOf<Int>()
         for (i in 0 until ancestorChain.size - 1) {
            val (parentPsi, _) = ancestorChain[i]
            val (_, childLine) = ancestorChain[i + 1]
            // Find siblings of the child within this parent
            val siblingLines = findSiblingDataTestLines(parentPsi, childLine)
            allExclusions.addAll(siblingLines)
         }

         if (allExclusions.isEmpty()) {
            // No sibling data tests at any level, just include root
            "kotest.data.$rootLine"
         } else {
            // Include root and exclude all siblings at all levels
            val exclusions = allExclusions.joinToString(" & ") { "!kotest.data.$it" }
            "kotest.data.$rootLine & $exclusions"
         }
      }

      val finalTag = "($baseTag) | kotest.data.nonJvm"

      return DataTestInfo(finalTag, regularAncestorPath)
   }

   /**
    * Finds the path of regular test/context ancestors (like `context("...")`) that contain this data test.
    * Returns the full path (e.g., "a context with nested withData calls") or null if no regular ancestors.
    *
    * Traverses through all ancestors including data test methods to find the full path of regular containers.
    */
   private fun findRegularAncestorPath(dataTestPsi: PsiElement): String? {
      val pathParts = mutableListOf<String>()
      var current: PsiElement? = dataTestPsi.parent

      while (current != null) {
         when (current) {
            is KtCallExpression -> {
               val calleeText = current.calleeExpression?.text
               // Skip data test methods but continue traversing - we want to find regular containers
               // that might encapsulate data tests
               if (calleeText !in allDataTestMethodNames) {
                  // If it's a regular container, extract its name and add to path
                  if (calleeText in regularContainerMethodNames) {
                     val name = extractTestName(current)
                     if (name != null) {
                        pathParts.add(0, name) // Add to front to maintain order from root to leaf
                     }
                  }
               }
            }

            is KtBinaryExpression -> {
               // Handle infix function calls:
               // - FreeSpec style: "container name" - { ... }
               // - WordSpec style: "container name" When { ... }
               val name = extractBinaryExpressionContainerName(current)
               if (name != null) {
                  pathParts.add(0, name) // Add to front to maintain order from root to leaf
               }
            }
         }
         current = current.parent
      }

      return if (pathParts.isEmpty()) null else pathParts.joinToString(" -- ")
   }

   /**
    * Extracts the container name from an infix function call represented as a binary expression.
    * Handles:
    * - FreeSpec style: "my container" - { ... }
    * - WordSpec style: "my container" When { ... }
    */
   private fun extractBinaryExpressionContainerName(binaryExpression: KtBinaryExpression): String? {
      return binaryExpression.extractStringLiteralFromLhsOfInfixFunction(listOf("-", "When"))?.text
   }

   /**
    * Extracts the test name from a regular container call expression.
    * For example, extracts "my test" from `context("my test") { ... }`
    */
   private fun extractTestName(callExpression: KtCallExpression): String? {
      // Try to extract string argument from common test function patterns
      val functionName = callExpression.calleeExpression?.text ?: return null
      return callExpression.extractStringArgForFunctionWithStringAndLambdaArgs(functionName)?.text
   }

   /**
    * Builds the ancestor chain from root to current test.
    * Returns a list of [PsiElement] and [currentLine]) pairs, ordered from root (first) to current test (last).
    */
   private fun buildAncestorChain(currentTestPsi: PsiElement, currentLine: Int): List<Pair<PsiElement, Int>> {
      val chain = mutableListOf<Pair<PsiElement, Int>>()
      chain.add(currentTestPsi to currentLine)

      var current: PsiElement? = currentTestPsi
      while (current != null) {
         val parent = findEnclosingDataTestPsi(current)
         if (parent != null) {
            val parentLine = parent.lineNumber()
            if (parentLine != null) {
               chain.add(0, parent to parentLine) // Add to front to maintain root-first order
            }
         }
         current = parent
      }

      return chain
   }

   /**
    * Finds the enclosing data test PSI element (a parent withXXX).
    * Returns null if this is a root-level data test.
    */
   private fun findEnclosingDataTestPsi(currentTestPsi: PsiElement): PsiElement? {
      var current: PsiElement? = currentTestPsi.parent
      while (current != null) {
         if (current is KtCallExpression) {
            val calleeText = current.calleeExpression?.text
            if (calleeText in allDataTestMethodNames) {
               return current
            }
         }
         current = current.parent
      }
      return null
   }

   /**
    * Finds line numbers of sibling data tests (other withData calls at the same nesting level).
    * Returns line numbers of siblings, excluding this test's own line number.
    */
   private fun findSiblingDataTestLines(parentPsi: PsiElement, thisLine: Int): List<Int> {
      val siblingLines = mutableListOf<Int>()

      findDirectDataTestCallsIn(parentPsi) { callPsi ->
         val line = callPsi.lineNumber()
         if (line != null && line != thisLine) {
            siblingLines.add(line)
         }
      }

      return siblingLines
   }

   /**
    * Finds direct data test call expressions (withXXX) within the given PSI element's lambda body.
    * Only looks at direct children to find sibling calls at the same nesting level.
    */
   private fun findDirectDataTestCallsIn(element: PsiElement, siblingsLineAdder: (PsiElement) -> Unit) {
      // Find the lambda body within the parent data test call
      val lambdaBody = findLambdaBody(element) ?: return

      // Look for direct data test calls within the lambda body
      for (child in lambdaBody.children) {
         if (child is KtCallExpression) {
            val calleeText = child.calleeExpression?.text
            if (calleeText in allDataTestMethodNames) {
               siblingsLineAdder(child)
            }
         }
      }
   }

   /**
    * Finds the lambda body (block expression) within a call expression or binary expression.
    */
   private fun findLambdaBody(element: PsiElement): PsiElement? {
      return when (element) {
         is KtCallExpression -> element.lambdaBody()
         is KtBinaryExpression -> element.lambdaBody()
         else -> null
      }
   }
}

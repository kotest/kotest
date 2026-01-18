package io.kotest.plugin.intellij.util

import com.intellij.psi.PsiElement
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
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtLambdaArgument

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

   /**
    * Returns the data test tag expression for this data test.
    *
    * - For root data tests: returns `"kotest.data.{lineNumber}"`
    * - For nested data tests within other data tests: builds a chain from the root parent
    *   down to this data test, excluding siblings at every level.
    *   This allows running a specific nested, at any level, data test by including all ancestors
    *   (so they execute and discover children) while excluding sibling data test blocks at each level.
    *
    * Example of tags for nested data tests - given the following spec:
    * ```kotlin
    *  1 | class ASpecWithManyDataTests : FunSpec({
    *  2 |
    *  3 |    withData("parent1", "parent2", "parent3") {                                            // -> "kotest.data.3"
    *  4 |       withTests("firstChild1", "firstChild2") {                                           // -> "kotest.data.3 & !kotest.data.7 & !kotest.data.25"
    *  5 |          1 + 1 shouldBe 2
    *  6 |       }
    *  7 |       withData("secondChild1", "secondChild2") {                                          // -> "kotest.data.3 & !kotest.data.4 & !kotest.data.25"
    *  8 |          withData("firstChildOfSecondChild1", "firstChildOfSecondChild2") {               // -> "kotest.data.3 & !kotest.data.4 & !kotest.data.25 & !kotest.data.16 & !kotest.data.19"
    *  9 |             withTests("firstChildOfFirstChildOfSecondChild1", "...") {                    // -> "kotest.data.3 & !kotest.data.4 & !kotest.data.25 & !kotest.data.16 & !kotest.data.19 & !kotest.data.12"
    * 10 |                1 + 1 shouldBe 2
    * 11 |             }
    * 12 |             withTests("secondChildOfFirstChildOfSecondChild1", "...") {                   // -> "kotest.data.3 & !kotest.data.4 & !kotest.data.25 & !kotest.data.16 & !kotest.data.19 & !kotest.data.9"
    * 13 |                1 + 1 shouldBe 2
    * 14 |             }
    * 15 |          }
    * 16 |          withTests("secondChildOfSecondChild1", "secondChildOfSecondChild2") {            // -> "kotest.data.3 & !kotest.data.4 & !kotest.data.25 & !kotest.data.8 & !kotest.data.19"
    * 17 |             1 + 1 shouldBe 2
    * 18 |          }
    * 19 |          withData("thirdChildOfSecondChild1", "thirdChildOfSecondChild2") {               // -> "kotest.data.3 & !kotest.data.4 & !kotest.data.25 & !kotest.data.8 & !kotest.data.16"
    * 20 |             withTests("firstAndOnlyChildOfThirdChildOfSecondChild1", "...") {             // -> "kotest.data.3 & !kotest.data.4 & !kotest.data.25 & !kotest.data.8 & !kotest.data.16"
    * 21 |                1 + 1 shouldBe 2                                                           //    (same as parent - no siblings to exclude at this level)
    * 22 |             }
    * 23 |          }
    * 24 |       }
    * 25 |       withTests("thirdChild1", "thirdChild2") {                                           // -> "kotest.data.3 & !kotest.data.4 & !kotest.data.7"
    * 26 |          1 + 1 shouldBe 2
    * 27 |       }
    * 28 |    }
    * 29 | })
    * ```
    *
    * Returns null early if this is not a data test or line number cannot be determined.
    */
   fun dataTestTagMaybe(isDataTest: Boolean, currentTestPsi: PsiElement): String? {
      if (!isDataTest) return null
      val thisLine = lineNumber(currentTestPsi) ?: return null

      // Build the ancestor chain from current test up to root
      // Each entry is (dataTestPsi, lineNumber)
      val ancestorChain = buildAncestorChain(currentTestPsi, thisLine)

      // If no ancestors, this is a root data test
      if (ancestorChain.size == 1) {
         return "kotest.data.$thisLine"
      }

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

      return if (allExclusions.isEmpty()) {
         // No sibling data tests at any level, just include root
         "kotest.data.$rootLine"
      } else {
         // Include root and exclude all siblings at all levels
         val exclusions = allExclusions.joinToString(" & ") { "!kotest.data.$it" }
         "kotest.data.$rootLine & $exclusions"
      }
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
            val parentLine = lineNumber(parent)
            if (parentLine != null) {
               chain.add(0, parent to parentLine) // Add to front to maintain root-first order
            }
         }
         current = parent
      }

      return chain
   }

   /**
    * Returns the 1-based line number where this test is defined in the source file.
    * Returns null if the line number cannot be determined.
    */
   private fun lineNumber(psi: PsiElement): Int? {
      val file = psi.containingFile ?: return null
      val document = file.viewProvider.document ?: return null
      val offset = psi.textOffset
      // Document line numbers are 0-based, we return 1-based to match source file
      return document.getLineNumber(offset) + 1
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
         val line = lineNumber(callPsi)
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
    * Finds the lambda body (block expression) within a call expression.
    */
   private fun findLambdaBody(callExpression: PsiElement): PsiElement? {
      for (child in callExpression.children) {
         if (child is KtLambdaArgument) {
            val lambda = child.getLambdaExpression()
            return lambda?.bodyExpression
         }
      }
      return null
   }
}

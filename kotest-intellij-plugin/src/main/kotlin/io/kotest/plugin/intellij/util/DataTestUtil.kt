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
    * - For nested data tests within other data tests: returns `"kotest.data.{parentLineNumber} & !kotest.data.{sibling1} & !kotest.data.{sibling2} ..."`.
    *   This allows running a specific nested data test by including the parent
    *   (so it executes and discovers children) while excluding sibling data test blocks except itself
    *
    * Returns null early if this is not a data test or line number cannot be determined.
    */
   fun dataTestTagMaybe(isDataTest: Boolean, currentTestPsi: PsiElement): String? {
      if (!isDataTest) return null
      val thisLine = lineNumber(currentTestPsi) ?: return null

      // Find the enclosing data test call via PSI navigation - if null this is a root data test, return simple tag
      val enclosingDataTestPsi = findEnclosingDataTestPsi(currentTestPsi) ?: return "kotest.data.$thisLine"

      // Nested data test - need to include parent and exclude siblings
      // if no parent line, because at this stage we are sure that this test should have had a parent data test, return simple tag (hopefully this does not happen)
      val parentLine = lineNumber(enclosingDataTestPsi) ?: return "kotest.data.$thisLine"

      // Find sibling data tests (other data tests with the same parent)
      val siblingLines = findSiblingDataTestLines(enclosingDataTestPsi, thisLine)

      return if (siblingLines.isEmpty()) {
         // No sibling data tests, just include parent - effectively running all tests of the parent data test without exclusions
         "kotest.data.$parentLine"
      } else {
         // Include parent and exclude all siblings
         val exclusions = siblingLines.joinToString(" & ") { "!kotest.data.$it" }
         "kotest.data.$parentLine & $exclusions"
      }
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

package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.TestName
import io.kotest.plugin.intellij.TestType
import io.kotest.plugin.intellij.psi.asString
import io.kotest.plugin.intellij.psi.enclosingKtClassOrObject
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList

/**
 * A [SpecStyle] that recognizes any function annotated with `@TestRunnable` as a test runner.
 *
 * A call is treated as a test when:
 *  - The spec class extends AbstractSpec.
 *  - The callee function is annotated with `@TestRunnable`.
 *  - The function has at least one parameter.
 *  - The first parameter is of type `String`.
 *  - The first argument at the call-site is a string literal — its value becomes the test name.
 *
 * Example:
 * ```kotlin
 * @TestRunnable
 * fun myTest(name: String, action: () -> Unit) { action() }
 *
 * class MySpec : CustomSpec() {
 *     init {
 *         myTest("my test name") { /* test body */ }
 *     }
 * }
 * ```
 *
 * Over time, the other spec styles will move into this infrastructure.
 */
object AbstractSpecStyle : SpecStyle {

   override fun fqn() = FqName("io.kotest.core.spec.AbstractSpec")

   override fun specStyleName(): String = "Abstract Spec"

   override fun generateTest(specName: String, name: String): String {
      return "test(\"$name\") { }"
   }

   override fun possibleLeafElements(): Set<String> = setOf("OPEN_QUOTE")

   override fun isTestElement(element: PsiElement): Boolean = test(element) != null

   /**
    * Returns a [Test] when [element] is a [KtCallExpression] whose resolved callee is a
    * `@TestRunnable`-annotated function with a `String` first parameter.
    */
   override fun test(element: PsiElement): Test? = when (element) {
      is KtCallExpression -> element.tryTestRunnableCall()
      else -> null
   }

   /**
    * Returns a [Test] when [element] is the opening quote of the first string argument in a
    * `@TestRunnable` function call, so that gutter icons appear on the correct character.
    */
   override fun test(element: LeafPsiElement): Test? {
      if (element.elementType.toString() != "OPEN_QUOTE") return null
      val stringTemplate = element.parent as? KtStringTemplateExpression ?: return null
      val valueArg = stringTemplate.parent as? KtValueArgument ?: return null
      val argList = valueArg.parent as? KtValueArgumentList ?: return null
      // Only treat the opening quote of the first argument as the canonical leaf
      if (argList.arguments.firstOrNull() != valueArg) return null
      val callExpr = argList.parent as? KtCallExpression ?: return null
      return test(callExpr)
   }

   /**
    * Walks the PSI parent chain looking for the nearest enclosing `@TestRunnable` call,
    * which becomes the parent [Test] of a nested test.
    */
   private fun locateParent(element: PsiElement): Test? {
      val parent = element.parent ?: return null
      return when (parent) {
         is KtCallExpression -> parent.tryTestRunnableCall() ?: locateParent(parent)
         else -> locateParent(parent)
      }
   }

   /**
    * Tries to interpret this [KtCallExpression] as a `@TestRunnable` test call.
    *
    * Resolution order:
    * 1. The callee must be a simple name reference (not a dot-qualified call).
    * 2. The referenced declaration must be a [KtNamedFunction] annotated with `@TestRunnable`.
    * 3. The function's first value parameter must be of type `String`.
    * 4. The first call-site argument must be a non-interpolated or interpolated string literal.
    */
   private fun KtCallExpression.tryTestRunnableCall(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null
      val calleeRef = calleeExpression as? KtNameReferenceExpression ?: return null

      // Resolve to the function declaration via PSI references
      val resolvedFn = calleeRef.reference?.resolve() as? KtNamedFunction ?: return null

      // The function must be annotated with @TestRunnable
      val hasAnnotation = resolvedFn.annotationEntries.any { entry ->
         entry.shortName?.asString() == "TestRunnable"
      }
      if (!hasAnnotation) return null

      // Its first parameter must be String
      val firstParam = resolvedFn.valueParameters.firstOrNull() ?: return null
      if (firstParam.typeReference?.text != "String") return null

      // Extract the first call-site argument as the test name
      val argList = valueArgumentList ?: return null
      val firstArgExpr = argList.arguments.firstOrNull()?.getArgumentExpression()
         as? KtStringTemplateExpression ?: return null
      val testName = firstArgExpr.asString()

      return Test(
         TestName(null, testName.text, testName.interpolated),
         locateParent(this),
         specClass,
         TestType.Container,
         xdisabled = false,
         psi = this
      )
   }
}

package io.kotlintest.plugin.intellij.psi

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtConstructorCalleeExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.psi.KtSuperTypeList
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

/**
 * Finds the enclosing class for the receiver [PsiElement] and then
 * locates the super types. Returns true if a super type corresponds to the given spec style.
 */
fun PsiElement.isInSpecStyle(name: String): Boolean {
  val ktclass = getParentOfType<KtClass>(true)
  if (ktclass != null) {
    if (ktclass.children.isNotEmpty() && ktclass.children[0] is KtSuperTypeList) {
      val ktsuper = ktclass.children[0]
      if (ktsuper.children.isNotEmpty() && ktsuper.children[0] is KtSuperTypeCallEntry) {
        val ktsupercall = ktsuper.children[0]
        if (ktsupercall.children.isNotEmpty() && ktsupercall.children[0] is KtConstructorCalleeExpression) {
          val ktconstructor = ktsupercall.children[0]
          if (ktconstructor.text == name) return true
        }
      }
    }
  }
  return false
}

fun PsiElement.isSingleStringArgList(): Boolean = when (this) {
  is KtValueArgumentList -> children.size == 1 && children[0] is KtValueArgument
  else -> false
}

fun PsiElement.enclosingClassName(): String? {
  val ktclass = getParentOfType<KtClass>(true)
  return ktclass?.fqName?.asString()
}

/**
 * Matches blocks of the form:
 *
 * functionName("some string") { }
 *
 * Eg, can be used to match: given("this is a test") { }
 *
 * @return the string argument of the invoked function
 *
 * @param lefts one or more acceptable names for the left hand side reference
 * @param rights one or more acceptable names for the right hand side reference
 */
fun PsiElement.matchDotExpressionWithReferenceOnBothSides(lefts: List<String>, rights: List<String>): String? {
  when (this) {
    is KtDotQualifiedExpression -> {
      val left = children[0]
      val right = children[1]
      // both sides should be call expressions with a reference name
      if (left is KtCallExpression && left.children[0] is KtReferenceExpression && lefts.contains(left.children[0].text) &&
          right is KtCallExpression && right.children[0] is KtReferenceExpression && rights.contains(right.children[0].text)) {
        return left.children[1].children[0].children[0].children[0].text
      }
    }
  }
  return null
}

/**
 * Matches blocks of the form:
 *
 * functionName("some string") { }
 *
 * Eg, can be used to match: given("this is a test") { }
 *
 * @return the string argument of the invoked function
 *
 * @param names one or more function names to search for
 */
fun PsiElement.matchFunction2WithStringAndLambdaArgs(names: List<String>): String? {
  if (this is KtCallExpression && children.size == 2) {
    if (children[0] is KtNameReferenceExpression && names.contains(children[0].text)
        && children[1] is KtValueArgumentList && children[1].isSingleStringArgList()
        && children[2] is KtLambdaArgument) {
      return children[1].children[0].children[0].children[0].text
    }
  }
  return null
}

/**
 * Matches blocks of the form:
 *
 * "string" infixFunctionName { }
 *
 * Eg, can be used to match: "this test" should { }
 *
 * @return the LHS operand
 *
 * @param names one or more function names to search for
 */
fun PsiElement.matchInfixFunctionWithStringAndLambaArg(names: List<String>): String? {
  if (this is KtBinaryExpression) {
    if (children[0] is KtStringTemplateExpression
        && children[1] is KtOperationReferenceExpression && names.contains(children[1].text)
        && children[2] is KtLambdaExpression) {
      return children[0].children[0].text
    }
  }
  return null
}


fun PsiElement.findReceiverForExtensionFunctionWithLambdaArgument(): String? {
  if (this is KtCallExpression) {
    if (children[0] is KtStringTemplateExpression
        && children[1] is KtLambdaArgument) {
      return children[0].children[0].text
    }
  }
  return null
}
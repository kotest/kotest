package io.kotlintest.plugin.intellij

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.lexer.KtToken
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

fun isIdentifier(element: PsiElement): Boolean {
  val node = element.node
  if (node != null) {
    val elementType = node.elementType
    if (elementType is KtToken) {
      return elementType.toString() == "IDENTIFIER"
    }
  }
  return false
}

fun isInKotlinFile(element: PsiElement): Boolean {
  val file = PsiTreeUtil.getParentOfType(element, KtFile::class.java)
  return file != null
}

fun referenceResolveToMethodContextInvocation(parent: KtSimpleNameExpression) =
    parent.mainReference.resolve()?.getKotlinFqName() == NSPEK_METHOD_CONTEXT_INVOCATION

fun isInsideAnnotatedTestFunction(parent: PsiElement): Boolean =
    parent.getParentOfType<KtNamedFunction>(true)?.
        findAnnotation(NSPEK_TEST_ANNOTATION) != null

private val NSPEK_TEST_ANNOTATION = FqName("com.elpassion.nspek.Test")
private val NSPEK_METHOD_CONTEXT_INVOCATION = FqName("com.elpassion.nspek.NSpekMethodContext.o")
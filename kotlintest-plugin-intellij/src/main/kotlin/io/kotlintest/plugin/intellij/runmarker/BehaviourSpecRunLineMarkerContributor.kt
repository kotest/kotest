package io.kotlintest.plugin.intellij.runmarker

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.util.Function
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList

private fun PsiElement.isBehaviorSpecKeyword(): Boolean = when (text) {
  "given", "Given", "`given`", "`Given`", "then", "Then", "`then`", "`Then`", "when", "When", "`when`", "`When" -> true
  else -> false
}

fun PsiElement.isSingleStringArgList(): Boolean = when (this) {
  is KtValueArgumentList -> children.size == 1 && children[0] is KtValueArgument
  else -> false
}

fun PsiElement.behaviorSpecTestName(): String? {
  if (this is KtCallExpression) {
    if (children[0] is KtNameReferenceExpression && children[0].isBehaviorSpecKeyword()
        && children[1] is KtValueArgumentList && children[1].isSingleStringArgList()
        && children[2] is KtLambdaArgument
        && isInSpecStyle("BehaviorSpec")) {
      return ((children[1] as KtValueArgumentList).children[0] as KtValueArgument).text
    }
  }
  return null
}

fun PsiElement.isBehaviorSpecElement(): Boolean {
  if (this is KtCallExpression) {
    if (children[0] is KtNameReferenceExpression && children[0].isBehaviorSpecKeyword()
        && children[1] is KtValueArgumentList && children[1].isSingleStringArgList()
        && children[2] is KtLambdaArgument
        && isInSpecStyle("BehaviorSpec")) {
      return true
    }
  }
  return false
}

class BehaviourSpecRunLineMarkerContributor : RunLineMarkerContributor() {

  override fun getInfo(element: PsiElement): Info? {
    if (element.isBehaviorSpecElement()) {
      return RunLineMarkerContributor.Info(
          AllIcons.RunConfigurations.TestState.Run,
          Function<PsiElement, String> { "Run test" },
          *ExecutorAction.getActions(0)
      )
    }
    return null
  }
}
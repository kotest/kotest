package io.kotlintest.plugin.intellij.runmarker

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.util.Function
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

class WordSpecRunLineMarkerContributor : RunLineMarkerContributor() {

  override fun getInfo(element: PsiElement): Info? {
//    println(element.toString() + "=" + element.javaClass.canonicalName)
    // detect: "some test name" should { }
    if (element is KtBinaryExpression) {
      val children = element.children
      if (children[0] is KtStringTemplateExpression
          && children[1] is KtOperationReferenceExpression && children[1].text == "should"
          && children[2] is KtLambdaExpression
          && element.isInSpecStyle("WordSpec")) {
        return Info(
            AllIcons.RunConfigurations.TestState.Run,
            Function<PsiElement, String> { "Run test" },
            *ExecutorAction.getActions(0)
        )
      }
    } else if (element is KtCallExpression) {
      val children = element.children
      if (children[0] is KtStringTemplateExpression
          && children[1] is KtLambdaArgument
          && element.isInSpecStyle("WordSpec")) {
        return Info(
            AllIcons.RunConfigurations.TestState.Run,
            Function<PsiElement, String> { "Run test" },
            *ExecutorAction.getActions(0)
        )
      }
    }
    return null
  }
}
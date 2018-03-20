package io.kotlintest.intellij

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement
import com.intellij.util.Function

class KotlinTestRunLineMarkerContributor : RunLineMarkerContributor() {

  val logger = Logger.getInstance("io.kotlintest")

  override fun getInfo(element: PsiElement): Info? {
    val specName = element.asSpecName()
    return specName?.let { name ->
      Info(
          AllIcons.RunConfigurations.TestState.Run,
          Function<PsiElement, String> { "Run ${name.shortNameOrSpecial()}" },
          *ExecutorAction.getActions(0)
      )
    }
  }
}

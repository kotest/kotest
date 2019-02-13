package io.kotlintest.plugin.intellij

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.util.Function
import io.kotlintest.plugin.intellij.psi.BehaviorSpecStyle
import io.kotlintest.plugin.intellij.psi.DescribeSpecStyle
import io.kotlintest.plugin.intellij.psi.ExpectSpecStyle
import io.kotlintest.plugin.intellij.psi.FeatureSpecStyle
import io.kotlintest.plugin.intellij.psi.FreeSpecStyle
import io.kotlintest.plugin.intellij.psi.FunSpecStyle
import io.kotlintest.plugin.intellij.psi.ShouldSpecStyle
import io.kotlintest.plugin.intellij.psi.SpecStyle
import io.kotlintest.plugin.intellij.psi.StringSpecStyle
import io.kotlintest.plugin.intellij.psi.WordSpecStyle

abstract class KotlinTestRunLineMarkerContributor(private val style: SpecStyle) : RunLineMarkerContributor() {

  override fun getInfo(element: PsiElement): Info? {
    if (style.isTestElement(element)) {
      return Info(
          AllIcons.RunConfigurations.TestState.Run,
          Function<PsiElement, String> { "Run test" },
          *ExecutorAction.getActions(0)
      )
    }
    return null
  }
}

class BehaviorSpecRunLineMarkerContributor : KotlinTestRunLineMarkerContributor(BehaviorSpecStyle)
class FunSpecRunLineMarkerContributor : KotlinTestRunLineMarkerContributor(FunSpecStyle)
class ShouldSpecRunLineMarkerContributor : KotlinTestRunLineMarkerContributor(ShouldSpecStyle)
class StringSpecRunLineMarkerContributor : KotlinTestRunLineMarkerContributor(StringSpecStyle)
class WordSpecRunLineMarkerContributor : KotlinTestRunLineMarkerContributor(WordSpecStyle)
class FeatureSpecRunLineMarkerContributor : KotlinTestRunLineMarkerContributor(FeatureSpecStyle)
class DescribeSpecRunLineMarkerContributor : KotlinTestRunLineMarkerContributor(DescribeSpecStyle)
class FreeSpecRunLineMarkerContributor : KotlinTestRunLineMarkerContributor(FreeSpecStyle)
class ExpectSpecRunLineMarkerContributor : KotlinTestRunLineMarkerContributor(ExpectSpecStyle)
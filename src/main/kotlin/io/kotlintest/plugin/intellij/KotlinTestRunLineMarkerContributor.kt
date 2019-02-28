package io.kotlintest.plugin.intellij

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
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
import io.kotlintest.plugin.intellij.psi.enclosingClassOrObjectForClassOrObjectToken
import io.kotlintest.plugin.intellij.psi.isSpecSubclass
import org.jetbrains.kotlin.lexer.KtToken

abstract class KotlinTestRunLineMarkerContributor(private val style: SpecStyle) : RunLineMarkerContributor() {

  override fun getInfo(element: PsiElement): Info? {

    // the docs say to only run a line marker for a leaf
    if (element !is LeafPsiElement) {
      return null
    }

    if (element.elementType !is KtToken) {
      return null
    }

    val ktclass = element.enclosingClassOrObjectForClassOrObjectToken()
    if (ktclass != null) {
      if (ktclass.isSpecSubclass(style)) {
        return Info(
            AllIcons.RunConfigurations.TestState.Run_run,
            Function<PsiElement, String> { "[KotlinTest] ${ktclass.fqName!!.shortName()}" },
            *ExecutorAction.getActions(0)
        )
      }
    }

    val testPath = style.testPath(element)
    if (testPath != null) {
      return Info(
          AllIcons.RunConfigurations.TestState.Run,
          Function<PsiElement, String> { "[KotlinTest] $testPath" },
          *ExecutorAction.getActions(0)
      )
    }

    return null
  }
}

class BehaviorSpecRunLineMarkerContributor : KotlinTestRunLineMarkerContributor(BehaviorSpecStyle)
class DescribeSpecRunLineMarkerContributor : KotlinTestRunLineMarkerContributor(DescribeSpecStyle)
class ExpectSpecRunLineMarkerContributor : KotlinTestRunLineMarkerContributor(ExpectSpecStyle)
class FeatureSpecRunLineMarkerContributor : KotlinTestRunLineMarkerContributor(FeatureSpecStyle)
class FreeSpecRunLineMarkerContributor : KotlinTestRunLineMarkerContributor(FreeSpecStyle)
class FunSpecRunLineMarkerContributor : KotlinTestRunLineMarkerContributor(FunSpecStyle)
class ShouldSpecRunLineMarkerContributor : KotlinTestRunLineMarkerContributor(ShouldSpecStyle)
class StringSpecRunLineMarkerContributor : KotlinTestRunLineMarkerContributor(StringSpecStyle)
class WordSpecRunLineMarkerContributor : KotlinTestRunLineMarkerContributor(WordSpecStyle)

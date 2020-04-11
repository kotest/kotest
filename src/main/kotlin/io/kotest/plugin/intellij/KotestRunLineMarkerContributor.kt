package io.kotest.plugin.intellij

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.util.Function
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

abstract class KotestRunLineMarkerContributor(private val style: SpecStyle) : RunLineMarkerContributor() {

  override fun getInfo(element: PsiElement): Info? {

     // the docs say to only run a line marker for a leaf
     if (element !is LeafPsiElement) {
        return null
     }

//     val ktclass = element.enclosingClassOrObjectForClassOrObjectToken()
//     if (ktclass != null) {
//        if (ktclass.isSpecSubclass(style)) {
//           return Info(
//              AllIcons.RunConfigurations.TestState.Run_run,
//              Function<PsiElement, String> { "Run ${ktclass.fqName!!.shortName()}" },
//              *ExecutorAction.getActions(0)
//           )
//        }
//     }

     val test = style.test(element)
     if (test != null) {
        return Info(
           AllIcons.RunConfigurations.TestState.Run,
           Function<PsiElement, String> { "Run ${test.path}" },
           *ExecutorAction.getActions(0)
        )
     }

     return null
  }
}

class BehaviorSpecRunLineMarkerContributor : KotestRunLineMarkerContributor(BehaviorSpecStyle)
class DescribeSpecRunLineMarkerContributor : KotestRunLineMarkerContributor(DescribeSpecStyle)
class ExpectSpecRunLineMarkerContributor : KotestRunLineMarkerContributor(ExpectSpecStyle)
class FeatureSpecRunLineMarkerContributor : KotestRunLineMarkerContributor(FeatureSpecStyle)
class FreeSpecRunLineMarkerContributor : KotestRunLineMarkerContributor(FreeSpecStyle)
class FunSpecRunLineMarkerContributor : KotestRunLineMarkerContributor(FunSpecStyle)
class ShouldSpecRunLineMarkerContributor : KotestRunLineMarkerContributor(ShouldSpecStyle)
class StringSpecRunLineMarkerContributor : KotestRunLineMarkerContributor(StringSpecStyle)
class WordSpecRunLineMarkerContributor : KotestRunLineMarkerContributor(WordSpecStyle)
